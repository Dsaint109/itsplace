package net.itsplace.controller.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.itsplace.domain.JsonResponse;
import net.itsplace.domain.Place;
import net.itsplace.domain.PlaceEvent;
import net.itsplace.repository.PlacePredicates;
import net.itsplace.service.PlaceService;
import net.itsplace.util.Paging;
import net.itsplace.util.PagingManager;
import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysema.query.types.Predicate;

@Controller
public class SearchController {
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
	@Autowired
	PlaceService palceService;
	@Autowired
	private PagingManager pagingManaer;
	/**
	 * Tile검색
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/web/search/place", method = RequestMethod.POST)
	public @ResponseBody JsonResponse  searchPlace(@RequestParam(required=false, defaultValue="") String searchWord ){
		logger.info("searchWord:{}",searchWord);
		JsonResponse json = new JsonResponse();
		
		Map<String, Object> param  =  new HashMap();
		param.put("searchWord", searchWord);
		
		Predicate predicate = PlacePredicates.isAuth(true).and(PlacePredicates.likeFname(searchWord));
		
		
		
//		json.setResult(searchService.getPlaceListByTile(param));
		json.setResult(palceService.findByAll(predicate));
		return json;
	}
	
	/**
	 * 주변검색
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search/place", method = RequestMethod.GET)
	public String place(Locale locale, Model model, @ModelAttribute Place p) {
		JSONArray array = new JSONArray();
		Paging page = new Paging();
//		p.setStart(p.getPageSize() * (p.getCurrentPage() - 1));
//		array = JSONArray.fromObject(searchService.placeInfo(p));
//		String pageHTML = page.init(p.getCurrentPage(), p.getPageSize(), p.getPageBlock(), "PLACE", null);
//		
		model.addAttribute("list", array);
	//	model.addAttribute("page", pageHTML);
		model.addAttribute("place", p);
		return "web/search/place";
	}
	
	
	
	/**
	 * 스마트폰 가맹점 검색
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search/placeList", method = RequestMethod.POST)
	public @ResponseBody JsonResponse  PlaceList(@RequestParam(required=false, defaultValue="1") Integer currentPage,
			 									@RequestParam(required=false, defaultValue="10") Integer pageSize ,
			 									@RequestParam(required=false, defaultValue="10") Integer pageGroupSize ,
			 									@RequestParam(required=false, defaultValue="") String searchWord 
			 									){
		logger.info("currentPage:{}",currentPage);
		logger.info("pageSize:{}",pageSize);
		logger.info("pageGroupSize:{}",pageGroupSize);
		logger.info("searchWord:{}",searchWord);
		Map<String, Object> param  = pagingManaer.createMysqlLimit(currentPage, pageSize);
		JsonResponse json = new JsonResponse();
		//json.setResult(searchService.getPlaceList(param));
		//json.setTotalCount(pagingManaer.getFoundRows());
		return json;
	}
	/**
	 * 스마트폰 가맹점 검색
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search/test", method = RequestMethod.POST)
	public @ResponseBody JsonResponse  test(){
		JsonResponse json = new JsonResponse();
		json.setResult("aaaa");
			return json;
	}
	
	/**
	 * 지도검색
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search/map", method = RequestMethod.GET)
	public String map(Locale locale, Model model) {
		return "web/search/map";
	}
	

	/**
	 * 주변검색
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search/event/list", method = RequestMethod.GET)
	public @ResponseBody JsonResponse  seventList(@RequestParam(required=false, defaultValue="1") Integer currentPage,
													 @RequestParam(required=false, defaultValue="10") Integer pageSize ,
													 @RequestParam(required=false, defaultValue="10") Integer pageGroupSize 
													){
		Map<String, Object> param  = pagingManaer.createMysqlLimit(currentPage, pageSize);
		List<PlaceEvent> placeEventList = null;//searchService.getPlaceEventList(param);
		
		String paging = pagingManaer.creatPaging(currentPage, pageSize, pagingManaer.getFoundRows(), pageGroupSize);
		 //String paging = pagingManaer.createPageHtml();
	//	pagingManaer.creatPaging(currentPage,pageSize,pagingManaer.getFoundRows(),pageGroupSize);
		//JSONArray array = new JSONArray();
		 JsonResponse json = new JsonResponse();
		 json.setResult(placeEventList);
		 json.setPaging(paging);
		return json;
	}
	/**
	 * 이벤트
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search/event", method = RequestMethod.GET)
	public String  eventList(@RequestParam(required=false, defaultValue="1") Integer currentPage,
							 @RequestParam(required=false, defaultValue="10") Integer pageSize ,
							 @RequestParam(required=false, defaultValue="10") Integer pageGroupSize,
							 Model model
							){
		Map<String, Object> param  = pagingManaer.createMysqlLimit(currentPage, pageSize);
		//List<PlaceEvent> placeEventList = searchService.getPlaceEventList(param);
		
		String paging = pagingManaer.creatPaging(currentPage, pageSize, pagingManaer.getFoundRows(), pageGroupSize);
		//model.addAttribute("placeEventList",placeEventList);
		model.addAttribute("paging",paging);
		return "web/search/event";
	}
}
