package net.itsplace.module.menu;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import net.itsplace.domain.DataTable;
import net.itsplace.domain.ImageFileUpload;
import net.itsplace.domain.JpaPaging;
import net.itsplace.domain.JsonResponse;
import net.itsplace.domain.Place;
import net.itsplace.domain.PlaceEvent;
import net.itsplace.domain.PlaceMenu;
import net.itsplace.domain.PlaceMenu.AddPlaceMenu;
import net.itsplace.domain.PlaceMenu.EditPlaceMenu;
import net.itsplace.module.event.PlaceEventService;
import net.itsplace.service.BaseService;
import net.itsplace.service.PlaceService;
import net.itsplace.user.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PlaceMenuController {
	private static final Logger logger = LoggerFactory.getLogger(PlaceMenuController.class);
	
	@Autowired
	private PlaceMenuService placeMenuService;
	
	@Autowired
    BaseService commonService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	PlaceService placeService;
	@Autowired
	PlaceEventService placeEventService;
	@Autowired
	JsonResponse json;
	private Place place; // 선택된 가맹점 

	/**
	 * 가맹점 메뉴관리 <br> 
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param fid 가맹점 pk
	 * @return DataTables
	 * @throws 
	 * @see a
	 */
	@RequestMapping(value = "/partner/menu/list", method = RequestMethod.GET)
	public String list(@RequestParam(required=false) Integer fid, Model model) {
		
		
		//model.addAttribute("place",getPlace());
		
		return "place/menu/list";
	}
	
	/**
	 * 메뉴목록   <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param iDisplayStart 페이지 번
	 * @param iDisplayLength 페이지 로우수 (한페이지에 보여줄 로우수)
	 * @param iSortCol_0 sort할 컬럼 번호 
	 * @param sSortDir_0 sort할 방향(asc/desc)
	 * @param sSearch 검색
	 * @return DataTables
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value="/partner/getMenuList",method = RequestMethod.GET)
    @ResponseBody
    public DataTable<PlaceMenu> getMenuLList(
    								@RequestParam(required=false, defaultValue="1") Integer iDisplayStart,
    								@RequestParam(required=false) Integer iDisplayLength,
    								@RequestParam(required=false) Integer iSortCol_0, 
    								@RequestParam(required=false) String sSortDir_0, 
                                    @RequestParam(required=false, defaultValue="") String sSearch ) {

                    logger.info("iDisplayStart:{}", iDisplayStart.toString());
                    logger.info("sSortDir_0:{}", sSortDir_0);
                    logger.info("iSortCol_0:{}", iSortCol_0);
                    logger.info("iDisplayLength:{}", iDisplayLength);
                    logger.info("sSearch:{}", sSearch);
                  
                    String columns[] = new String[]{"title", "price","isSale","salePrice"};
                    
                    JpaPaging paging = new JpaPaging(columns,iDisplayStart, iDisplayLength, iSortCol_0, sSortDir_0,sSearch);
                 
                   
                    return  placeMenuService.getMenuList(paging, UserInfo.getFid());
           
                   
    }       
	
	
	/**
	 * 가맹점 메뉴관리
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/partner/menu/add", method = RequestMethod.GET)
	public String add(ModelMap model) {
	//	model.addAttribute("place",getPlace());
		model.addAttribute("placeMenu", new PlaceMenu());

		return "place/menu/add";
	}
	/**
	 * 메뉴  생성 <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/menu/add", method = RequestMethod.POST)
	@ResponseBody
 	public JsonResponse addSubmit(@Validated({AddPlaceMenu.class}) PlaceMenu placeMenu, BindingResult result, Model model) {

		if (result.hasErrors()) {
			 logger.info("필드에러:"+result.getObjectName() +": "+ result.getFieldError().getDefaultMessage());
			 json =  json.getValidationErrorResult(result, json);
			
		}else{	
			Place place = placeService.getPlace(placeMenu.getPlace().getFid());
			placeMenu.setPlace(place);
			logger.info("placeEvent:"+placeMenu.getTitle());
			PlaceMenu saved = placeMenuService.saveMenu(placeMenu);
			json.setSuccess();
			json.setResult(messageSource.getMessage("register", new Object [] {saved.getTitle()} , Locale.getDefault()));
		
		}		
		return json;
	}
	
	/**
	 * 가맹점 메뉴 수정 폼 
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/partner/menu/edit", method = RequestMethod.GET)
	public String edit(@RequestParam(required=true) Integer mnid, ModelMap model) {
		PlaceMenu placeMenu = placeMenuService.getMenu(mnid);
		placeMenu.setFilePath(placeMenu.getHost()+placeMenu.getFilePath());
		model.addAttribute("placeMenu", placeMenu);
		//model.addAttribute("place",getPlace());
		return "place/menu/edit";
	}
	/**
	 * 메뉴  수 <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/menu/edit", method = RequestMethod.POST)
	@ResponseBody
 	public JsonResponse editSubmit(@Validated({EditPlaceMenu.class}) PlaceMenu placeMenu, BindingResult result, Model model) {
		if (result.hasErrors()) {
			 logger.info("필드에러:"+result.getObjectName() +": "+ result.getFieldError().getDefaultMessage());
			 json =  json.getValidationErrorResult(result, json);
			
		}else{	
			Place place = placeService.getPlace(placeMenu.getPlace().getFid());
			//placeEvent.setPlace(place);
			//logger.info("placeEvent:"+placeEvent.getTitle());
			PlaceMenu db = placeMenuService.getMenu(placeMenu.getMnid());
		    db.setTitle(placeMenu.getTitle());
		    db.setPrice(placeMenu.getPrice());
		    db.setPlace(place);
		    PlaceMenu saved = placeMenuService.editMenu(db);
			json.setSuccess();
			json.setResult(messageSource.getMessage("register", new Object [] {saved.getTitle()} , Locale.getDefault()));
		
		}		
		return json;
	}
	
	/**
	 * 메뉴  삭제  <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/menu/delete", method = RequestMethod.GET)
	@ResponseBody
 	public JsonResponse delete(@RequestParam(required=true) Integer mnid, Model model) {
		JsonResponse json = new JsonResponse();
		try{
			placeMenuService.deleteMenu(mnid);
			json.setStatus("SUCCESS");
		}catch(Exception e){
			json.setStatus("FAIL");
		}
		
		return json;
	}

	/**
	 *메뉴  사진 업로드  <br />
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/menuUpload", method = RequestMethod.POST)
 	public void placeFileUpload(ImageFileUpload file, BindingResult result, Model model, HttpServletResponse response) throws Exception {
		logger.info("filename:{}",file.getFile().getOriginalFilename());
		
		
		String resultJson = "";
		
			file.setFid(UserInfo.getFid());
			
			PlaceMenu placeMenu = placeMenuService.savePlaceMenuImage(file);
		//	logger.info(commonService.getBasecd().getImageHost()+placeMenu.getFilePath());
		//	resultJson ="{mnid:'"+placeMenu.getMnid()+"',fileName:'"+commonService.getBasecd().getImageHost()+placeMenu.getFilePath()+"'}";	
		
		 response.setContentType("text/html");
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 
		 out.write(resultJson.getBytes());
		 response.setContentLength(out.size());
		 
		 response.getOutputStream().write(out.toByteArray());
		 response.getOutputStream().flush();
	}
//	private Place getPlace(){			
//		if(place == null){
//			this.place = adminPlaceService.getPlace(UserInfo.getFid());
//		}else if(place.getFid() != UserInfo.getFid()){
//			this.place = adminPlaceService.getPlace(UserInfo.getFid());
//		}
//		return this.place;
//	}
}
