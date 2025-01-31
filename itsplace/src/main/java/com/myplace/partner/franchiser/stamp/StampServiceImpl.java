package com.myplace.partner.franchiser.stamp;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.itsplace.partner.place.PlaceComment;
import com.myplace.partner.franchiser.FranchiserDao;
import com.myplace.partner.franchiser.FranchiserMember;
import com.myplace.partner.franchiser.FranchiserService;
import com.myplace.util.DurationFromNow;


@Service("StampService")
public class StampServiceImpl implements StampService{

	private static final Logger logger =  LoggerFactory.getLogger(StampController.class);
	@Autowired
	private StampDao  stampDao;
	@Autowired
	private FranchiserService franchiserService;
	
	@Override
	public String saveStamp(Stamp stamp)  {
		FranchiserMember fMember = null;
		fMember = franchiserService.getFranchiserFcode(stamp.getFcode());
		if(fMember == null){
			logger.info("인증번호가 틀력씁니다:{0}",stamp.getFcode());
			return "false";
		}else{
			stampDao.saveStamp(stamp);
			logger.info(stamp.toString());
			return "true";
		}
		
	}



	@Override
	public List<Stamp> getStamptypeList() {
		return stampDao.getStamptypeList();
	}

	

	@Override
	public Stamp getStampRegister(Stamp stamp)  {
		return stampDao.getStampRegister(stamp);
	}
	@Override
	public void updateStampRegister(Stamp stamp)  {
		stampDao.updateStampRegister(stamp);
		
	}

	@Override
	public void saveStampRegister(Stamp stamp)  {
		if(stampDao.getStampRegister(stamp)==null){
			
			stampDao.saveStampRegister(stamp);
		}else{
			stampDao.updateStampRegister(stamp);
		}
		
	}



	@Override
	public List<Stamp> getUserStampListByMobile(Stamp stamp)
			 {
		return stampDao.getUserStampListByMobile(stamp);
	}



	@Override
	public Stamp getStamptypeRegister(int fid) {
		// TODO Auto-generated method stub
		return stampDao.getStamptypeRegister(fid);
	}



	@Override
	public void deleteStamp(String pid) {
		stampDao.deleteStamp(pid);
		
	}



	@Override
	public List<Stamp> getUserStampListByEmail(Stamp stamp)
			throws DataAccessException {
		return stampDao.getUserStampListByEmail(stamp);
	}



	@Override
	public List<Stamp> getUserStampListByMain() {
		List<Stamp> list = stampDao.getUserStampListByMain();
		for(Integer i = 0 ; i < list.size(); i++){
			Stamp pc  = list.get(i);
			pc.setStampDate(((DurationFromNow.getTimeDiffLabel(pc.getInpdate()))));
			list.set(i,pc);
		}
		return list;
		
	}



	@Override
	public void updateStamp(Stamp stamp) {
		stampDao.updateStamp(stamp);
		
	}



	@Override
	public List<Stamp> getUserStampListByFid(Stamp stamp)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return stampDao.getUserStampListByFid(stamp);
	}



	@Override
	public List<Stamp> getUserStampListByFcode(Stamp stamp) {
		// TODO Auto-generated method stub
		return stampDao.getUserStampListByFcode(stamp);
	}

}
