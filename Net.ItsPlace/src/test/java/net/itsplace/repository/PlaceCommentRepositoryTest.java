package net.itsplace.repository;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.itsplace.domain.Place;
import net.itsplace.domain.PlaceComment;
import net.itsplace.domain.PlaceMedia;
import net.itsplace.domain.PlaceReview;
import net.itsplace.domain.QPlaceComment;
import net.itsplace.domain.QPlaceMedia;
import net.itsplace.domain.QPlaceReview;
import net.itsplace.domain.dto.PlaceUserMedia;
import net.itsplace.init.TestApplicationContext;

import org.apache.velocity.runtime.directive.Foreach;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mysema.query.types.Predicate;

public class PlaceCommentRepositoryTest extends TestApplicationContext {
	  
	private static final Logger logger  = LoggerFactory.getLogger(PlaceCommentRepositoryTest.class); 

	@Autowired
	PlaceCommentRepository commentRepo;
	
	@Autowired
	PlaceReviewRepository reviewRepo;
	
	@Test
	public void testFindByPlace() {
		Place place = new Place();
		place.setFid(46);
		
		
		List<PlaceUserMedia> userMedias = new ArrayList();
		QPlaceComment placeComment = QPlaceComment.placeComment;
		Predicate predicate = placeComment.isDelete.eq(false).and(placeComment.place.eq(place));
    	Iterable<PlaceComment> list = commentRepo.findAll(predicate);
    	
    	PlaceUserMedia placeUserMedia = null;
    	for (PlaceComment c : list) {
			placeUserMedia = new PlaceUserMedia();
			placeUserMedia.setMkey(c.getCid());
			placeUserMedia.setTitle("");
			placeUserMedia.setMtype("comment");
			placeUserMedia.setContent(c.getComment());
			placeUserMedia.setUrl("");
			placeUserMedia.setCreateDate(c.getSaveDate());
			userMedias.add(placeUserMedia);
		}
    	
    	
    	QPlaceReview placeReview = QPlaceReview.placeReview;
		predicate = placeReview.isDelete.eq(false).and(placeReview.place.eq(place));
    	Iterable<PlaceReview> list2 = reviewRepo.findAll(predicate);
    	
    	for (PlaceReview r : list2) {
    		placeUserMedia = new PlaceUserMedia();
			placeUserMedia.setMkey(r.getRid());
			placeUserMedia.setTitle(r.getTitle());
			placeUserMedia.setMtype("review");
			placeUserMedia.setContent(r.getContent());
			placeUserMedia.setUrl(r.getFilePath());
			placeUserMedia.setSiteUrl(r.getSiteURL());
			placeUserMedia.setCreateDate(r.getSaveDate());
			userMedias.add(placeUserMedia);
		}
		
    	//userMedias
    	Comparator compate = new Comparator<PlaceUserMedia>() {

			@Override
			public int compare(PlaceUserMedia o1, PlaceUserMedia o2) {
				if(o1.getCreateDate().compareTo(o2.getCreateDate()) == 0){
					
					return 0;
				}else{
					return 1;
				}
				
				
			}
		};
	}
	

}
