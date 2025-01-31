package com.itsplace.partner.place.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itsplace.partner.place.PlaceComment;
import com.itsplace.partner.place.PlaceController;
import com.myplace.partner.franchiser.FranchiserMember;

@Controller
public class EventController {
	private static final Logger logger =  LoggerFactory.getLogger(PlaceController.class);
	
	@Autowired
	EventService eventService;
	
	

	/**
	 * <pre>
	 * 이벤트 등록폼			
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10.21
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/event/new/{fid}", method = RequestMethod.GET)
	public  String  event_form(@PathVariable String fid, Model model){
		
		Event event = new Event();
		event.setFid(fid);
		Map<String, Object> param = new HashMap<String, Object> ();
		param.put("fid", fid);
		
		model.addAttribute("event",event);

		List<Event> list = eventService.getEventList(param);
		model.addAttribute("eventList",list);
		return "place/eventForm";
		
	}
	/**
	 * <pre>
	 * 이벤트 등록폼			
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10.21
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/event/new/{fid}", method = RequestMethod.POST)
	public  String  event_new_submit(@Valid Event event, BindingResult result){
		
	        
		if (result.hasErrors()) {			
			return  "place/eventForm";	
		}else {
			logger.info(event.toString());
			int eid = eventService.saveEvent(event);
			return "redirect:/place/event/edit/" + eid ;
			
		}
	}
	/**
	 * <pre>
	 * 이벤트 수정폼			
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10.21
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/event/edit/{eid}", method = RequestMethod.GET)
	public  String  event_edit(@PathVariable String eid ,Model model){
		Event event = eventService.getEvent(eid);
		model.addAttribute("event",event);
		
		Map<String, Object> param = new HashMap<String, Object> ();
		param.put("fid", event.getFid());
		List<Event> list = eventService.getEventList(param);
		model.addAttribute("eventList",list);
		return "place/eventForm";
		
	}
	/**
	 * <pre>
	 * 이벤트 수정폼			
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10.21
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/event/edit/{eid}", method = RequestMethod.POST)
	public  String  event_edit(@Valid Event event, BindingResult result){
		
		if (result.hasErrors()) {			
			return  "place/eventForm";	
		}else {
			logger.info(event.toString());
			eventService.updateEvent(event);
			
			return "redirect:/place/event/edit/" + event.getEid() ;
			
		}
	
		
	}
	
	/**
	 * <pre>
	 * 이벤트 등록폼			
	 * </pre>
	 * @author 김동훈
	 * @version 1.0, 2011. 10.21
	 * @param 
	 * @return  
	 * @throws 
	 * @see 
	 */
	
	@RequestMapping(value="/place/event/list/{fid}", method = RequestMethod.POST)
	public String event_list(@PathVariable String fid){

		Map<String, Object> param = new HashMap<String, Object> ();
		param.put("fid", fid);
		
		
		eventService.getEventList(param);
	return "place/eventList";
			
		
	}
	
}
