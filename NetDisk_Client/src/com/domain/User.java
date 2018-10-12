package com.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于保存当前登录客户端的用户信息
 * @author 田 金 东
 *
 */
public class User {
	private static String email;
	private static Long uid;
	private static Long initSize;
	private static Long usedSize;
	private static String fileListStr;
	private static String code;//用户的串码
	private static LinkedList<String> pathStack=new LinkedList<>();
	
	
	
	public static String getEmail() {
		return email;
	}
	public static void setEmail(String email) {
		User.email = email;
	}
	public static Long getInitSize() {
		return initSize;
	}
	public static void setInitSize(Long initSize) {
		User.initSize = initSize;
	}
	public static Long getUsedSize() {
		return usedSize;
	}
	public static void setUsedSize(Long usedSize) {
		User.usedSize = usedSize;
	}
	public static Long getUid() {
		return uid;
	}
	public static void setUid(Long uid) {
		User.uid = uid;
	}
	public static String getFileListStr() {
		return fileListStr;
	}
	public static void setFileListStr(String fileListStr) {
		User.fileListStr = fileListStr;
	}
	public static LinkedList<String> getPathStack() {
		return pathStack;
	}
	public static void setPathStack(LinkedList<String> pathStack) {
		User.pathStack = pathStack;
	}
	public static String getCode() {
		return code;
	}
	public static void setCode(String code) {
		User.code = code;
	}
	
}
