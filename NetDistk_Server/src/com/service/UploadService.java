package com.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import com.dao.LoginDao;
import com.dao.UploadDao;
import com.utils.ConfigLoader;

import net.sf.json.JSONObject;

public class UploadService extends Thread{
	
	private Socket socket=null;
	public UploadService(Socket socket) {
		this.socket=socket;
	}
	
	public void run() {
		//{"filesize":"","filename":"","filemd5":"","did":"","code":"","uid":""}
		InputStream input=null;
		OutputStream output=null;
		LoginDao loginDao=new LoginDao();
		UploadDao uploadDao=new UploadDao();
		try {
			input=socket.getInputStream();
			output=socket.getOutputStream();
			byte[] buffer=new byte[2048];
			int length=input.read(buffer);
			String jsonStr=new String(buffer,0,length);
			JSONObject json = JSONObject.fromObject(jsonStr);

			Long fileSize =json.getLong("filesize");
			String code = json.getString("code");
			Long did=json.getLong("did");
			String filemd5=json.getString("filemd5");
			String filename=json.getString("filename");
			Long uid=json.getLong("uid");
			
			//如果该用户没有登录，则关闭该线程
			if(!UserOnline.isUserOnline(code, uid)) {
				return;
			}
			
			Long usedSize=loginDao.getUsedSize(uid);
			Long initSize=loginDao.getInitSize(uid);
			
			if(usedSize+fileSize<=initSize) {
				//向数据库插入用户的文件数据
				uploadDao.regUserFile(filename, uid, did, fileSize, filemd5);
				if(uploadDao.isExistFile(filemd5)) {
					//服务器中已经存在该文件，相应客户端“秒传”
					output.write("{\"type\":\"mc\"}".getBytes());
					output.flush();
				}else {
					output.write("{\"type\":\"pleaseUpload\"}".getBytes());
					output.flush();
					File filePath=new File(ConfigLoader.getFilePath(),filemd5);
					FileOutputStream fileOutput=new FileOutputStream(filePath);
					
					buffer=new byte[1024*1024];
					long size=0l;
					while((length = input.read(buffer))>0) {
						fileOutput.write(buffer, 0, length);
						size+=length;
						if(size>=fileSize) {
							break;
						}
					}
					fileOutput.close();
					//将新上传的文件信息	注册数据库
					uploadDao.regSystemFile(filemd5, fileSize, filePath.getPath(), uid);
					output.write("{\"type\":\"success\"}".getBytes());
					output.flush();
				}
				
			}else {
				output.write("{\"type\":\"overSize\"}".getBytes());
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void openUploadService() throws Exception{
		ServerSocket serverSocket = new ServerSocket(ConfigLoader.getUploadtPort());
		while(true) {
			Socket socket = serverSocket.accept();
			new UploadService(socket).start();
		}
	}
}
