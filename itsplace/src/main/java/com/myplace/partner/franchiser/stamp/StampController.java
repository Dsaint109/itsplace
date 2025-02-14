package com.myplace.partner.franchiser.stamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myplace.common.CommonService;
import com.myplace.partner.franchiser.FranchiserController;
import com.myplace.partner.franchiser.FranchiserMember;
import com.myplace.partner.franchiser.FranchiserService;
import com.myplace.user.User;
import com.myplace.user.UserService;
import com.myplace.util.Encrypt;
import com.myplace.util.PagingManager;
import com.myplace.util.StandardOrMobile;

@Controller
public class StampController {
	private static final Logger logger =  LoggerFactory.getLogger(StampController.class);
	@Autowired
	private StampService stampService;
	
	private PagingManager pagingManaer;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FranchiserService franchiserService;
	/**
	 * <pre>
	 *	모바일 사용자가 스탬프 등록 화면 호출				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10. 4.
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/stampByUser/{fid}/{stampType}/{eventday}/{fname}", method = RequestMethod.GET)
 	public String  stampByUser(Model model,
 			@PathVariable String fid,
 			@PathVariable String eventday,
 			@PathVariable String stampType,
 			@PathVariable String fname){
		
		model.addAttribute("fid",fid);
		model.addAttribute("fname",fname);
		model.addAttribute("eventday",eventday);
		model.addAttribute("stamptype",stampType);
		return "stamp/m_stampByUser";
	}
	/**
	 * <pre>
	 *	모바일 사용자의 휴대폰으로 가맹점 인증코드를 입력받아 (fCODE 입력하여) 스탬프 등록	
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2012. 03.05
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/saveStampJson", method = RequestMethod.POST)
 	public @ResponseBody String saveStampJson(Stamp stamp, HttpServletRequest request) {

			return stampService.saveStamp(stamp);

	
	}
	/**
	 * <pre>
	 *	모바일 사용자의 휴대폰으로 가맹점 인증코드를 입력받아  스탬프를 사용한다(소진)	
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2012. 03.05
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/useStampJson", method = RequestMethod.POST)
 	public @ResponseBody String useStampJson(Stamp stamp, HttpServletRequest request) {
		
		FranchiserMember fMember = null;
		fMember = franchiserService.getFranchiserFcode(stamp.getFcode());
		if(fMember == null){
			logger.info("인증번호가 틀력씁니다:{0}",stamp.getFcode());
			return "false";
		}else{
			List<Stamp> stampList = stampService.getUserStampListByFcode(stamp);
			String eventDay = stampList.get(0).getEventday();
			if(stampList.size()>=Integer.parseInt(eventDay)){
				for(int i=0; i<Integer.parseInt(eventDay);i++){
					stampService.updateStamp(stamp);
				}
				return "true";
			}else{
				return "false";
			}
		}
		
	}
	/**
	 * <pre>
	 *	모바일 사용자의 휴대폰으로 스탬프 등록	
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10. 4.
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/stampByUser/register", method = RequestMethod.POST)
	public String  stampByUserRegister(Model model, Stamp stamp,HttpServletRequest request){
		String result;
		User user = (User)request.getSession().getAttribute("USERSESSION");
		
		stamp.setEmail(user.getEmail());
		stamp.setMobile(user.getMobile());
		
		logger.info(stamp.toString());
		
		User stampUser = userService.getUser(Integer.toString(stamp.getFid()));//가맹점 정보
		
		if(stampUser.getPassword().equals(Encrypt.md5Encoding(stamp.getStampPassword())) ){
			
			if(stamp.getEventday()==null || stamp.getEventday().equals("none")){
				stampService.saveStamp(stamp);
				result = "스탬프를 등록하였습니다";
			}else{
				////이벤트와 등록을 동시에 
				stampService.saveStamp(stamp);
				result = "스탬프를 등록하고 사용하였씁니다";
				
			}
			
		}else{
			logger.info("비밀번호가 틀렸습니다");
			result = "비밀번호가 틀렸습니다";
		}
		
		model.addAttribute("result",result);
		model.addAttribute("returnUrl","/place/placeView/"+stamp.getFid());
		
		return "stamp/m_stampByUserResult";
		
		
	}
	

	/**
	 * <pre>
	 *	Ajax스탬프 휴대폰 번호로 등록
	 *  가맹점이 휴대폰 번호로 사용자를 조회하여 스탬프를 등록하고 휴대폰번호가 없다면 아이디 없이 등록하고 차후 회원가입후 매칭				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param 
	 * @return  가맹점 신청(가입) 페이지
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/register/{mobile}/stamptype/{stamptype}", method = RequestMethod.GET)
 	public  @ResponseBody String  register( 
 			@PathVariable String mobile,
 			@PathVariable String stamptype) {
			
		User user = null;
		Stamp stamp = null;
		String success="";
		if(mobile!="" && stamptype!=""){
			
			user = userService.getUserByMobile(mobile);
			
			if(user!=null){
				stamp = new Stamp();
				stamp.setEmail(user.getEmail());
				stamp.setStamptype(stamptype);		
				stamp.setMobile(mobile);
				stamp.setFid( Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName()) );
				success = user.getName() +" ("+ user.getMobile()+") 님의 스탬프 등록";							
			}else{
				stamp = new Stamp();			
				stamp.setStamptype(stamptype);
				stamp.setMobile(mobile);
				stamp.setFid( Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName()) );
				stamp.setMobile(mobile);
				success = "등록되지 않은" +" ("+ mobile+") 님의 스탬프 등록";
			}
			try{
				stampService.saveStamp(stamp);
			}catch(Exception e){
				return "스탬프 등록 실패 e.toString()";
			}
		}else{
			return "휴대폰번호 또는 스탬프가 설정되지 않았습니다";
		}
		return success;
	}
	
	/**
	 * <pre>
	 *	가맹점(사용자)이 휴대폰 번호로 스팸프 조회(사용자 및 스탬프) 
	 *  				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10. 1.
	 * @param 
	 * @return 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/mobile", method = RequestMethod.GET)
 	public  String  mobile(Model model,Stamp stamp,Device device,SitePreference sitePreference) {
	//	logger.info("ddddddddd:" + mobile +stamptype);
		User user = userService.getUserByMobile(stamp.getMobile());
		if(user != null){
			model.addAttribute("user",user);
		}
		
		Stamp stamptypeResister  = stampService.getStamptypeRegister(stamp.getFid());
		List<Stamp> stampListAll = stampService.getUserStampListByMobile(stamp);
		if(stampListAll==null){
			logger.info("스태프가 없습니다");
			//return "franchiser/fid";
		}
		int totalCount = commonService.getFoundRows();
		int size = Integer.parseInt(stamptypeResister.getStampcount());
		int couponCount = (totalCount/size)+(totalCount%size==0?0:1);		
		int startRow = 0;
		int endRow = 0;
		
		List<Object> stamppedListAll = new ArrayList<Object>();
		
		for(int j =1; j<couponCount+1; j++){
			if(j==1){
				startRow = 0;
			}else{
				startRow = endRow; 
			}
			endRow = size * j;
			logger.info("쿠폰갯수:"+couponCount +"srartRow:" + startRow + ", endRow:"+ endRow);
			
			stamp.setStartRow(startRow);
			stamp.setEndRow(endRow);
			List<Stamp> stampList = stampService.getUserStampListByMobile(stamp);
			
			
			List<Stamp> stamppedList = new ArrayList<Stamp>();			
			for(int i=1; i< Integer.parseInt(stamptypeResister.getStampcount())+1;i++ ){
					 
				 if(i<stampList.size()+1){
					 Stamp stampped = (Stamp)stampList.get(i-1);
					 if(i % Integer.parseInt(stamptypeResister.getEventday())==0){
						 stampped.setEventday("eventday");
					 }
					 if(stampped.getEventday()==null || stampped.getEventday().equals("")){
						 stampped.setEventday("stampped"); 
					 }else{
						 stampped.setEventday("stamppedEvent"); // 스탬프와 이벤트(스탬프까지 사용함)
					 }
					 stamppedList.add(stampped);
				 }else{
					 logger.info("인덱스:"+i + "data:blank");
						
					 Stamp blank = new Stamp();
					 if(i % Integer.parseInt(stamptypeResister.getEventday())==0){
						 blank.setEventday("eventday");
					 }
					 stamppedList.add(blank);
				 }
				
			}
			
			stamppedListAll.add(stamppedList);
		}
		 
		//stampList.size()/stamptypeResister.getStampcount()
		
		
		model.addAttribute("mobile",stamp.getMobile());				
		model.addAttribute("stamptypeRegister",stamptypeResister);				
		model.addAttribute("stamppedListAll",stamppedListAll);
		model.addAttribute("franchiserMember",stamptypeResister);				
		
		if(stamp.getUrlType().equals("myStamp")){
			
			return StandardOrMobile.getPageName(device, sitePreference, "stamp/m_myStamp", "stamp/myStamp");						
			
		}else{
			return "franchiser/fid";
		}
		
 			
	}
	
	/**
	 * <pre>
	 *	Ajax스탬프 취소(삭제
	 *  				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10. 3.
	 * @param 
	 * @return 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/delete/{pid}", method = RequestMethod.GET)
 	public  @ResponseBody String  delete( 
 			@PathVariable String pid) {
		
		try{
			stampService.deleteStamp(pid);
			return "succes";
		}catch(Exception e){
			return "false";
		}
		
	
	}
	
	/**
	 * <pre>
	 *	사용자의 스탬프 리스트를 가져와서 Iframe으로 처리 
	 *  				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10. 1.
	 * @param 
	 * @return 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/stamp/myStamp", method = RequestMethod.GET)
 	public  String  myStamp(Model model,Device device,SitePreference sitePreference) {
		
		Stamp stamp = new Stamp();
		stamp.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		List<Stamp> stampList = stampService.getUserStampListByEmail(stamp);
		
		model.addAttribute("myStampList",stampList);
		
		

		return StandardOrMobile.getPageName(device, sitePreference, "stamp/m_myStampList",  "stamp/myStampList");						
			
	}
	
	@RequestMapping(value = "/stamp/myStampJson",  method = RequestMethod.GET)
	public  @ResponseBody List<Stamp> myStampJson(@RequestParam String email){		
		Stamp stamp = new Stamp();
		stamp.setEmail(email);
		
		List<Stamp> stampList = stampService.getUserStampListByEmail(stamp);
	
		return stampList;
	}
	
	@RequestMapping(value = "/stamp/myStampByFidJson",  method = RequestMethod.GET)
	public  @ResponseBody List<Stamp> myStampByFidJson(@RequestParam String email, @RequestParam String fid){		
		Stamp stamp = new Stamp();
		stamp.setEmail(email);
		stamp.setFid(Integer.parseInt(fid));
		List<Stamp> stampList = stampService.getUserStampListByFid(stamp);
	
		return stampList;
	}
	
	 private static List<State> states;
	  @RequestMapping(value = "statesj", method = RequestMethod.GET)
	    public @ResponseBody List<State> fetchStatesJson() {
	        logger.info("fetching JSON states");
	        return getStates();
	    }
	  private List<State> getStates() {
	        if (states == null) {
	            states = new ArrayList<State>();
	            states.add(new State("ALABAMA", "AL"));
	            states.add(new State("ALASKA", "AK"));
	            states.add(new State("ARIZONA", "AZ"));
	            states.add(new State("ARKANSAS", "AR"));
	            states.add(new State("CALIFORNIA", "CA"));
	            states.add(new State("COLORADO", "CO"));
	            states.add(new State("CONNECTICUT", "CT"));
	            states.add(new State("DELAWARE", "DE"));
	            states.add(new State("FLORIDA", "FL"));
	            states.add(new State("GEORGIA", "GA"));
	            states.add(new State("HAWAII", "HI"));
	            states.add(new State("IDAHO", "ID"));
	            states.add(new State("ILLINOIS", "IL"));
	            states.add(new State("INDIANA", "IN"));
	            states.add(new State("IOWA", "IA"));
	            states.add(new State("KANSAS", "KS"));
	            states.add(new State("KENTUCKY", "KY"));
	            states.add(new State("LOUISIANA", "LA"));
	            states.add(new State("MAINE", "ME"));
	            states.add(new State("MARYLAND", "MD"));
	            states.add(new State("MASSACHUSETTS", "MA"));
	            states.add(new State("MICHIGAN", "MI"));
	            states.add(new State("MINNESOTA", "MN"));
	            states.add(new State("MISSISSIPPI", "MS"));
	            states.add(new State("MISSOURI", "MO"));
	            states.add(new State("MONTANA", "MT"));
	            states.add(new State("NEBRASKA", "NE"));
	            states.add(new State("NEVADA", "NV"));
	            states.add(new State("NEW HAMPSHIRE", "NH"));
	            states.add(new State("NEW JERSEY", "NJ"));
	            states.add(new State("NEW MEXICO", "NM"));
	            states.add(new State("NEW YORK", "NY"));
	            states.add(new State("NORTH CAROLINA", "NC"));
	            states.add(new State("NORTH DAKOTA", "ND"));
	            states.add(new State("OHIO", "OH"));
	            states.add(new State("OKLAHOMA", "OK"));
	            states.add(new State("OREGON", "OR"));
	            states.add(new State("PENNSYLVANIA", "PA"));
	            states.add(new State("RHODE ISLAND", "RI"));
	            states.add(new State("SOUTH CAROLINA", "SC"));
	            states.add(new State("SOUTH DAKOTA", "SD"));
	            states.add(new State("TENNESSEE", "TN"));
	            states.add(new State("TEXAS", "TX"));
	            states.add(new State("UTAH", "UT"));
	            states.add(new State("VERMONT", "VT"));
	            states.add(new State("VIRGINIA", "VA"));
	            states.add(new State("WASHINGTON", "WA"));
	            states.add(new State("WEST VIRGINIA", "WV"));
	            states.add(new State("WISCONSIN", "WI"));
	            states.add(new State("WYOMING", "WY"));
	        }

	        return states;
	    }
	
}
