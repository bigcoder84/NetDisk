package com.domain;

/**
 * 封装用户信息
 * @author 田 金 东
 *
 */
public class User {
	private Long uid;
	private String email;
	private Long initSize;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getInitSize() {
		return initSize;
	}
	public void setInitSize(Long initSize) {
		this.initSize = initSize;
	}
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	
}
