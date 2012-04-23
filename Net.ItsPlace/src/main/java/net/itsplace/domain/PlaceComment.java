package net.itsplace.domain;

import java.util.Date;

/**
 * 가맹점 댓글 클래스    <br />
 * 
 * @author 김동훈
 * @version 1.0, 2011. 8. 24.
 * @param email
 * @return  edit.jsp
 * @throws 
 * @see 
 */
public class PlaceComment {

		private String cid; /* 코멘트  */
		private String comment; /* 코멘트 */
		private String email; /* 사용자 이메 */
		private String name; 
		private String fname;
		private String fid;
		private Date inpdate;
		private String writeDate;
		private String profileImageUrl;
		
		
		
		public String getFname() {
			return fname;
		}
		public void setFname(String fname) {
			this.fname = fname;
		}
		public String getProfileImageUrl() {
			return profileImageUrl;
		}
		public void setProfileImageUrl(String profileImageUrl) {
			this.profileImageUrl = profileImageUrl;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getWriteDate() {
			return writeDate;
		}
		public void setWriteDate(String writeDate) {
			this.writeDate = writeDate;
		}
		public String getCid() {
			return cid;
		}
		public void setCid(String cid) {
			this.cid = cid;
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getFid() {
			return fid;
		}
		public void setFid(String fid) {
			this.fid = fid;
		}
		public Date getInpdate() {
			return inpdate;
		}
		public void setInpdate(Date inpdate) {
			this.inpdate = inpdate;
		}
		
		@Override
		public String toString() {
			return "PlaceComment:[cid=" + cid + ", fid= " + fid + ", email= " + email + ", comment= " + comment  + "]"  ;
		}
}
