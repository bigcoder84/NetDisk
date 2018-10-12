package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class ConfigLoader {
	
	private static Properties properties=null;
	private static String IP;
	private static Integer registPort;
	private static Integer loginPort;
	private static Integer uploadtPort;
	private static Integer downloadPort;
	
	static {
		properties=new Properties();
		try {
			properties.load(new FileInputStream(new File("src/config.properties")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IP = properties.getProperty("server_ip");
		registPort=Integer.valueOf(properties.getProperty("server_regist_port"));
		loginPort=Integer.valueOf(properties.getProperty("server_login_port"));
		uploadtPort=Integer.valueOf(properties.getProperty("server_upload_port"));
		downloadPort=Integer.valueOf(properties.getProperty("server_download_port"));
	}
	public static String getServerIP() {
		return IP;
	}
	public static Integer getServerRegistPort() {
		return registPort;
	}
	public static Integer getServerLoginPort() {
		return loginPort;
	}
	
	public static Integer getUploadtPort() {
		return uploadtPort;
	}
	public static Integer getDownloadPort() {
		return downloadPort;
	}
	
	@Test
	public void test() {
		System.out.println(ConfigLoader.getUploadtPort());
	}
}
