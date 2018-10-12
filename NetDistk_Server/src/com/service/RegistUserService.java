package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.dao.RegistDao;
import com.utils.ConfigLoader;
import com.utils.EmailUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @author 田 金 东
 *
 */
public class RegistUserService extends Thread{

	private Socket socket;
	private RegistDao dao;
	private Integer checkCode;
	private Map<String,String> mapping=null;
 	
	public RegistUserService(Socket socket,Map mapping) {
		this.mapping=mapping;
		this.socket = socket;
		this.dao=new RegistDao();
	}

	@Override
	public void run() {
		InputStream input;
		OutputStream output;
		try {
			input=socket.getInputStream();
			output=socket.getOutputStream();
			byte[] buffer=new byte[2048];
			int len = input.read(buffer);
			String infoStr=new String(buffer,0,len);
			JSONObject obj = JSONObject.fromObject(infoStr);
			String infoType=obj.getString("type");
			//如果为注册请求
			if(infoType.trim().equals("reg")) {
				String email=obj.getString("email");
				String checkCode = obj.getString("checkCode");
				String password=obj.getString("password");
				
				if(email!=null&&checkCode!=null&&password!=null) {
					if(checkCode.trim().equals(mapping.get(email))) {
						try {
							dao.createUser(email, password);
							output.write("{\"type\":\"true\"}".getBytes());
						} catch (SQLException e) {
							
							//数据库操作失败，发送响应（系统异常，注册失败）
							output.write("{\"type\":\"false\",\"info\":\"系统异常，注册失败\"}".getBytes());
							output.flush();
							e.printStackTrace();
						}
					}else {
						//当请求验证码和缓存中不一致时，发送响应（验证码有误，请重新获得验证码）
						output.write("{\"type\":\"false\",\"info\":\"验证码有误，请重新获得验证码\"}".getBytes());
						output.flush();
					}
					mapping.remove(email);
				}else {
					//当获取的email，checkCode，password为空时，发送响应（请求失败，请重试）
					output.write("{\"type\":\"false\",\"info\":\"请求失败，请重新获得验证码注册\"}".getBytes());
					output.flush();
				}
			//获取验证码请求
			}else if(infoType.trim().equals("checkCode")) {
				String infoEmail = obj.getString("email");
				Random random=new Random();
				Integer checkCode = random.nextInt(100000);
				
				if(dao.checkEmail(infoEmail)) {
					EmailUtils.sendEmail(infoEmail, checkCode.toString());
					//将验证码加入缓存，为了注册请求时验证该验证码
					mapping.put(infoEmail, checkCode.toString());
					output.write("\"type\":\"true\"".getBytes());
					output.flush();
				}else {
					output.write("{\"type\":\"false\",\"info\":\"该邮箱已经注册！\"}".getBytes());
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void openRegistService() throws IOException {
		ServerSocket serverSocket = new ServerSocket(ConfigLoader.getRegistPort());
		Map<String,String> mapping=Collections.synchronizedMap(new HashMap<String,String>());
		while(true) {
			Socket accept = serverSocket.accept();
			new RegistUserService(accept,mapping).start();
		}
	}
}
