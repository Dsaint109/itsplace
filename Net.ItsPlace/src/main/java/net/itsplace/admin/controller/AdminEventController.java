package net.itsplace.admin.controller;

import java.util.Locale;

import net.itsplace.admin.service.AdminBaseService;
import net.itsplace.admin.service.AdminEventService;
import net.itsplace.admin.service.AdminPlaceService;
import net.itsplace.domain.PlaceEvent;
import net.itsplace.domain.PlaceStamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminEventController {
	private static final Logger logger = LoggerFactory.getLogger(AdminEventController.class);
	@Autowired
	private AdminEventService adminEventService;
	
	@Autowired
	private  AdminPlaceService adminPlaceService;
	
	/**
	 * 가맹점 이벤트관리
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/admin/place/event/add", method = RequestMethod.GET)
	public String add(@RequestParam(required=true) Integer fid,
						Model model) {
		
		model.addAttribute("place",adminPlaceService.getPlace(fid));
		
		model.addAttribute("placeEvent", new PlaceEvent());
		model.addAttribute("placeEventList",adminEventService.getPlaceEventList(fid));
		return "admin/place/event/add";
	}
}
