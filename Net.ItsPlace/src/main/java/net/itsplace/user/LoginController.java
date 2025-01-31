/**
 * 
 */
package net.itsplace.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import net.itsplace.domain.JsonResponse;
import net.itsplace.domain.User;
import net.itsplace.domain.User.AddUser;
import net.itsplace.service.UserService;
import net.itsplace.util.Encrypt;
import net.itsplace.util.StandardOrMobile;

@Controller
public class LoginController {
        
	private static final Logger logger =  LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private PersistentTokenBasedRememberMeServices rememberMeServices;
	@Autowired
	private JsonResponse json;
	/**
	 * 아늗로이드에서 로그인 
	 * 안드로이드 페이스북 인증후  로그인한다. 
	 * @return
	 */
	@RequestMapping(value = "/user/facebooklogin", method = RequestMethod.POST)
	public @ResponseBody JsonResponse socialLogin(HttpServletRequest request,HttpServletResponse response,
												  @RequestParam(required=false, defaultValue="") String email,
												  @RequestParam(required=false, defaultValue="") String token){
		
				System.out.println("소셜인증");
				CustomUserDetailsService cuser = new CustomUserDetailsService();
				
				User user = userService.getUser(email);
				if(user == null){
					json.setFail();
					
					return json;
				}
				// Principal principal = new Princip();
				// UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(localUserId, "itsplace!@#$", cuser.getAuthorities("ROLE_USER"));
				CustomUserDetails details = new CustomUserDetails(
						user, 
						email,						
						user.getPassword(),
						true,
						true,
						true,
						true,
						cuser.getAuthorities("ROLE_USER"));
				System.out.println("password:"+details.getUser().getPassword());
				
				UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(details, details.getUser().getPassword(),cuser.getAuthorities("ROLE_USER"));
				//	RememberMeAuthenticationToken newAuth = new RememberMeAuthenticationToken("itsplace",details,cuser.getAuthorities("ROLE_USER"));
				//PersistentTokenBasedRememberMeServices d ; d.
				HttpServletRequest nativeReq = request;
				HttpServletResponse nativeRes = response;
				nativeReq.setAttribute("_spring_security_remember_me", "1");
				
				SecurityContextHolder.getContext().setAuthentication(newAuth);
				//rememberMeServices.setKey("itsplace");
				//rememberMeServices.setParameter("_spring_security_remember_me");
				rememberMeServices.setAlwaysRemember(true);
				rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);
				/*if(rememberMeServices.autoLogin(nativeReq, nativeRes)==null){
					
					rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);
					System.out.println("rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);");
					System.out.println("rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);");
					System.out.println("rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);");
					System.out.println("rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);");
				}else{
					System.out.println("cookey exit");
					System.out.println("cookey exit");
					System.out.println("cookey exit");
					System.out.println("cookey exit");
					System.out.println("cookey exit");
					System.out.println("cookey exit");
					System.out.println("cookey exit");
					System.out.println("cookey exit");
				}
		*/
//				SecurityContextHolder.getContext().setAuthentication(rememberMeAuthenticationToken);
				
//				
				System.out.println("리다이텍트 소셜 로그인");
				return json;
	}
	/**
	 * 로그인폼 호출<br>
	 * security-context.xml 참고할것
	 * 인증에 실패하면 authentication-failure-url 에 정의된 에러 파라미터를 넘긴다
	 * Validation 할것인지는 고려해볼것 				
	 * @author 김동훈
	 * @version 1.0, 2011.8.15 
	 * @param 
	 * @return  로그인페이지
	 * @throws org.springframework.dao.DataAccessException if the query fails
	 * @see 
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm(@RequestParam(value="error", required=false) boolean error,Model model,HttpServletRequest request) {
		 System.out.println("개발 모드 자동로그인");
//		 if(net.itsplace.user.UserInfo.autoLogin("faye12005@gmail.com", "hoon1014")){
//			 DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST_KEY");
//			 System.out.println("자동로그인"+request.getRequestURI()+defaultSavedRequest);
//			 //return request.getRequestURI();
//			// new CustomUserDetailsService().loadUserByUsername(username)
//			 model.addAttribute("back","back");
//			 
//		 }
		
		/*Map<String, Object> param;
		param = new HashMap<String, Object>();		
		List<User> userList= userService.getUserList(param);
		model.addAttribute("userList",userList);
		*/
		// ModelAndView m = new ModelAndView("/");
        // m.addObject("ajaxExceptionMessage", "login");
		/*if (error == true) {
			model.addAttribute("error", "이메일 또는 비밀번호가 잘못되었어요");
		} else {
			model.addAttribute("error", "");
		}
		//System.out.println("/user/login 실패시--------------------"+request.getHeader("X-Ajax-call"));
		////ogger.info("/user/login 실패시");
		
		String returnPage = "user/login";
		//일반  사용자 로그임 폼은 메인으로
		//가맹점은 로그인 폼으로 
		if(request.getHeader("webuser")!=null){
    		if (request.getHeader("webuser").equals("true")) {
    			returnPage = "redirect:/?badCrendcial=true";
    		}
		}*/
		return "user/login";		
	}
	
	/**
	 * 소셜 로그인 최초등록일경
	 * @author 김동훈
	 * @version 1.0, 2011.8.15 
	 * @param 
	 * @return  접근권한 없음 페이지
	 * @see 
	 */
