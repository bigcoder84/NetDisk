package com.domain;

public class FileItem {
	private Long fid;
	private String fname;
	private Long did;
	private String file_md5;
	private Long file_size;
	private Long uid;
	
	
	public Long getFid() {
		return fid;
	}
	public void setFid(Long fid) {
		this.fid = fid;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public Long getDid() {
		return did;
	}
	public void setDid(Long did) {
		this.did = did;
	}
	public String getFile_md5() {
		return file_md5;
	}
	public void setFile_md5(String file_md5) {
		this.file_md5 = file_md5;
	}
	public Long getFile_size() {
		return file_size;
	}
	public void setFile_size(Long file_size) {
		this.file_size = file_size;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	
}
