package net.itsplace.admin.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.itsplace.admin.service.AdminUserService;
import net.itsplace.domain.BasCd;
import net.itsplace.domain.DataTable;
import net.itsplace.domain.JsonResponse;
import net.itsplace.user.User;
import net.itsplace.user.User.AddUser;
import net.itsplace.user.User.EditUser;
import net.itsplace.util.PagingManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {
	private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);
	@Autowired
	private AdminUserService adminUserService;
	
	@Autowired
	private PagingManager pagingManaer;
	
	/**
	 * 관리자 회원 관리 리스트 <br />
	 * 모든 회원을 조회한다 <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
	
		return "admin/user/list";
	}
	/**
	 * 회원 생성 폼<br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  add.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("user",new User());	
		return "admin/user/add";
	}
	/**
	 * 회원 생성 <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
 	public String addSubmit(@Validated({AddUser.class}) User user, BindingResult result, Model model) {
		
	  
		if (result.hasErrors()) {
			logger.info(result.getObjectName() +": "+ result.getFieldError().getDefaultMessage() +"------------발생");
			return "admin/user/add";
		} else {	
			adminUserService.saveUser(user);		
			return "admin/user/list";
		}
	
	}
	/**
	 * 회원 생성 <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(@RequestParam(required=true) String email, Model model)  {
	
		model.addAttribute("user",adminUserService.getUser(email));
		User user = null;
		//user.setEmail("dd");
		//int i = 66666*10343434000;		
		
		return "admin/user/edit";
	}
	/**
	 * 회원 생성 <br />
	 * 
	 * @author 김동훈
	 * @version 1.0, 2011. 8. 24.
	 * @param model
	 * @return  list.jsp
	 * @throws 
	 * @see 
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public @ResponseBody JsonResponse editSubmit(@Validated({EditUser.class}) User user, BindingResult result, Model model)  {
		JsonResponse json = new JsonResponse();
		System.out.println("edit");
		if (result.hasErrors()) {
			logger.info(result.getObjectName() +": "+ result.getFieldError().getDefaultMessage() +"------------발생");
			json.setResult(result.getAllErrors());
			json.setStatus("FAIL");
		} else {	
			adminUserService.updateUser(user);
			json.setResult(user);
			json.setStatus("SUCCESS");
			
		}		
		
		return json;
	}
	@RequestMapping(value="/getUserList")
    @ResponseBody
    public DataTable<User> getUserList(
    								@RequestParam(required=false, defaultValue="1") Integer iDisplayStart,
    								@RequestParam(required=false) Integer iDisplayLength,
    								@RequestParam(required=false) Integer iSortCol_0, 
    								@RequestParam(required=false) String sSortDir_0, 
                                    @RequestParam(required=false, defaultValue="") String sSearch ) {

                    logger.info("iDisplayStart:{}", iDisplayStart.toString());
                    logger.info("sSortDir_0:{}", sSortDir_0.toString());
                    logger.info("iSortCol_0:{}", iSortCol_0.toString());
                    logger.info("iDisplayLength:{}", iDisplayLength.toString());
                    logger.info("sSearch:{}", sSearch.toString());
                   
                    String columns[] = new String[]{"profileImageUrl", "email", "name","role", "mobile", "useyn", "emailyn"};
                    //도메인이랑 대소문자 일치해야
                    DataTable<User> table = iDisplayLength != null ?
                                    new DataTable<User>(columns, sSortDir_0, iDisplayStart, iDisplayLength) :
                                    new DataTable<User>(columns, sSortDir_0, iDisplayStart);
                    
                    logger.info("getOrderColumn:{}"+ table.getOrderColumn(iSortCol_0));
                   
            		Map<String, Object> param  = pagingManaer.createDataTableLimit(iDisplayStart, iDisplayLength);
                    param.put("search", sSearch);
                    param.put("sortDirection", sSortDir_0);
                    param.put("sortColumn", table.getOrderColumn(iSortCol_0));
            		
                    List<User> userList= adminUserService.getUserList(param);
            		pagingManaer.setTotalCount(pagingManaer.getFoundRows());
            		
            		
                   
                    table.setRows(userList); // TODO add filter params to the service method, like in organizations.
                    table.setiTotalDisplayRecords(pagingManaer.getTotalCount());
                   
                    return table;
           
                   
    }       
	
	
	
}
