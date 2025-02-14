package com.myplace.partner.franchiser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itsplace.partner.place.PlaceComment;
import com.myplace.common.mail.MailService;
import com.myplace.util.DateUtil;
import com.myplace.util.DurationFromNow;
import com.myplace.util.Encrypt;


@Service("FranchiserService")
public class FranchiserServiceImpl implements FranchiserService {

	private static final Logger logger =  LoggerFactory.getLogger(FranchiserServiceImpl.class);
	@Autowired
	private FranchiserDao franchiserDao;
	@Autowired
	MailService mailService;
	   
	
	@Inject 
	private FileSystemResource fsResource;
	
	@Transactional(readOnly=true)
	public FranchiserMember getFranchiserMember(int fid) {		
		return franchiserDao.getFranchiserMember(fid);
			
		
	}



	@Transactional(readOnly=true)
	public List<FranchiserMember> getFranchiserMemberDaoList(Map<String, Object> param){
		if(logger.isDebugEnabled()){
			List<FranchiserMember> ul = franchiserDao.getFranchiserMemberList(param);
			if (ul != null){				
				for(Integer i=0;i<ul.size();i++){
					FranchiserMember franchiserMember = (FranchiserMember)ul.get(i);
					logger.debug("FranchiserMember:" + franchiserMember.toString());
				}
			}
			
		}
		return franchiserDao.getFranchiserMemberList(param);
	}


	public FranchiserMember saveFranchiserMember(FranchiserMember franchiserMember,HttpServletRequest request) {
		if(!franchiserMember.getFileData().isEmpty()){
			franchiserMember = saveFranchiserImage(franchiserMember);
		}else{
			franchiserMember.setFileName("thumbnail.jpg");
		}
	
		
		int fid = franchiserDao.saveFranchiserMember(franchiserMember);
		franchiserMember.setFid(fid);
		
		franchiserMember = setQrcodeFile(franchiserMember, request.getRequestURL().toString());
	
		
		String content = "타입:"+franchiserMember.getStype() + "<br>";
		content+="비고:"+franchiserMember.getRemark();
		mailService.sendMail("admin@itsplace.net", "faye12005@gmail.com", "[가맹신청]"+franchiserMember.getFname(), content);
	
	
		return franchiserMember;
		
	}

	@Override
	public void updateFranchiserMember(FranchiserMember franchiserMember) {
		
		if(!franchiserMember.getFileData().isEmpty()){
			franchiserMember = saveFranchiserImage(franchiserMember);
		}else{
			franchiserMember.setFileName("thumbnail.jpg");
		}
		
		franchiserDao.updateFranchiserMember(franchiserMember);
	}

	public List<FranchiserMember> getFranchiserMemberList(
			Map<String, Object> param) throws DataAccessException {
		
		return franchiserDao.getFranchiserMemberList(param);
	}



	public void savePlaceComment(PlaceComment placeComment) {
		franchiserDao.savePlaceComment(placeComment);
		
	}
	public List<PlaceComment> getPlaceCommentList(int fid) {
		
		List<PlaceComment> list = franchiserDao.getPlaceCommentList(fid);
		for(Integer i = 0 ; i < list.size(); i++){
			PlaceComment pc  = list.get(i);
			pc.setWriteDate(DurationFromNow.getTimeDiffLabel(pc.getInpdate()));
			list.set(i,pc);
		}
		//logger.info(DurationFromNow.test2());
		return list; 
		 
	}



	public void deletePlaceComment(PlaceComment placeComment) {
		franchiserDao.deletePlaceComment(placeComment);
		
	}



	public List<FranchiserMember> getFranchiserListByRoleFranchaiser(
			Map<String, Object> param) {
		// TODO Auto-generated method stub
		return franchiserDao.getFranchiserListByRoleFranchaiser(param);
	}



	@Override
	public List<FranchiserMember> getFranchiserMemberListByMain() {
		// TODO Auto-generated method stub
		return franchiserDao.getFranchiserMemberListByMain();
	}



	@Override
	public List<FranchiserMember> getFranchiserMemberByLocation(
			FranchiserMember franchiserMember) {
		// TODO Auto-generated method stub
		return franchiserDao.getFranchiserMemberByLocation(franchiserMember);
	}





