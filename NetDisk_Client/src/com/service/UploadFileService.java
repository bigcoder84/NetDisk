package com.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JTable;

import com.domain.User;
import com.utils.ConfigLoader;
import com.utils.FileSizeUtils;
import com.utils.MD5FileUtil;
import com.view.FileListJPanel;
import com.view.MainJFrame;

import net.sf.json.JSONObject;

public class UploadFileService extends Thread {

	private Long uid;
	private String code;
	private File file;
	private Long did;
	private MainJFrame mainJFrame;
	private FileListJPanel fileListJPanel;
	private JTable uploadTable;
	
	//保存上该任务的在上传列表中的信息 
	private Vector<String> uploadRow;

	public UploadFileService(Long uid, String code, File file, Long did,
			MainJFrame mainJFrame,FileListJPanel fileListJPanel,Vector<String> uploadRow,JTable uploadTable) {
		super();
		this.uploadRow=uploadRow;
		this.uid = uid;
		this.code = code;
		this.file = file;
		this.did = did;
		this.mainJFrame = mainJFrame;
		this.fileListJPanel=fileListJPanel;
		this.uploadTable=uploadTable;
	}

	public void run() {
		Socket socket=null;
		try {
			uploadRow.add(file.getName());
			uploadRow.add(file.getPath());
			uploadRow.add(file.length()/1024/1024+"MB");
			FileInputStream fileInput = new FileInputStream(file);
			String filemd5 = MD5FileUtil.getFileMD5String(file);
			//{"filesize":"","filename":"","filemd5":"","did":"","code":"","uid":""}
			String reqStr = "{\"filesize\":\"" + file.length()
					+ "\",\"filename\":\"" + file.getName()
					+ "\",\"filemd5\":\"" + filemd5 + "\",\"did\":\"" + did
					+ "\",\"code\":\"" + code + "\",\"uid\":\"" + uid + "\"}";
			socket = new Socket(ConfigLoader.getServerIP(),ConfigLoader.getUploadtPort());
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			
			output.write(reqStr.getBytes());
			output.flush();
			
			byte[] buffer=new byte[1024];
			int length = input.read(buffer);
			
			String jsonStr=new String(buffer,0,length);
			
			JSONObject json = JSONObject.fromObject(jsonStr);
			String type = json.getString("type");
			if(type.equals("overSize")) {
				//剩余容量大小不足
				javax.swing.JOptionPane.showMessageDialog(mainJFrame, "您的账户容量不足!");
			}else if(type.equals("mc")){
				uploadRow.set(4, "秒传成功！");
			}else if(type.equals("pleaseUpload")) {
				//开始上传
				long s=0l;//开始时间
				long sta=0L;//记录每秒开始时间
				long end=0L;//记录每次分段上传时间
				long startSize=file.length();//文件开始大小
				long uSize=0L;//一秒类文件上传大小
				long oldSize=startSize;//文件的剩余大小
				buffer=new byte[1024*1024];
				
				s=System.currentTimeMillis();
				sta=s;
				while((length=fileInput.read(buffer))>0) {
					output.write(buffer, 0, length);
					uSize+=length;
					oldSize-=length;
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					end=System.currentTimeMillis();
					
					if(end-sta>1000) {
						//算出来的是以MB为单位的
						String speed=FileSizeUtils.getSpeed(uSize);
						uploadRow.set(4, speed);
						uploadRow.set(3, FileSizeUtils.getSize(oldSize));
						uploadTable.updateUI();
						sta=end;
						uSize=0l;
					}
				}
				uploadRow.set(3, "上传完成");
				uploadRow.set(4, "——");
				uploadTable.updateUI();
				
				int len = input.read(buffer);
				jsonStr=new String(buffer,0,len);
				json = JSONObject.fromObject(jsonStr);
			}
			fileListJPanel.updateFileList();
			//重新获得用户已使用大小，然后寄存到User类中
			User.setUsedSize(LoginService.getUsedSize());
			//重新加载主页面的内存大小进度条（它是根据User类中寄存的usedSize属性更新的）
			mainJFrame.updateUsedSize();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(socket!=null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
