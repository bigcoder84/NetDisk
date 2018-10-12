package com.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


public class DownloadFileService extends Thread{

	private Long uid;
	private String code;
	private Long fid;
	private File file;
	private MainJFrame mainJFrame;
	private Vector<String> downloadRow;
	private JTable downloadTable;
		
	
	
	public DownloadFileService(Long uid, String code,Long fid, File file,
			MainJFrame mainJFrame, Vector<String> downloadRow,
			JTable downloadTable) {
		super();
		this.uid = uid;
		this.code = code;
		this.file = file;
		this.fid=fid;
		this.mainJFrame = mainJFrame;
		this.downloadRow = downloadRow;
		this.downloadTable = downloadTable;
	}



	public void run() {
		Socket socket=null;
		InputStream input=null;
		OutputStream output=null;
		try {
			downloadRow.add(file.getName());
			downloadRow.add(file.getPath());
			downloadRow.add(file.length()/1024/1024+"MB");
			FileOutputStream fileOutput = new FileOutputStream(file);
			
			String reqStr="{\"uid\":\""+uid+"\",\"code\":\""+code+"\",\"fid\":\""+fid+"\"}";
			socket=new Socket(ConfigLoader.getServerIP(),ConfigLoader.getDownloadPort());
			
			input=socket.getInputStream();
			output=socket.getOutputStream();
			
			output.write(reqStr.getBytes());
			output.flush();
			
			
			byte[] buffer=new byte[1024];
			int length = input.read(buffer);
			String jsonStr=new String(buffer,0,length);
			JSONObject.fromObject(jsonStr);
			
			//开始上传
			long s=0l;//开始时间
			long sta=0L;//记录每秒开始时间
			long end=0L;//记录每次分段下载时间
			long startSize=file.length();//文件开始大小
			long uSize=0L;//一秒类文件上传大小
			long oldSize=startSize;//文件的剩余大小
			buffer=new byte[1024*1024];
			
			s=System.currentTimeMillis();
			sta=s;
			while((length=input.read(buffer))>0) {
				fileOutput.write(buffer, 0, length);
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				end=System.currentTimeMillis();
				
				if(end-sta>1000) {
					//算出来的是以MB为单位的
					downloadRow.set(4, FileSizeUtils.getSpeed(uSize));
					downloadRow.set(3, FileSizeUtils.getSize(oldSize));
					downloadTable.updateUI();
					sta=end;
					uSize=0l;
				}
			}
			downloadRow.set(3, "下载完成");
			downloadRow.set(4, "——");
			//刷新表格
			downloadTable.updateUI();
			
			
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
