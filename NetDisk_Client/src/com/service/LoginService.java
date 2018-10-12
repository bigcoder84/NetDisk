package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


import com.domain.User;
import com.utils.ConfigLoader;

import net.sf.json.JSONObject;

/**
 * 该类是客户端业务处理的主类
 * @author 田 金 东
 *
 */
public class LoginService extends Thread {
	private static Socket socket;
	private static InputStream input;
	private static OutputStream output;

	
	public LoginService(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		
	}

	public static boolean login(String username, String password)
			throws Exception, IOException {
		socket = new Socket(ConfigLoader.getServerIP(),
				ConfigLoader.getServerLoginPort());
		input = socket.getInputStream();
		output = socket.getOutputStream();
		String reqStr = "{\"type\":\"login\",\"email\":\"" + username
				+ "\",\"password\":\"" + password + "\"}";
		output.write(reqStr.getBytes());
		output.flush();

		byte[] buffer = new byte[1024];
		int len = input.read(buffer);
		String resStr = new String(buffer, 0, len);
		JSONObject resInfo = JSONObject.fromObject(resStr);
		if (resInfo.getString("type").trim().equals("true")) {
			User.setEmail(username);
			User.setUid(resInfo.getLong("uid"));
			User.setInitSize(resInfo.getLong("initSize"));
			User.setUsedSize(getUsedSize());
			//记录用户登录的串码
			User.setCode(resInfo.getString("code"));
			new LoginService(socket).start();//登陆成功后开启线程长时间保持连接
			return true;
		}
		return false;
	}

	/**
	 * 获得该用户已使用的内存大小
	 * @return
	 */
	public static Long getUsedSize() {
		String reqStr = "{\"type\":\"usedSize\"}";
		try {
			output.write(reqStr.getBytes());
			output.flush();
			byte[] buffer = new byte[1024];
			int length = input.read(buffer);
			String resStr = new String(buffer, 0, length);

			JSONObject json = JSONObject.fromObject(resStr);
			if (json.getString("type").trim().equals("true")) {
				return json.getLong("size");
			} else {
				throw new RuntimeException("容量获取发生错误！");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取目录下文件及文件夹列表
	 * @return
	 * @throws Exception 
	 */
	public static String getFileListStr(Long did) throws Exception {
		//id=0表示获取根目录
		String reqStr = "{\"type\":\"list\",\"did\":\"" + did + "\"}";
		try {
			output.write(reqStr.getBytes());
			output.flush();
			byte[] buffer = new byte[4096];
			int length = input.read(buffer);
			String resStr = new String(buffer, 0, length);
			if (resStr.trim().equals("false")) {
				throw new Exception("目录获取发生错误");
			} else {
				return resStr;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		}
	}
	public static void logout() {
		try {
			output.write("{\"type\":\"logout\"}".getBytes());
			output.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static String rename(String type,String id,String newName) throws IOException {
		String reqStr=null;
		if(type.equals("dir")) {
			reqStr="{\"type\":\"renameDir\",\"did\":\""+id+"\",\"newName\":\""+newName+"\"}";
		}else if(type.equals("file")){
			reqStr="{\"type\":\"renameFile\",\"fid\":\""+id+"\",\"newName\":\""+newName+"\"}";
		}
		output.write(reqStr.getBytes());
		output.flush();
		
		byte[] buffer=new byte[1024];
		int length = input.read(buffer);
		String resStr=new String(buffer,0,length);
		return resStr;
	}
	
	public static String delete(String type,String id) throws IOException {
		String reqStr=null;
		if(type.equals("dir")) {
			reqStr="{\"type\":\"removeDir\",\"did\":\""+id+"\"}";
		}else if(type.equals("file")){
			reqStr="{\"type\":\"removeFile\",\"fid\":\""+id+"\"}";
		}
		output.write(reqStr.getBytes());
		output.flush();
		
		byte[] buffer=new byte[1024];
		int length = input.read(buffer);
		String resStr=new String(buffer,0,length);
		return resStr;
	}

	public static String createDir(Long id, String dirName) throws IOException {
		String reqStr="{\"type\":\"mkdir\",\"dname\":\""+dirName+"\",\"rdid\":\""+id+"\"}";
		output.write(reqStr.getBytes());
		output.flush();
		byte[] buffer=new byte[1024];
		int length = input.read(buffer);
		String resStr=new String(buffer,0,length);
		return resStr;
	}
}