	public FranchiserMember saveFranchiserImage(FranchiserMember franchiserMember){
		String filename = franchiserMember.getFileData().getOriginalFilename();
		franchiserMember.setFileName(filename);
		
		String imgExt = filename.substring(filename.lastIndexOf(".")+1, filename.length());

		
		if(imgExt.equalsIgnoreCase("JPG") || imgExt.equalsIgnoreCase("JPEG") || imgExt.equalsIgnoreCase("GIF") || imgExt.equalsIgnoreCase("PNG")){
			byte[] bytes = franchiserMember.getFileData().getBytes();
			try{
				//logger.info(fsResource.getPath() + filename);
				String sysYear = DateUtil.getSysYear();
	 			String sysMonth = DateUtil.getSysMonth();
	 			String sysTime = DateUtil.getSysTime();
	 			String yearDir = fsResource.getPath()+ sysYear;
	 			String targerDir =yearDir +"/"+sysMonth+"/";
	 			logger.info("fsResource:"+fsResource.getPath());
	 			File createYearDir = new File(fsResource.getPath(), sysYear);
 				 try {
					FileUtils.forceMkdir(createYearDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
 				
 				 File createMonthDir = new File(yearDir+"/", sysMonth);
 				 try {
 					 FileUtils.forceMkdir(createMonthDir);
 				 } catch (IOException e) {
 					 e.printStackTrace();
 				 }
 				logger.info("타켓DIR: " + targerDir+sysYear+sysMonth+sysTime); 
 				
 				String srcName = targerDir+sysYear+sysMonth+sysTime+".png";
 				String fileName = sysYear+"/"+sysMonth+"/"+sysYear+sysMonth+sysTime+".png";
 				franchiserMember.setFileName(fileName);
				
 				File lOutFile = new File(srcName);
  			  
			    FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
			    lFileOutputStream.write(bytes);
			    lFileOutputStream.close();
			    
			    
			}catch(IOException ie){
				//Exception 처리
				logger.info("File writing error!" + ie.getMessage());
			}
			logger.info("franchiserMember File upload success! ");
		}else{
			logger.info("File type error! ");
		}
		return franchiserMember;
	}

	

	@Override
	public void savePFLocation(Location location) {
		franchiserDao.savePFLocation(location);
	}



	@Override
	public FranchiserMember setQrcodeFile(FranchiserMember franchiserMember,
			String url) {
		try {
			String sysYear = DateUtil.getSysYear();
 			String sysMonth = DateUtil.getSysMonth();
 			String sysTime = DateUtil.getSysTime();
 			String yearDir = fsResource.getPath()+ sysYear;
 			String targerDir =yearDir +"/"+sysMonth+"/";
 			
 			File createYearDir = new File(fsResource.getPath(), sysYear);
				 try {
				FileUtils.forceMkdir(createYearDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
				 File createMonthDir = new File(yearDir+"/", sysMonth);
				 try {
					 FileUtils.forceMkdir(createMonthDir);
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
				logger.info("타켓DIR: " + targerDir+sysYear+sysMonth+sysTime); 
				
				String srcName = targerDir+sysYear+sysMonth+sysTime+"qr.png";
				String fileName = sysYear+"/"+sysMonth+"/"+sysYear+sysMonth+sysTime+"qr.png";
				franchiserMember.setQrcode(fileName);
				
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			
			
			String[] urlArray = url.split("partner");
		
			logger.info("qrtext:"+urlArray[0]+"place/placeView/"+franchiserMember.getFid());
			logger.info("qrtext:"+urlArray[1]);
			
			
			String qrText = urlArray[0]+"place/placeView/"+franchiserMember.getFid();
			qrText = new String(qrText.getBytes("UTF-8"), "ISO-8859-1");
	        BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE,100, 100);
	        
	        MatrixToImageWriter.writeToFile(bitMatrix, "png",new File(srcName));
	        
	        franchiserDao.updateFranchiserMemberQrcode(franchiserMember);
	        
	        return franchiserMember;
	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return franchiserMember;	
	}



	@Override
	public FranchiserMember getFranchiserFcode(String fcode) {
		// TODO Auto-generated method stub
		return franchiserDao.getFranchiserFcode(fcode);
	}
	
}
