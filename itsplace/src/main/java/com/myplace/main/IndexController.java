package com.myplace.main;

import java.security.Principal;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
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
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.itsplace.partner.place.PlaceComment;
import com.itsplace.partner.place.PlaceService;
import com.myplace.common.CommonService;
import com.myplace.common.Notice;
import com.myplace.partner.franchiser.FranchiserMember;
import com.myplace.partner.franchiser.FranchiserService;
import com.myplace.partner.franchiser.stamp.Stamp;
import com.myplace.partner.franchiser.stamp.StampService;
import com.myplace.user.User;
import com.myplace.user.UserController;
import com.myplace.user.UserService;
import com.myplace.util.PagingManager;
import com.myplace.util.StandardOrMobile;

/**
 * 메인 인덱 
 * 
 * 
 */
@Controller
public class IndexController {
	private static final Logger logger =  LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private FranchiserService franchiserService;
	@Autowired
	private PlaceService placeService;
	@Autowired
	private StampService stampService;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private Facebook facebook;
	
	private final Provider<ConnectionRepository> connectionRepositoryProvider;

	@Inject
	public IndexController(Provider<ConnectionRepository> connectionRepositoryProvider) {
		this.connectionRepositoryProvider = connectionRepositoryProvider;
	}
	
	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}
	
	/**
	 *  메인 인덱스 
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Device device,SitePreference sitePreference,Model model,HttpServletRequest request) {
		model.addAttribute("connectionsToProviders", getConnectionRepository().findAllConnections());
		
		Boolean confirmEmail = (Boolean)request.getSession().getAttribute("confirmEmail");
	
		if(confirmEmail != null){
			if(confirmEmail){
				System.out.println("이메일인증");
				model.addAttribute("confirmEmail",true);
				request.getSession().removeAttribute("confirmEmail");
			}
		}
		Map<String, Object> param  = new HashMap<String, Object>();
		
		List<PlaceComment> placeCommentList =  placeService.getPlaceCommentRecentList(param);
		model.addAttribute("placeCommentRecentList",placeCommentList);
		
		List<FranchiserMember> franchiserMemberList  = franchiserService.getFranchiserMemberListByMain();
		model.addAttribute("franchiserMemberList",franchiserMemberList);
		
		List<Stamp> stapmList  = stampService.getUserStampListByMain();
		model.addAttribute("stapmList",stapmList);
		
		param.put("startRow", 0);
		param.put("endRow", 5);
		List<Notice> NoticeList  = commonService.getNoticeList(param);
		model.addAttribute("noticeList",NoticeList);
		
	
		
		//logger.info(" Main : Index 페이지 호출: Locale: "+ locale.toString());
		
	//	SecurityContext securityContext = SecurityContextHolder.getContext();
	//	Authentication auth = securityContext.getAuthentication();
		
		//String login = request.getParameter("login");
	
	//	Date date = new Date();
	//	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
	//	String formattedDate = dateFormat.format(date);
		
		try {
			//User user = userService.getUser("");
			
			
			
		//	loggger.info("유저정보:" + user.getName() );
			//.debug("유저정보:" + user.getName() );
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//model.addAttribute("serverTime", formattedDate );
		logger.info("--------------------------Top 페이지hhㅗㅎ허 호출" );
		
		return StandardOrMobile.getPageName(device, sitePreference,  "mobile_home",   "home");		
		
		
		
	}
	@RequestMapping(value = "/welcome/confirm", method = RequestMethod.GET)
	public String welcome(Device device,SitePreference sitePreference,Model model,HttpServletRequest request) {
		
		return StandardOrMobile.getPageName(device, sitePreference,  "welcome/confirm",   "welcome/confirm");
	}
	
    @RequestMapping(value = "/common", method = RequestMethod.GET)
    public String getCommonPage() {
    	logger.debug("Received request to show common page");
    
    	// Do your work here. Whatever you like
    	// i.e call a custom service to do your business
    	// Prepare a model to be used by the JSP page
    	
    	// This will resolve to /WEB-INF/jsp/commonpage.jsp
    	return "commonpage";
	}
	

}
