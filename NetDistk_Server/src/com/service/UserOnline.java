package com.service;

import java.util.HashMap;

public class UserOnline {
	private static HashMap<String,Long> map=new HashMap<>();
	
	/**
	 * 
	 * @param code 客户端登录时，服务器生成的串码,在登录时注册
	 * @param uid 该串码对应用户的uid
	 */
	public static void regUserOnline(String code,Long uid) {
		synchronized (map) {
			map.put(code, uid);
		}
	}
	
	public static boolean isUserOnline(String code,Long uid) {
		Long id = map.get(code);
		if(id!=null&&id==uid) {
			return true;
		}
		return false;
	}
	
	public static Long getOnlineUid(String code) {
		
		return map.get(code);
	}
	
	public static void logout(String code) {
		map.remove(code);
	}
}
