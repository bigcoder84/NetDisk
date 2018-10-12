package com.domain;

public class Directory {
	private Long did;
	private String dname;
	private Long rdid;
	private Long uid;
	
	
	public Long getDid() {
		return did;
	}
	public void setDid(Long did) {
		this.did = did;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public Long getRdid() {
		return rdid;
	}
	public void setRdid(Long rdid) {
		this.rdid = rdid;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public Directory(Long did, String dname, Long rdid, Long uid) {
		super();
		this.did = did;
		this.dname = dname;
		this.rdid = rdid;
		this.uid = uid;
	}
	
	
}
