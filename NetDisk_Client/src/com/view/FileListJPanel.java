package com.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.domain.User;
import com.service.LoginService;
import com.service.UploadFileService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FileListJPanel extends JPanel {
	private JTextField pathTextFiled = null;
	
	private DefaultTableModel uploadTableModel; 
	private DefaultTableModel downloadTableModel; 
	private JTable uploadTable;
	private JTable downloadTable;
	private String path="";
	//该id是当前所在文件夹的fdid
	private Long id;
	
	//private String preFileListStr=null;
	
	private JPopupMenu menu = new JPopupMenu();
	private JMenuItem uploadFile = new JMenuItem("上传文件");
	

	/**
	 * Create the panel.
	 */
	public FileListJPanel(JTextField pathTextFiled,DefaultTableModel uploadTableModel,DefaultTableModel downloadTableModel,JTable uploadTable,JTable downloadTable) {
		this.pathTextFiled = pathTextFiled;
		this.uploadTableModel=uploadTableModel;
		this.downloadTableModel=downloadTableModel;
		this.uploadTable=uploadTable;
		this.downloadTable=downloadTable;
		this.setPreferredSize(new Dimension(570, 1500));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		updateFileList("", 0L);
		
		//添加上传按钮
		menu.add(uploadFile);
		
		this.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==3) {
					menu.show(FileListJPanel.this, e.getX(), e.getY());
				}
				
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		
		uploadFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser=new JFileChooser();
				//设置文件的选择模式（只能选择文件）
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.showSaveDialog(MainJFrame.mainJFrame);
				
				File file = chooser.getSelectedFile();
				if(file!=null) {
					Vector<String> uploadRow = new Vector<>();
					uploadRow.add(file.getName());
					uploadRow.add(file.getPath());
					uploadRow.add(file.length()/1024/1024+"MB");
					uploadRow.add(file.length()/1024/1024+"MB");
					uploadTableModel.addRow(uploadRow);
					new UploadFileService(User.getUid(),User.getCode() , file, id, MainJFrame.mainJFrame, FileListJPanel.this,uploadRow,uploadTable).start();
				}
			}
		});
	}
	
	/**
	 * 该方法用于在进入新文件夹时，对视图进行更新
	 * @param fileName
	 * @param id
	 */
	public void updateFileList(String fileName, Long id) {
		
		this.id = id;
		if(id!=0) {
			//当不是根目录时需要设置路径
			path=path+" / "+fileName;
		}
		pathTextFiled.setText(path);
		String fileListStr = null;
		try {
			fileListStr = LoginService.getFileListStr(id);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "文件项载入错误，请重新登录！");
			e.printStackTrace();
		}
		User.setFileListStr(fileListStr);
		
		JSONObject json = JSONObject.fromObject(fileListStr);

		JSONArray dirs = json.getJSONArray("directories");
		JSONArray files = json.getJSONArray("fileItems");

		//清除当前面板中所有组件
		this.removeAll();
		int count = dirs.size() + files.size();

		this.setPreferredSize(new Dimension(570,
				135 * (count / 4 + (count % 4 == 0 ? 0 : 1) + 50)));
		for (int i = 0; i < dirs.size(); i++) {
			JSONObject dir = dirs.getJSONObject(i);
			this.add(new FileJPanel(dir.getString("did"), dir.getString("rdid"),
					"dir", dir.getString("dname"), 0L, this));
		}
		for (int i = 0; i < files.size(); i++) {
			JSONObject file = files.getJSONObject(i);
			this.add(new FileJPanel(file.getString("fid"),
					file.getString("did"), "file", file.getString("fname"),
					file.getLong("file_size"), this));
		}
		this.updateUI();
	}
	
	/**
	 * 该方法用于在创建文件夹后更新视图，与另个一重载方法不同的是，他不需要对路径进行拼接（因为他还是当前目录）
	 */
	public void updateFileList() {
		String fileListStr = null;
		try {
			fileListStr = LoginService.getFileListStr(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "文件项载入错误，请重新登录！");
			e.printStackTrace();
		}
		User.setFileListStr(fileListStr);
		
		JSONObject json = JSONObject.fromObject(fileListStr);

		JSONArray dirs = json.getJSONArray("directories");
		JSONArray files = json.getJSONArray("fileItems");

		//清除当前面板中所有组件
		this.removeAll();
		int count = dirs.size() + files.size();

		this.setPreferredSize(new Dimension(570,
				135 * (count / 4 + (count % 4 == 0 ? 0 : 1) + 50)));
		for (int i = 0; i < dirs.size(); i++) {
			JSONObject dir = dirs.getJSONObject(i);
			this.add(new FileJPanel(dir.getString("did"), dir.getString("rdid"),
					"dir", dir.getString("dname"), 0L, this));
		}
		for (int i = 0; i < files.size(); i++) {
			JSONObject file = files.getJSONObject(i);
			this.add(new FileJPanel(file.getString("fid"),
					file.getString("did"), "file", file.getString("fname"),
					file.getLong("file_size"), this));
		}
		this.updateUI();
	}

	
	
	public void updateFileListByJson(String json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		JSONArray dirs = jsonObj.getJSONArray("directories");
		JSONArray files = jsonObj.getJSONArray("fileItems");
		
		//刷新当前页面所在目录的did;
		id=jsonObj.getLong("rdid");

		//清除当前面板中所有组件
		this.removeAll();
		int count = dirs.size() + files.size();

		this.setPreferredSize(new Dimension(570,
				135 * (count / 4 + (count % 4 == 0 ? 0 : 1) + 50)));
		for (int i = 0; i < dirs.size(); i++) {
			JSONObject dir = dirs.getJSONObject(i);
			this.add(new FileJPanel(dir.getString("did"), dir.getString("rdid"),
					"dir", dir.getString("dname"), 0L, this));
		}
		for (int i = 0; i < files.size(); i++) {
			JSONObject file = files.getJSONObject(i);
			this.add(new FileJPanel(file.getString("fid"),
					file.getString("did"), "file", file.getString("fname"),
					file.getLong("file_size"), this));
		}
		this.updateUI();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
