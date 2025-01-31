package net.itsplace.controller.partner;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.itsplace.domain.Authcode;
import net.itsplace.domain.Bascd.EditBascd;
import net.itsplace.domain.ImageFileUpload;
import net.itsplace.domain.JsonResponse;
import net.itsplace.domain.Place;
import net.itsplace.service.BaseService;
import net.itsplace.service.MediaService;
import net.itsplace.service.PlaceService;
import net.itsplace.service.PlaceService;
import net.itsplace.service.StampBaseService;
import net.itsplace.user.UserInfo;
import net.itsplace.util.ImageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PlaceInfoController {

	private static final Logger logger = LoggerFactory.getLogger(PlaceInfoController.class);
	@Autowired
	private PlaceService adminPlaceService;
	@Autowired
	private StampBaseService adminStampService;
	@Autowired
	private MediaService adminMediaService;
	@Autowired
	private BaseService commonService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private PlaceService placeInfoService;
	
	@RequestMapping(value = "/partner/edit", method = RequestMethod.GET)
	public String placeInfo(Model model) {
		model.addAttribute("place",adminPlaceService.getPlace(UserInfo.getFid()));
		model.addAttribute("stampTypeList",adminStampService.getStampTypeListAll());
		model.addAttribute("categoryList",commonService.getBascdList("CATEGORY"));
		model.addAttribute("placeTypeList",commonService.getBascdList("PLACETYPE"));
		model.addAttribute("serviceTypeList",commonService.getBascdList("SERVICETYPE"));
		return "place/edit";
	}
	/**
	 * 가맹점 수정  <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/edit", method = RequestMethod.POST)
 	public String placeInfoEdit(@Validated({EditBascd.class}) Place place, BindingResult result, Model model) {
		if (result.hasErrors()) {
			logger.info(result.getObjectName() +": "+ result.getFieldError().getDefaultMessage() +"------------발생");
			return "place/edit";
		} else {	
			adminPlaceService.editPlace(place);
			
			return "place/edit";
		}
	
	}
	/**
	 * 가맹점 대표 사진 업로드  <br />
	 * 이미지는 항상 새로 발생하고(업데이트없음) 대표이미지만 교체한다. 업데이트 없이 삭제로 함.
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/upload", method = RequestMethod.POST)
 	public void placeFileUpload(ImageFileUpload file, BindingResult result, Model model, HttpServletResponse response) throws Exception {
		logger.info("filename:"+file.getFile().getOriginalFilename());
		String resultJson = "";
		if (result.hasErrors()) {
			logger.info(result.getObjectName() +": "+ result.getFieldError().getDefaultMessage() +"------------발생");
		}else{	

			String placeImagePath = adminMediaService.savePlaceMedia(file,UserInfo.getFid());
			//resultJson ="{error: '',fileName:'"+commonService.getBasecd().getMediaImageHost()+placeImagePath+"'}";	
		}
		 response.setContentType("text/html");
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 
		 out.write(resultJson.getBytes());
		 response.setContentLength(out.size());
		 
		 response.getOutputStream().write(out.toByteArray());
		 response.getOutputStream().flush();
	}
	/**
	 * 인증코드  관리 폼  <br />
	 * ROLE_FRANCHISER 권한만 인증코드를 변경할 수 있습니다.
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param 
	 * @return 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/auth", method = RequestMethod.GET)
	public String auth(Model model) {
		model.addAttribute("authcode", new Authcode());
		return "place/auth";
	}
	/**
	 * 가맹점 관리자 인증코드 수정       <br />
	 * ROLE_FRANCHISER 권한만 인증코드를 변경할 수 있습니다.
	 * 가맹점 관리자 인증코드 수정    
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param 
	 * @return 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/auth", method = RequestMethod.POST)
	public @ResponseBody JsonResponse authSubmit(Authcode authcode, BindingResult result, Model model) {
		JsonResponse json = new JsonResponse();
		if (result.hasErrors()) {
			logger.info(result.getObjectName() +": "+ result.getFieldError().getDefaultMessage() +"------------발생");
			json.setResult(result.getAllErrors());
			json.setStatus("FAIL");
		} else {	
			if(placeInfoService.editAuthCode(authcode)){
				json.setResult(null);
				json.setStatus("SUCCESS");
			}else{
				json.setResult("인증코드가 유효하지 않습니다");
				json.setStatus("FAIL");
			}
		}
		return json;
	}
	/**
	 * 가맹점 관리자 QR 코드 출력      <br />
	 * ROLE_FRANCHISER 권한만 인증코드를 변경할 수 있습니다.
	 * 가맹점 관리자 인증코드 수정    
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param 
	 * @return 
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/partner/getQrCode", method = RequestMethod.GET)
	public String list(Model model) {
		
		model.addAttribute("place",placeInfoService.getPlace(UserInfo.getFid()));	
		
		return "place/stamp/list";
	}
}
