package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.utils.ConfigLoader;

import net.sf.json.JSONObject;

public class DownloadService extends Thread{
	
	private Socket socket;

	public DownloadService(Socket socket) {
		super();
		this.socket = socket;
	}
	
	public void run() {
		//{"uid":xxx,"code":"xxx","fid":"xxx"}
		InputStream input=null;
		OutputStream output=null;
		
		try {
			input=socket.getInputStream();
			output=socket.getOutputStream();
			byte[] buffer=new byte[1024];
			int length = input.read(buffer);
			String jsonStr=new String(buffer,0,length);
			JSONObject json = JSONObject.fromObject(jsonStr);
			
			Long uid=json.getLong("uid");
			Long fid = json.getLong("fid");
			String code=json.getString("code");
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void openDownloadService() throws Exception {
		ServerSocket serverSocket = new ServerSocket(ConfigLoader.getDownloadPort());
		while(true) {
			Socket socket = serverSocket.accept();
			new DownloadService(socket).start();
		}
	}

}
