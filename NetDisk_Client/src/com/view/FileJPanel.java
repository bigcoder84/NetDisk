package com.view;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.domain.User;
import com.service.LoginService;

import net.sf.json.JSONObject;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * 该类用于显示文件项
 * @author 田 金 东
 *
 */
public class FileJPanel extends JPanel {
	private String id;
	private String rid;
	private String type;
	private String fileName;
	private Long size;
	private FileListJPanel fileListJPanel;
	

	//右键菜单
	JPopupMenu fileMenu = new JPopupMenu();
	JPopupMenu dirMenu = new JPopupMenu();
	JMenuItem renameDir = new JMenuItem("重命名");
	JMenuItem deleteDir = new JMenuItem("删除文件夹");
	
	
	JMenuItem renameFile = new JMenuItem("重命名");
	JMenuItem deleteFile = new JMenuItem("删除文件");
	JMenuItem downloadFile = new JMenuItem("下载文件");
	
	/**
	 * Create the panel.
	 */
	public FileJPanel(String id,String rid,String type,String fileName,Long size,FileListJPanel fileListJPanel) {
		this.id=id;
		this.fileName=fileName;
		this.rid=rid;
		this.type=type;
		this.size=size;
		this.fileListJPanel=fileListJPanel;
		setLayout(null);
		setPreferredSize(new Dimension(108, 126));
		JLabel imgLabel = new JLabel("");
		imgLabel.setBounds(10, 10, 88, 88);
		add(imgLabel);
		
		JLabel lblNewLabel = new JLabel(fileName,JLabel.CENTER);
		lblNewLabel.setBounds(0, 98, 108, 18);
		add(lblNewLabel);
		
		//为右键菜单添加项
		dirMenu.add(renameDir);
		dirMenu.add(deleteDir);
		
		fileMenu.add(renameFile);
		fileMenu.add(downloadFile);
		fileMenu.add(deleteFile);
		
		renameFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(MainJFrame.mainJFrame, "重命名", fileName);
				if(newName!=null&&!newName.equals("")) {
					try {
						String resStr = LoginService.rename(type, id, newName);
						JSONObject obj = JSONObject.fromObject(resStr);
						
						if(obj.getString("type").trim().equals("true")) {
							FileJPanel.this.fileName=newName;
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "重命名成功！");
							fileListJPanel.updateFileList();
							fileListJPanel.updateUI();
						}else {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, obj.getString("info"));
						}
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "网络异常！");
					}
				}
			}
		});
		
		renameDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(MainJFrame.mainJFrame, "重命名", fileName);
				if(newName!=null&&!newName.equals("")) {
					try {
						String resStr = LoginService.rename(type, id, newName);
						JSONObject obj = JSONObject.fromObject(resStr);
						
						if(obj.getString("type").trim().equals("true")) {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "重命名成功！");
							FileJPanel.this.fileName=newName;
							fileListJPanel.updateFileList();
							fileListJPanel.updateUI();
						}else {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, obj.getString("info"));
						}
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "网络异常！");
					}
				}
			}
		});
		
		
		deleteDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int click = JOptionPane.showConfirmDialog(MainJFrame.mainJFrame, "确定要删除 \""+fileName+"\" 文件吗？", "警告", JOptionPane.YES_NO_OPTION);
				if(click==0) {
					try {
						String resStr = LoginService.delete("dir", id);
						JSONObject obj = JSONObject.fromObject(resStr);
						if(obj.getString("type").trim().equals("true")) {
							fileListJPanel.updateFileList();
							fileListJPanel.updateUI();
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "删除成功！");
						}else {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, obj.getString("info"));
						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "网络异常！");
					}
				}
			}
		});
		deleteFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int click = JOptionPane.showConfirmDialog(MainJFrame.mainJFrame, "确定要删除 \""+fileName+"\" 文件夹吗？", "警告", JOptionPane.YES_NO_OPTION);
				if(click==0) {
					try {
						String resStr = LoginService.delete("file", id);
						JSONObject obj = JSONObject.fromObject(resStr);
						if(obj.getString("type").trim().equals("true")) {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "删除成功！");
							User.setUsedSize(LoginService.getUsedSize());
							((MainJFrame) MainJFrame.mainJFrame).updateUsedSize();
							fileListJPanel.updateFileList();
							fileListJPanel.updateUI();
						}else {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame, obj.getString("info"));
						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainJFrame.mainJFrame, "网络异常！");
					}
				}
			}
		});
		
		
		if(type.equals("dir")) {
			imgLabel.setIcon(new ImageIcon("image/icon_list_folder.png"));
			//为“文件夹”添加鼠标监听
			this.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					int temp = e.getButton();
					if(temp==1) {
						Long did = Long.valueOf(id);
						User.getPathStack().push(User.getFileListStr());
						//如果为左键点击
						fileListJPanel.updateFileList(fileName, did);
						try {
							Thread.currentThread().sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}else if(temp==3){
						//如果为右键点击
						dirMenu.show(FileJPanel.this, e.getX(), e.getY());
					}
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					//鼠标移入主键时变成小手
					FileJPanel.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
		}else if(type.equals("file")) {
			//为文件项添加鼠标监听
			this.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					fileMenu.show(FileJPanel.this, e.getX(), e.getY());
				}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {
					//鼠标移入主键时变成小手
					FileJPanel.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
			//如果该项为文件，则设置提示信息，提示信息显示该文件大小
			if(size>=0&&size<1024) {
				this.setToolTipText(size+"B");
			}else if(size<1024*1024) {
				this.setToolTipText((size/1024)+"KB");
			}else if(size<1024*1024*1024) {
				this.setToolTipText((size/1024/1024)+"MB");
			}
			if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".bmp")
					|| fileName.toLowerCase().endsWith(".jepg") || fileName.toLowerCase().endsWith(".gif")
					|| fileName.toLowerCase().endsWith(".png")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_image.png"));
			} else if (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_doc.png"));
			} else if (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_excel.png"));
			} else if (fileName.toLowerCase().endsWith(".html") || fileName.toLowerCase().endsWith(".htm")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_html.png"));
			} else if (fileName.toLowerCase().endsWith(".pdf")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_pdf.png"));
			} else if (fileName.toLowerCase().endsWith(".vsd")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_visio.png"));
			} else if (fileName.toLowerCase().endsWith(".vcf")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_vcard.png"));
			} else if (fileName.toLowerCase().endsWith(".ppt") || fileName.toLowerCase().endsWith(".pptx")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_ppt.png"));
			} else if (fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".wma")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_audiofile.png"));
			} else if (fileName.toLowerCase().endsWith(".mp4") || fileName.toLowerCase().endsWith(".rm")
					|| fileName.toLowerCase().endsWith(".rmvb") || fileName.toLowerCase().endsWith(".avi")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_videofile.png"));
			} else if (fileName.toLowerCase().endsWith(".rar") || fileName.toLowerCase().endsWith(".jar")
					|| fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".7z")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_compressfile.png"));
			} else if (fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".log")
					|| fileName.toLowerCase().endsWith(".ini")) {
				imgLabel.setIcon(new ImageIcon("image/icon_list_txtfile.png"));
			} else {
				imgLabel.setIcon(new ImageIcon("image/icon_list_unknown.png"));
			}
		}
	}

	

}
