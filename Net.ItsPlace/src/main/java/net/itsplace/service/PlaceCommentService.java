package net.itsplace.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.itsplace.domain.DataTable;
import net.itsplace.domain.JpaPaging;
import net.itsplace.domain.PlaceComment;
import net.itsplace.repository.PlaceCommentRepository;
import net.itsplace.user.UserInfo;
import net.itsplace.util.PagingManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface PlaceCommentService {
	public DataTable<PlaceComment> getPlaceCommentList(String columns[],Integer iDisplayStart,Integer iDisplayLength,Integer iSortCol_0,String sSortDir_0, String sSearch, int fid);
	public void savePlaceComment(PlaceComment placeComment);
	public PlaceComment getPlaceComment(int cid);
	public boolean deletePlaceComment(int cid) ;
	public Page<PlaceComment> findPlaceCommentList(JpaPaging paging,int fid);
	public List<PlaceComment> findPlaceCommentList(int fid);
}

@Service("placeCommentService")
class PlaceCommentServiceImpl  implements PlaceCommentService {
	private static final Logger logger = LoggerFactory.getLogger(PlaceCommentService.class);
	@Autowired
	PagingManager pagingManaer;
	@Autowired
	PlaceService placeService;
	@Autowired
	PlaceCommentRepository repo;
	@Override
	public DataTable<PlaceComment> getPlaceCommentList(String columns[],Integer iDisplayStart,Integer iDisplayLength,Integer iSortCol_0,String sSortDir_0, String sSearch,int fid){
//		  DataTable<PlaceComment> table = iDisplayLength != null ?
//                  new DataTable<PlaceComment>(columns, sSortDir_0, iDisplayStart, iDisplayLength) :
//                  new DataTable<PlaceComment>(columns, sSortDir_0, iDisplayStart);
//		  Map<String, Object> param  = pagingManaer.createDataTableLimit(iDisplayStart, iDisplayLength);
//		  param.put("sortDirection", sSortDir_0);
//		  param.put("sortColumn", table.getOrderColumn(iSortCol_0));
//		  param.put("search", sSearch);
//			
//		  List<PlaceComment> userList= placeCommentDao.getPlaceCommentList(param);
//		  
//		  pagingManaer.setTotalCount(pagingManaer.getFoundRows());
//		 
//		  table.setRows(userList); 
//		  table.setiTotalDisplayRecords(pagingManaer.getTotalCount());
		return null;
	}
	
	public Page<PlaceComment> findPlaceCommentList(JpaPaging paging,int fid) {
		
		Page<PlaceComment> list = repo.findByPlace(placeService.getPlace(fid), paging.getPageable());
		
		List<PlaceComment> placeCommentList = list.getContent();
		
//		for(int i=0;i<placeCommentList.size();i++){
//			PlaceComment placeComment = placeCommentList.get(i);
//			
//			//placeComment.setPrettyDate(DurationFromNow.getTimeDiffLabel(placeComment.getSaveDate()));
//			
//			placeCommentList.set(i, placeComment);
//		}
		return list;
	}
	@Override
	public void savePlaceComment(PlaceComment placeComment) {
		placeComment.setSaveDate(new Date());
		placeComment.setIsDelete(false);
		placeComment.setUser(UserInfo.getUser());
			
		repo.save(placeComment);
	}

	@Override
	public boolean deletePlaceComment(int cid) {
		boolean result = false;
		PlaceComment placeComment = getPlaceComment(cid);	
		if(placeComment == null){
			return false;
		}
		
		placeComment.setIsDelete(true);
		placeComment.setUser(UserInfo.getUser());
		
			repo.save(placeComment);
			
		
		
//		if(placeComment.getEmail().equals(UserInfo.getEmail()) || UserInfo.getUser().getRole().equals(commonService.getCode("ROLE","ADMIN"))){
//			placeCommentDao.deletePlaceComment(cid);	
//			result = true;
//		}
//		else{
//			
//			result = false;
//		}
		return result;
	}

	@Override
	public PlaceComment getPlaceComment(int cid) {
		return repo.findOne(cid);
	}

	@Override
	public List<PlaceComment> findPlaceCommentList(int fid) {
		// TODO Auto-generated method stub
		
		return repo.findByPlace(placeService.getPlace(fid));
	}

}
