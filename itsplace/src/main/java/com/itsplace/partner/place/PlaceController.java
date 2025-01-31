/**
 * 
 */
package com.itsplace.partner.place;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.myplace.common.Address;
import com.myplace.common.CommonService;
import com.myplace.partner.franchiser.FranchiserMember;
import com.myplace.partner.franchiser.FranchiserService;
import com.myplace.partner.franchiser.image.FranchiserImage;
import com.myplace.partner.franchiser.image.FranchiserImageService;
import com.myplace.partner.franchiser.stamp.Stamp;
import com.myplace.partner.franchiser.stamp.StampService;
import com.myplace.user.User;
import com.myplace.util.Encrypt;
import com.myplace.util.PagingManager;
import com.myplace.util.StandardOrMobile;

/**
 * 
 * @author faye12005
 * 가맹점 검색 , 리뷰, 코멘트 달
 */
@Controller
public class PlaceController {
        
	private static final Logger logger =  LoggerFactory.getLogger(PlaceController.class);
	@Autowired
	private FranchiserService fService;	
	private PagingManager pagingManaer;	
	@Autowired
	private CommonService commonService;

	@Autowired
	private StampService stampService;
	
	@Autowired
	private FranchiserImageService franchiserImageService;
	
	@Autowired
	private PlaceService placeService;
	
