package net.itsplace.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.itsplace.domain.DataTable;
import net.itsplace.domain.JpaPaging;
import net.itsplace.domain.Place;
import net.itsplace.domain.PlaceEvent;
import net.itsplace.domain.PlaceUser;
import net.itsplace.domain.User;
import net.itsplace.repository.PlaceUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

public interface PlaceUserService {

	public List<PlaceUser> getPlaceListByEmail(String email) ;
	
	/* 가맹점 직원등록  */	
	public void savePlaceUser(PlaceUser placeUser);	
	/* 가맹점 직원삭제 */
	public void deletePlaceUser(int uid);
	DataTable<PlaceUser> findPlaceUserList(JpaPaging paging,  int fid);
}



@Service("placeUserService")
class PlaceUserServiceImpl implements PlaceUserService {

	private static final Logger logger = LoggerFactory.getLogger(PlaceUserServiceImpl.class);
	
	
	@Autowired
	PlaceUserRepository repo;
	
	@Autowired
	PlaceService placeService;
	
	@Autowired
	UserService userService;
	@Override
	public DataTable<PlaceUser> findPlaceUserList(JpaPaging paging, int fid) {
		
          DataTable<PlaceUser> table = new DataTable<PlaceUser>(paging);
          
          
          Page<PlaceUser> users = repo.findByPlace(placeService.getPlace(fid), paging.getPageable());
          
      			 
          
		  table.setRows(users.getContent()); 
		  
		  table.setiTotalDisplayRecords(users.getTotalElements());
		  logger.info("결과:{}",table.getiDisplayLength());
		  logger.info("결과:{}",table.getiTotalRecords());
		  return table;
	}

	@Override
	public void savePlaceUser(PlaceUser placeUser) {
		repo.save(placeUser);
	}

	@Override
	public void deletePlaceUser(int uid) {
		PlaceUser user = repo.findOne(uid);
		user.setIsDelete(true);
		user.setEditDate(new Date());
		repo.save(user);
	}

	@Override
	public List<PlaceUser> getPlaceListByEmail(String email) {
		
		return repo.findByUser(userService.getUser(email));
	}


	
	
}
