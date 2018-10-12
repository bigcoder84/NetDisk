package com.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.domain.User;
import com.utils.ConfigLoader;

import net.sf.json.JSONObject;

import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;

/**
 * 密码修改界面
 * @author 田 金 东
 *
 */
public class RePasswordDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JPasswordField passwordField;
	private JPasswordField rePasswordField_1;
	private JPasswordField rePasswordField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RePasswordDialog dialog = new RePasswordDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RePasswordDialog() {
		setTitle("修改密码");
		setBounds(742, 360, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 432, 253);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(133, 41, 189, 32);
		contentPanel.add(passwordField);
		
		rePasswordField_1 = new JPasswordField();
		rePasswordField_1.setBounds(133, 86, 189, 32);
		contentPanel.add(rePasswordField_1);
		
		rePasswordField_2 = new JPasswordField();
		rePasswordField_2.setBounds(133, 131, 189, 32);
		contentPanel.add(rePasswordField_2);
		
		JLabel label = new JLabel("原密码：");
		label.setBounds(66, 48, 60, 18);
		contentPanel.add(label);
		
		JLabel label_1 = new JLabel("新密码：");
		label_1.setBounds(66, 93, 60, 18);
		contentPanel.add(label_1);
		
		JLabel label_2 = new JLabel("确认密码：");
		label_2.setBounds(51, 138, 75, 18);
		contentPanel.add(label_2);
		
		JButton button = new JButton("确认修改");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String password=passwordField.getText().trim();
				String rePassword=rePasswordField_1.getText().trim();
				String rePassword1=rePasswordField_2.getText().trim();
				if(!rePassword.equals(rePassword1)) {
					JOptionPane.showMessageDialog(RePasswordDialog.this, "您两次输入的密码不一致！");
					return;
				}
				
				String reqStr="{\"type\":\"rePassword\",\"email\":\""+User.getEmail()+"\",\"password\":\""+password+"\",\"rePassword\":\""+rePassword+"\"}";
				
				try {
					Socket socket = new Socket(ConfigLoader.getServerIP(),ConfigLoader.getServerLoginPort());
					InputStream input = socket.getInputStream();
					OutputStream output = socket.getOutputStream();
					
					output.write(reqStr.getBytes());
					output.flush();
					byte[] buffer=new byte[1024];
					int length = input.read(buffer);
					String resStr = new String(buffer,0,length);
					
					JSONObject json = JSONObject.fromObject(resStr);
					
					if(json.getString("type").trim().equals("false")) {
						JOptionPane.showMessageDialog(RePasswordDialog.this, "原密码输入错误");
						passwordField.setText("");
					}else {
						JOptionPane.showMessageDialog(RePasswordDialog.this, "密码更改成功！");
						RePasswordDialog.this.setResizable(false);
						RePasswordDialog.this.dispose();
					}
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		button.setBounds(167, 200, 113, 27);
		contentPanel.add(button);
	}
}
