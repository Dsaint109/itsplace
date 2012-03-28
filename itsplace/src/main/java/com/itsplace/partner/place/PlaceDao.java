package com.itsplace.partner.place;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.itsplace.partner.place.PlaceComment;
import com.myplace.partner.franchiser.FranchiserMember;


public interface PlaceDao {
	
	/* 최근 작성된 코멘트 리스트를 가져온다 */
	public List<PlaceComment>  getPlaceCommentRecentList(Map<String, Object> param) throws DataAccessException;
	public List<FranchiserMember>  getPlaceListByDong(Map<String, Object> param) throws DataAccessException;
	
	
	
}