//	@RequestMapping(value="/signup", method=RequestMethod.GET)
//	public String signup( Model model, WebRequest request) {
//		System.out.println("페이스북 최초 로그인시");
//		User user = new User();
//		Connection<?> connection = ProviderSignInUtils.getConnection(request);
//		if (connection != null) {
//			//request.setAttribute("message", new Message(MessageType.INFO, "Your " + StringUtils.capitalize(connection.getKey().getProviderId()) + " account is not associated with a Spring Social Showcase account. If you're new, please sign up."), WebRequest.SCOPE_REQUEST);
//			//return SignupForm.fromProviderUser(connection.fetchUserProfile());
//			UserProfile providerUser =	connection.fetchUserProfile();
//			System.out.println("사인업"+providerUser.getEmail()+providerUser.getName()+providerUser.getUsername());
//			user.setPassword("itsplace");
//			user.setEmail(providerUser.getEmail());
//			user.setName(providerUser.getName());
//			user.setProfileImageUrl(connection.getProfileUrl());
//			user.setRole("ROLE_USER");
//			
//			userService.saveUser(user);
//			User dbUser = userService.getUser(user.getEmail());
//			CustomUserDetailsService cuser = new CustomUserDetailsService();
//			CustomUserDetails details = new CustomUserDetails(
//					dbUser, 
//					dbUser.getEmail(),						
//					dbUser.getPassword().toLowerCase(),
//					true,
//					true,
//					true,
//					true,
//					cuser.getAuthorities("ROLE_USER"));
//			//UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(details, null);
//			UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(details, details.getUser().getPassword(),cuser.getAuthorities("ROLE_USER"));
//			SecurityContextHolder.getContext().setAuthentication(newAuth);
//			ProviderSignInUtils.handlePostSignUp(providerUser.getEmail(), request);
//			return "redirect:/places";
//		} else {
//			//return new SignupForm();
//			model.addAttribute("userForm", user);
//			model.addAttribute("user", user);
//			return "user/register";
//		}
//	}
//	
	
	@RequestMapping(value = "/passwordreset", method = RequestMethod.GET)
	public String passwordreset(){
		 
		return "user/passwordreset";
	}
	@RequestMapping(value = "/passwordreset", method = RequestMethod.POST)
	public String passwordreset(@RequestParam(value="email", required=false) String email, Model model, WebRequest request){
		
		if(userService.updateUserPasswordLink(request.getContextPath(),email)){
			model.addAttribute("error", "");
			return "user/passwordreset-send";
		}else{
			model.addAttribute("error", "이메일이 유효하지 않습니다");
			return "user/passwordreset";
		}
	
		
	}
	
	
	@RequestMapping(value = "/passwordchange/{passwordLink}", method = RequestMethod.GET)
	public String passwordchange(@PathVariable String passwordLink, Model model){
		User user = userService.getUserByPasswordLink(passwordLink);
		if(user == null){
			model.addAttribute("error", "올바른 요청이 아닙니다");
		}else{
			model.addAttribute("error", "");
			model.addAttribute("passwordLink", passwordLink);
			
		}
		return "user/passwordchange";
	}
	@RequestMapping(value = "/passwordchange", method = RequestMethod.POST)
	public String passwordchange(@RequestParam(value="password", required=true) String password,
								 @RequestParam(value="confirmPassword", required=true) String confirmPassword,
								 @RequestParam(value="passwordLink", required=true) String passwordLink,
								 Model model){
		User user = userService.getUserByPasswordLink(passwordLink);
		if(user == null){
			return "user/passwordchange";
		}else{
			if(!password.equals("") && password.equals(confirmPassword)){
				user.setPassword(Encrypt.md5Encoding(password));
				userService.updateUserPassword(user);
				return "user/passwordchange-end";
			}else{
				model.addAttribute("error", "비밀번호가 일치하지 않습니다");
				model.addAttribute("passwordLink", passwordLink);
				return "user/passwordchange";
			}
		}
		
	}
	
	@Deprecated
	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String signin(){
		 System.out.println("웹 사용자 로그인");
		return "user/signin";
	}
	
	/**
	 * 웹사용자 로그인 및 회원가입
	 * @param model
	 * @version 1.0, 2013.03.19
	 * @return
	 */
	@RequestMapping(value = "/sign-in", method = RequestMethod.GET)
	public String signin(Model model ) {
		
		model.addAttribute("user",new User());
		return "web/user/sign-in";
	}
	/**
	 * 웹사용자 로그인 및 회원가입
	 * @param model
	 * @version 1.0, 2013.03.19
	 * @return
	 */
	@RequestMapping(value = "/sign-in/{badCredential}", method = RequestMethod.GET)
	public String signin(@PathVariable String badCredential, Model model ) {
		if(!badCredential.isEmpty()){
			//model.addAttribute("errorcode","001");
			model.addAttribute("error","이메일 또는 비밀번호가 잘못되었어요");
		}
		//model.addAttribute("errorcode","002");
		model.addAttribute("user",new User());
		return "web/user/sign-in";
	}
	/**
	 * 웹사용자 로그인 및 회원가입
	 * @param model
	 * @version 1.0, 2013.03.19
	 * @return
	 */
	@RequestMapping(value = "/sign-in", method = RequestMethod.POST)
	public String signin(@Validated({AddUser.class}) User user, BindingResult result, Model model,HttpServletRequest request,HttpServletResponse response){
		logger.info("signup--------------------->");
		
		if (result.hasErrors()) {
			logger.debug("필드에러:"+result.getObjectName() +": "+ result.getFieldError().getDefaultMessage());
			//json =  json.getValidationErrorResult(result, json);
			//model.addAttribute("user",new User());
			return "web/user/sign-in";
		} else {	
			userService.saveUser(user);
			json.setResult(user);
			json.setSuccess();
			User dbUser = userService.getUser(user.getEmail());
			CustomUserDetailsService cuser = new CustomUserDetailsService();
			CustomUserDetails details = new CustomUserDetails(
					dbUser, 
					dbUser.getEmail(),						
					dbUser.getPassword(),
					true,
					true,
					true,
					true,
					cuser.getAuthorities("ROLE_USER"));
			System.out.println("password:"+details.getUser().getPassword() + user.getEmail());
			logger.info("가입완료");
			UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(details, details.getUser().getPassword(),cuser.getAuthorities("ROLE_USER"));
			HttpServletRequest nativeReq = request;
			HttpServletResponse nativeRes = response;
			nativeReq.setAttribute("_spring_security_remember_me", "1");
			
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			rememberMeServices.setKey("itsplace");
			rememberMeServices.setParameter("_spring_security_remember_me");
			rememberMeServices.setAlwaysRemember(true);
			rememberMeServices.loginSuccess(nativeReq,nativeRes,newAuth);
			
		}
		return "redirect:/places";
	}
	/**
	 * 접근권한이 없을시에 페이지 호출 security-context.xml의 access-denied-page="/denied" 정의함
	 * 	<http auto-config="true" use-expressions="true" access-denied-page="/denied" >
	 * access-denied-page="/denied"
	 * @author 김동훈
	 * @version 1.0, 2011.8.15 
	 * @param 
	 * @return  접근권한 없음 페이지
	 * @throws org.springframework.dao.DataAccessException if the query fails
	 * @see 
	 */
	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	public String denied() {	
		return "user/deniedpage";
	}
	
	
	 /*@RequestMapping(value="/logout")
	  public String logout(@RequestParam("targetUrl") String targetUrl, SessionStatus status) {
	      status.setComplete();
	      return "redirect:" + targetUrl;
	  }
	  */
}