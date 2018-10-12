package com.view;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.service.LoginService;
import com.utils.CheckEmail;
import com.utils.ConfigLoader;

import net.sf.json.JSONObject;

/**
 * 登录页面
 * @author 田 金 东
 *
 */
public class LoginJDialog extends JDialog {

	private JPasswordField passwordField;
	private JTextField usernameField;
	private JTextField regist_email;
	private JTextField regist_checkCode;
	private JPasswordField regist_password;
	private JPasswordField regist_repassword;

	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginJDialog dialog = new LoginJDialog();
					dialog.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							System.exit(0);
						}
					});
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog
	 */
	public LoginJDialog() {
		super();
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setTitle("登录菜鸟网盘 v1.0");
		setBounds(680, 360, 416, 240);

		usernameField = new JTextField("");
		usernameField.setBounds(105, 50, 224, 28);
		getContentPane().add(usernameField);

		passwordField = new JPasswordField();
		passwordField.setBounds(105, 91, 224, 27);
		getContentPane().add(passwordField);

		final JButton button = new JButton();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (LoginJDialog.this.getHeight() == 240) {
					LoginJDialog.this.setSize(416, 563);
				} else if (LoginJDialog.this.getHeight() == 563) {
					LoginJDialog.this.setSize(416, 240);
				}

			}
		});
		button.setText("注册账户");
		button.setBounds(225, 147, 106, 28);
		getContentPane().add(button);

		final JButton button_1 = new JButton();
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username=usernameField.getText().trim();
				String password=passwordField.getText().trim();
				if(username.equals("")||password.equals("")) {
					JOptionPane.showMessageDialog(LoginJDialog.this, "用户名或密码不能为空！");
				}else {
					boolean isLogin=false;
					try {
						isLogin = LoginService.login(username, password);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if(isLogin) {
						LoginJDialog.this.setResizable(false);
						LoginJDialog.this.dispose();
						MainJFrame.start();
					}else {
						JOptionPane.showMessageDialog(LoginJDialog.this, "用户名或密码错误！");
						usernameField.setText("");
						passwordField.setText("");
					}
				}
			}
		});
		button_1.setText("登录");
		button_1.setBounds(105, 147, 106, 28);
		getContentPane().add(button_1);

		final JLabel label = new JLabel();
		label.setText("忘记密码？");
		label.setBounds(254, 120, 75, 18);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		getContentPane().add(label);

		JLabel label_1 = new JLabel("用户名：");
		label_1.setBounds(47, 55, 60, 18);
		getContentPane().add(label_1);

		JLabel label_2 = new JLabel("密  码:");
		label_2.setBounds(47, 95, 60, 18);
		getContentPane().add(label_2);

		JPanel loginPanel = new JPanel();
		loginPanel.setBorder(new TitledBorder(null, "\u7528\u6237\u6CE8\u518C",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		loginPanel.setToolTipText("");
		loginPanel.setBounds(14, 225, 370, 291);
		getContentPane().add(loginPanel);
		loginPanel.setLayout(null);

		JLabel label_3 = new JLabel("邮箱：");
		label_3.setBounds(37, 46, 45, 18);
		loginPanel.add(label_3);

		regist_email = new JTextField();
		regist_email.setBounds(86, 40, 221, 31);
		loginPanel.add(regist_email);
		regist_email.setColumns(10);

		JLabel label_4 = new JLabel("验证码：");
		label_4.setBounds(22, 103, 60, 18);
		loginPanel.add(label_4);

		regist_checkCode = new JTextField();
		regist_checkCode.setColumns(10);
		regist_checkCode.setBounds(86, 97, 99, 31);
		loginPanel.add(regist_checkCode);

		JButton button_2 = new JButton("获取验证码");
		button_2.setBounds(200, 99, 107, 27);
		loginPanel.add(button_2);
		button_2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				InputStream input;
				OutputStream output;
				String email=regist_email.getText().trim();
				try {
					if(email.equals("")) {
						JOptionPane.showMessageDialog(LoginJDialog.this, "邮箱不能为空");
					}else if(!CheckEmail.checkEmail(email)){
						JOptionPane.showMessageDialog(LoginJDialog.this, "请输入正确的邮箱格式");
					}else {
						Socket socket=new Socket(ConfigLoader.getServerIP(), ConfigLoader.getServerRegistPort());
						input=socket.getInputStream();
						output=socket.getOutputStream();
						String reqInfo="{\"type\":\"checkCode\",\"email\":\""+email+"\"}";
						output.write(reqInfo.getBytes());

						byte[] buffer=new byte[2048];
						int len = input.read(buffer);
						String infoStr = new String(buffer,0,len);
						
						JSONObject info = JSONObject.fromObject(infoStr);
						String typeInfo = info.getString("type");
						if(typeInfo.trim().equals("true")) {
							JOptionPane.showMessageDialog(LoginJDialog.this, "验证码发送成功！");
						}else {
							String errorInfo = info.getString("info");
							JOptionPane.showMessageDialog(LoginJDialog.this, errorInfo);
						}
						output.flush();
						output.close();
						input.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JLabel label_5 = new JLabel("密码：");
		label_5.setBounds(37, 158, 45, 18);
		loginPanel.add(label_5);

		JLabel label_6 = new JLabel("确认密码:");
		label_6.setBounds(10, 210, 72, 18);
		loginPanel.add(label_6);

		JButton button_3 = new JButton("注册");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String email=regist_email.getText().trim();
				String checkCode=regist_checkCode.getText().trim();
				String password=regist_password.getText().trim();
				String repassword=regist_repassword.getText().trim();
				
				if(email.equals("")||checkCode.equals("")||password.equals("")||repassword.equals("")){
					JOptionPane.showMessageDialog(LoginJDialog.this, "输入信息不完整！");
					return;
				}
				if(!repassword.equals(password)) {
					JOptionPane.showMessageDialog(LoginJDialog.this, "两次密码不相同，请重新输入");
					regist_password.setText("");
					regist_repassword.setText("");
				}
				Socket socket=null;
				InputStream input=null;
				OutputStream output=null;
				try {
					socket = new Socket(ConfigLoader.getServerIP(),ConfigLoader.getServerRegistPort());
					input=socket.getInputStream();
					output=socket.getOutputStream();
					//请求服务器创建用户
					String str="{\"type\":\"reg\",\"email\":\""+email+"\",\"password\":\""+password+"\",\"checkCode\":\""+checkCode+"\"}";
					
					output.write(str.getBytes());
					output.flush();

					//----接收服务器响应
					byte[] buffer=new byte[1024];
					int len = input.read(buffer);
					String infoStr=new String(buffer,0,len);
					JSONObject info = JSONObject.fromObject(infoStr);
					String typeInfo = info.getString("type");
					if(typeInfo.trim().equals("true")) {
						JOptionPane.showMessageDialog(LoginJDialog.this, "注册成功");
						LoginJDialog.this.setSize(416, 240);
					}else if(typeInfo.trim().equals("false")){
						JOptionPane.showMessageDialog(LoginJDialog.this, info.getString("info"));
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		button_3.setBounds(86, 256, 99, 27);
		loginPanel.add(button_3);

		JButton button_4 = new JButton("取消");
		button_4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LoginJDialog.this.setSize(416, 240);
				regist_email.setText("");
				regist_password.setText("");
				regist_repassword.setText("");
				regist_checkCode.setText("");
			}
		});
		button_4.setBounds(208, 256, 99, 27);
		loginPanel.add(button_4);

		regist_password = new JPasswordField();
		regist_password.setBounds(86, 155, 221, 31);
		loginPanel.add(regist_password);

		regist_repassword = new JPasswordField();
		regist_repassword.setBounds(86, 207, 221, 31);
		loginPanel.add(regist_repassword);
	}
}