	public String getUserEmail(){
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication auth = securityContext.getAuthentication();
		return auth.getName();
	}
	/**
	 * 가맹점 뷰어 <br>
	 * 가맹점 코드로 가맹점을 조회한
	 * <pre>
	 * Ex.		
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 9. 22.
	 * @param 가맹점 아이디 
	 * @param 클라이언트 구분 
	 * @return placeView.jsp 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value="/place/placeView/{fid}", method = RequestMethod.GET )
	public  String placeView(Model model,
			 @PathVariable int fid,HttpServletRequest request,Device device,SitePreference sitePreference){				
		
		FranchiserMember franchiserMember = fService.getFranchiserMember(fid);		
		List<PlaceComment> placeCommentList = fService.getPlaceCommentList(fid);	
		
		User user = (User)request.getSession().getAttribute("USERSESSION");
		
		if (user != null){
			if(!franchiserMember.getStype().trim().equals("N")){
				Stamp stamp = new Stamp();
				stamp.setFid(fid);
				stamp.setMobile(user.getMobile());
				Stamp stamptypeResister = stampService.getStamptypeRegister(fid);
				if(stamptypeResister != null){
					stamp.setStamptype(stamptypeResister.getStamptype());
					
					List<Stamp> stampListAll = stampService.getUserStampListByMobile(stamp);
					int totalCount = 0;
					
					if(stampListAll.size()==0){
						logger.info("등록된 스태프가 없습니다");
						totalCount = 1;
						
						//return "franchiser/fid";
					}
					totalCount = commonService.getFoundRows();
					int size = Integer.parseInt(stamptypeResister.getStampcount());
					int couponCount = (totalCount/size)+(totalCount%size==0?0:1);
					if(couponCount==0){
						couponCount = 1;
					}
					int startRow = 0;
					int endRow = 0;
					logger.info("size:"+size+ " 쿠폰갯수:"+couponCount +"srartRow:" + startRow + ", endRow:"+ endRow);
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
								 	
								 Stamp blank = new Stamp();
								 if(i % Integer.parseInt(stamptypeResister.getEventday())==0){
									 blank.setEventday("eventday");
								 }
								 stamppedList.add(blank);
							 }
							
						}
						
						stamppedListAll.add(stamppedList);
						model.addAttribute("stamptypeRegister",stamptypeResister);				
						model.addAttribute("stamppedListAll",stamppedListAll);
					}
				}
			}else{
				logger.info("franchiserMember.getStype():서비스에 가입되지 않았습니다 서비스타입:" + franchiserMember.getStype() );
			}
			
		}else{
			logger.info("사용자 권한이 없습니다. 세션 이즈 널:" );
			
		}
		FranchiserImage franchiserImage = new FranchiserImage();
		franchiserImage.setFid(fid);
		List<FranchiserImage> franchiserImageList = franchiserImageService.getFranchiserImageList(franchiserImage);
		
		model.addAttribute("franchiserMember", franchiserMember);
		model.addAttribute("placeCommentList", placeCommentList);
		model.addAttribute("franchiserImageList", franchiserImageList);
		
		
		return StandardOrMobile.getPageName(device, sitePreference, "place/m_placeView", "place/placeView");						
		
		
		
	
	}
	
	/**
	 * 가맹점 코멘트 등 <br>
	 * 사용자들이 가맹점에 대해 리뷰 , 코멘트를 등록한다 
	 * <pre>
	 * Ex.		
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 9. 22.
	 * @param {@link PlaceComment} 
	 * @param 클라이언트 구분 
	 * @return redirect:/place/placeView/가맹점코
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/place/newPlaceReview", method = RequestMethod.POST)
	public String newPlaceReviewSubmit(@Valid PlaceComment placeComment, BindingResult result,Model model,Principal principal) {
			          
		if (result.hasErrors()) {			
			logger.info("신규 가맹점 리뷰 등록 에러발생:{}",result.getFieldError().getDefaultMessage());		
			return "franchiser/newFranchiser";			
		}else {										
			placeComment.setEmail( principal.getName());
			
			fService.savePlaceComment(placeComment);		
			model.addAttribute("placeComment",placeComment);
			
			logger.info("신규 가맹점 리뷰 등록: {}",placeComment.toString());
			
			return  "redirect:/place/placeView/" + placeComment.getFid();
		}
	
	}
	/**
	 * 가맹점 코멘트 삭제  <br>
	 * 코멘트는 본인 또는 관리자만 삭제 할 수 있
	 * <pre>
	 * Ex.		
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 9. 22.
	 * @param {@link PlaceComment} 
	 * @param 클라이언트 구분 
	 * @return redirect:/place/placeView/가맹점코드 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/place/deletePlaceReview", method = RequestMethod.POST)
	public String deletePlaceReview(PlaceComment placeComment) {
		placeComment.setEmail(getUserEmail());
		fService.deletePlaceComment(placeComment);
		
		return  "redirect:/place/placeView/" + placeComment.getFid();
		
		
	}
	
	/**
	 * <pre>
	 * 모바일 가맹점 리스트				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 21.
	 * @param 
	 * @return  가맹점아이디
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/placebylocation/lat/{latitude}/lot/{longitude}", method = RequestMethod.GET)
	public  String  placebylocation(Model model,
			@PathVariable String latitude,
			@PathVariable String longitude){
		
		FranchiserMember f = new FranchiserMember();
		
		f.setLatitude(latitude);
		f.setLongitude(longitude);
		
		logger.info("좌표:"+latitude+" :: "+longitude);
		
		List<FranchiserMember> franchiserMemberList  = fService.getFranchiserMemberByLocation(f);
		if(franchiserMemberList == null){
		
		}
		model.addAttribute("franchiserMemberList",franchiserMemberList);
		model.addAttribute("latitude",latitude);
		model.addAttribute("longitude",longitude);
		return "place/m_placebylocation";
		
	}
	/**
	 * <pre>
	 * 모바일 가맹점 동이름으로 검색 				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 21.
	 * @param 
	 * @return  가맹점아이디
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/getPlaceListByDong/{searchWord}", method = RequestMethod.GET)
	public  String  getPlaceListByDong(Model model,
			@PathVariable String searchWord){
		
		Map<String, Object> param = new HashMap<String, Object> ();
		param.put("searchWord", searchWord);
		
		
		List<FranchiserMember> franchiserMemberList = placeService.getPlaceListByDong(param);
		
		model.addAttribute("franchiserMemberList",franchiserMemberList);
		return "place/m_placebylocation";
		
	}
	/**
	 * <pre>
	 * 모바일 가맹점검색 				
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 21.
	 * @param 
	 * @return  가맹점아이디
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/search", method = RequestMethod.GET)
	public  String  search(Model model){
		
		
		return "place/m_search";
		
	}
}