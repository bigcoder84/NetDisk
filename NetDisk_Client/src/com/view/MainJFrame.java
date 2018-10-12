package com.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.util.Vector;
import javax.swing.JTable;

import com.domain.User;
import com.service.LoginService;

import net.sf.json.JSONObject;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 * 程序运行的主界面
 * @author 田 金 东
 *
 */
public class MainJFrame extends JFrame {

	private JTable uploadTable;
	private JTable downloadTable;
	public static MainJFrame mainJFrame = null;
	private Vector<Vector<String>> uploadRows=new Vector<>();
	private Vector<Vector<String>> downloadRows=new Vector<>();
	private DefaultTableModel uploadTableModel=null;
	private DefaultTableModel downloadTableModel=null;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainJFrame frame = new MainJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainJFrame frame = new MainJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	final JProgressBar progressBar;
	private JTextField pathTextFiled;
	

	public void updateUsedSize() {
		progressBar.setMaximum((int) (User.getInitSize() / 1024 / 1024));
		progressBar.setValue((int) ((User.getInitSize() - User.getUsedSize())
				/ 1024 / 1024));
		progressBar.setString(
				(User.getInitSize() - User.getUsedSize()) / 1024 / 1024 + "MB /"
						+" "+ User.getInitSize() / 1024 / 1024 + "MB");
	}

	/**
	 * Create the frame
	 */
	public MainJFrame() {
		super();
		mainJFrame = this;
		setResizable(false);
		setTitle("菜鸟网盘 V1.0");
		getContentPane().setLayout(null);
		setBounds(648, 230, 614, 592);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JLabel userInfo = new JLabel();
		userInfo.setText("用户名:" + User.getEmail());
		userInfo.setBounds(20, 10, 279, 27);
		getContentPane().add(userInfo);

		final JLabel label_1 = new JLabel();
		label_1.setText("容量大小:");
		label_1.setBounds(20, 50, 78, 27);
		getContentPane().add(label_1);

		progressBar = new JProgressBar();
		progressBar.setForeground(new Color(46, 139, 87));
		progressBar.setStringPainted(true);
		updateUsedSize();
		progressBar.setBounds(104, 51, 482, 27);
		getContentPane().add(progressBar);

		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(10, 84, 584, 460);
		getContentPane().add(tabbedPane);

		final JPanel netDiskPanel = new JPanel();
		netDiskPanel.setLayout(new BorderLayout());
		tabbedPane.addTab("我的网盘", null, netDiskPanel, null);

		JPanel panel = new JPanel();
		netDiskPanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel(" 路径： ");
		panel.add(lblNewLabel, BorderLayout.WEST);

		pathTextFiled = new JTextField();
		pathTextFiled.setEditable(false);
		panel.add(pathTextFiled, BorderLayout.CENTER);
		pathTextFiled.setColumns(10);

		/*上传面板*/
		final JPanel uploadPanel = new JPanel();
		uploadPanel.setLayout(new BorderLayout());
		tabbedPane.addTab("上传任务", null, uploadPanel, null);

		final JScrollPane uploadScrollPane = new JScrollPane();
		uploadPanel.add(uploadScrollPane, BorderLayout.CENTER);

		Vector<String> col1 = new Vector<>();
		col1.add("文件名称");
		col1.add("存放路径");
		col1.add("大小");
		col1.add("剩余");
		col1.add("速度");
		uploadTable = new JTable();
		uploadTableModel = new DefaultTableModel(uploadRows,col1);
		uploadTable.setModel(uploadTableModel);
		uploadScrollPane.setViewportView(uploadTable);
		
		/*下载面板*/
		JScrollPane scrollPane_2 = new JScrollPane();
		netDiskPanel.add(scrollPane_2, BorderLayout.CENTER);
		
		final JPanel downloadPanel = new JPanel();
		downloadPanel.setLayout(new BorderLayout());
		tabbedPane.addTab("下载任务", null, downloadPanel, null);

		final JScrollPane downloadScrollPane = new JScrollPane();
		downloadPanel.add(downloadScrollPane, BorderLayout.CENTER);

		Vector<String> col = new Vector<>();
		col.add("文件名称");
		col.add("保存路径");
		col.add("大小");
		col.add("剩余");
		col.add("速度");

		downloadTable = new JTable();
		downloadTableModel = new DefaultTableModel(downloadRows,col);
		downloadTable.setModel(downloadTableModel);
		downloadScrollPane.setViewportView(downloadTable);


		/*  添加文件项面板*/
		FileListJPanel fileListJPanel = new FileListJPanel(pathTextFiled,uploadTableModel,downloadTableModel,uploadTable,downloadTable);

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new BorderLayout(0, 0));

		JButton btnNewButton_1 = new JButton("<<");
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (User.getPathStack().size() == 0) {
					JOptionPane.showMessageDialog(MainJFrame.this,
							"当前目录已是根目录！");
				} else {
					String pop = User.getPathStack().pop();
					User.setFileListStr(pop);

					String path = pathTextFiled.getText();
					//去掉当前目录的字符串（即去掉最后一个 /**）
					String currentPath = path.substring(0,
							path.lastIndexOf("/"));
					pathTextFiled.setText(currentPath);
					fileListJPanel.setPath(currentPath);
					fileListJPanel.updateFileListByJson(pop);
				}
			}
		});
		panel_1.add(btnNewButton_1, BorderLayout.WEST);

		JButton createDirButton = new JButton("新建文件夹");
		createDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String dirName = JOptionPane.showInputDialog(
						MainJFrame.mainJFrame, "文件夹名", "新建文件夹",
						JOptionPane.QUESTION_MESSAGE);
				if (dirName != null && !dirName.equals("")) {
					try {
						String resStr = LoginService
								.createDir(fileListJPanel.getId(), dirName);
						JSONObject json = JSONObject.fromObject(resStr);
						if (json.getString("type").equals("false")) {
							JOptionPane.showMessageDialog(MainJFrame.mainJFrame,
									json.getString("info"));
						}

						fileListJPanel.updateFileList();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(MainJFrame.mainJFrame,
								"网络异常");

						e1.printStackTrace();
					}
				}
			}
		});
		panel_1.add(createDirButton, BorderLayout.CENTER);

		JButton btnNewButton = new JButton("刷新");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(User.getFileListStr());
				JSONObject json = JSONObject.fromObject(User.getFileListStr());
				fileListJPanel.setId(json.getLong("rdid"));
				fileListJPanel.updateFileList();
				JOptionPane.showMessageDialog(MainJFrame.this, "刷新成功！");
			}
		});
		panel_1.add(btnNewButton, BorderLayout.EAST);
		scrollPane_2.getViewport().setView(fileListJPanel);
		scrollPane_2.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		

		final JButton button = new JButton();
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				int click = JOptionPane.showConfirmDialog(MainJFrame.this,
						"确定要退出吗？", "警告", JOptionPane.YES_NO_OPTION);
				System.out.println(click);
				if (click == 0) {
					LoginService.logout();
					MainJFrame.this.dispose();
				}
			}
		});
		button.setText("退出网盘");
		button.setBounds(480, 9, 106, 28);
		getContentPane().add(button);

		final JButton button_1_1 = new JButton();
		button_1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RePasswordDialog.main(null);
			}
		});
		button_1_1.setText("修改密码");
		button_1_1.setBounds(360, 9, 106, 28);
		getContentPane().add(button_1_1);
	}
}
