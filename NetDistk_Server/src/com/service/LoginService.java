package com.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.dao.LoginDao;
import com.domain.Directory;
import com.domain.FileItem;
import com.domain.User;
import com.utils.ConfigLoader;
import com.utils.MD5FileUtil;

import net.sf.json.JSONObject;

public class LoginService extends Thread {
	private Socket socket;
	private LoginDao dao;

	public LoginService(Socket socket) {
		this.socket = socket;
		dao = new LoginDao();
	}

	public void run() {
		InputStream input=null;
		OutputStream output=null;
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
			String reqStr = null;
			JSONObject reqInfo = null;
			User user = null;
			while (true) {
				byte[] buffer = new byte[2048];
				int length = input.read(buffer);
				reqStr = new String(buffer, 0, length);
				reqInfo = JSONObject.fromObject(reqStr);
				String typeInfo = reqInfo.getString("type").trim();
				if (reqInfo.getString("type").trim().equals("login")) {//登录
					String username = reqInfo.getString("email");
					String password = reqInfo.getString("password");
					user = dao.login(username, password);
					String resStr = null;
					if (user != null) {
						String code= MD5FileUtil.getMD5String(System.currentTimeMillis() + "" + user.getUid());
						//在UserOnline中注册串码。
						UserOnline.regUserOnline(code, user.getUid());
						resStr = "{\"type\":\"true\",\"uid\":\"" + user.getUid()
								+ "\",\"initSize\":\"" + user.getInitSize()
								+ "\",\"code\":"+code+" }";
						output.write(resStr.getBytes());
						output.flush();
					} else {
						resStr = "{\"type\":\"false\"}";
						output.write(resStr.getBytes());
						output.flush();
						//如果登陆失败，则终止该线程运行，不让程序进入后面的死循环接收请求
						return;
					}
				} else if (typeInfo.equals("usedSize")) {//获得用户已经使用的内存大小
					Long usedSize;
					try {
						usedSize = dao.getUsedSize(user.getUid());
						String resStr = "{\"type\":\"true\",\"size\":"
								+ usedSize + "}";
						output.write(resStr.getBytes());
					} catch (Exception e) {
						output.write("{\"type\":\"false\"}".getBytes());
						e.printStackTrace();
					}

					output.flush();
				} else if (typeInfo.equals("list")) {//获取目录
					//获取did(获取当前所在目录)
					long did = reqInfo.getLong("did");

					//获得did目录下所有文件夹
					List<Directory> directories;
					//获得did目录下所有文件
					List<FileItem> fileItems;
					try {
						directories = dao.getDirectories(user.getUid(), did);
						fileItems = dao.getFileItems(user.getUid(), did);
						HashMap<String, Object> hashMap = new HashMap<>();
						hashMap.put("rdid", did);
						hashMap.put("directories", directories);
						hashMap.put("fileItems", fileItems);

						JSONObject resObj = JSONObject.fromObject(hashMap);
						output.write(resObj.toString().getBytes());
						output.flush();

					} catch (Exception e) {
						//如果发生异常则回一个错误提示
						output.write("false".getBytes());
						output.flush();
						e.printStackTrace();
					}

				} else if (typeInfo.equals("mkdir")) {//创建文件夹
					//获取当前所在目录的父目录
					long rdid = reqInfo.getLong("rdid");
					//获取文件夹名称
					String dname = reqInfo.getString("dname");
					Directory dir = new Directory(0L, dname, rdid, user.getUid());

					//判断在当前目录下是否有同名文件夹
					if (dao.isExistName(dir)) {
						//将新建的文件夹的父目录设置为传入的rdid
						int row = dao.mkDir(dir);

						if (row > 0) {
							output.write("{\"type\":\"true\"}".getBytes());
						} else {
							output.write(
									"{\"type\":\"false\",\"info\":\"创建失败！\"}"
											.getBytes());
						}
					} else {
						//有重名文件
						output.write("{\"type\":\"false\",\"info\":\"文件夹已存在！\"}"
								.getBytes());
					}
				} else if (typeInfo.equals("removeDir")) {//删除文件夹
					//获得需要删除的文件夹的did
					long did = reqInfo.getLong("did");
					try {
						dao.deleteDir(user.getUid(), did);
						output.write("{\"type\":\"true\"}".getBytes());
						output.flush();
					} catch (SQLException e) {
						output.write("{\"type\":\"false\",\"info\":\"删除失败\"}"
								.getBytes());
						output.flush();
						e.printStackTrace();
					}

				} else if (typeInfo.equals("removeFile")) {//删除文件
					//获得需要删除的文件的fid
					long fid = reqInfo.getLong("fid");
					try {
						dao.deleteFileItem(user.getUid(), fid);
						output.write("{\"type\":\"true\"}".getBytes());
						output.flush();
					} catch (SQLException e) {
						output.write("{\"type\":\"false\",\"info\":\"删除失败\"}"
								.getBytes());
						output.flush();
						e.printStackTrace();
					}
				} else if (typeInfo.equals("renameDir")) {//重命名文件夹
					//获得目标文件夹的did
					Long did = reqInfo.getLong("did");
					//获得文件夹的名称
					String newName = reqInfo.getString("newName");

					if (dao.dirNameIsExist(user.getUid(), did, newName)) {
						dao.reNameDir(did, newName);
						
						output.write("{\"type\":\"true\"}".getBytes());
						output.flush();
					} else {
						output.write(
								"{\"type\":\"false\",\"info\":\"该文件名已存在！\"}"
										.getBytes());
						output.flush();
					}

				} else if (typeInfo.equals("renameFile")) {//重命名文件
					//获得目标文件的fid
					Long fid = reqInfo.getLong("fid");
					//获得文件的名称
					String newName = reqInfo.getString("newName");

					if (dao.fileNameIsExist(user.getUid(), fid, newName)) {
						dao.reNameFile(fid, newName);
						output.write("{\"type\":\"true\"}".getBytes());
						output.flush();
					} else {
						output.write(
								"{\"type\":\"false\",\"info\":\"该文件名已存在！\"}"
										.getBytes());
						output.flush();
					}
				} else if (typeInfo.equals("rePassword")) {//修改密码
					String email = reqInfo.getString("email");
					String password = reqInfo.getString("password");
					String rePassword = reqInfo.getString("rePassword");

					boolean isUpdate = dao.rePassword(email, password,
							rePassword);
					String resStr = null;
					if (isUpdate) {
						resStr = "{\"type\":\"true\"}";
					} else {
						resStr = "{\"type\":\"false\"}";
					}
					output.write(resStr.getBytes());
					output.flush();
				} else if (typeInfo.equals("logout")) {//退出登录
					break;
				}

			}
		} catch (IOException | SQLException e) {
			try {
				output.write("{\"type\":\"error\",\"info\":\"套接字异常，请关闭客户端重试！\"}".getBytes());
				output.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static void openLoginServer() throws IOException {
		ServerSocket serverSocket = new ServerSocket(
				ConfigLoader.getLoginPort());
		while (true) {
			Socket socket = serverSocket.accept();
			new LoginService(socket).start();
		}

	}

}
