package com.utils;
import org.apache.commons.mail.HtmlEmail;

public class EmailUtils {

	public static void sendEmail(String emailaddress, String code) {

		try {
			HtmlEmail email = new HtmlEmail();
			email.setHostName("smtp.126.com");
			email.setCharset("UTF-8");
			email.addTo(emailaddress);// 收件地址

			email.setFrom("tianjindong98@126.com", "菜鸟网盘注册验证码");

			email.setAuthentication("tianjindong98@126.com", "tjd123456789");

			email.setSubject("菜鸟网盘注册验证码");
			email.setMsg("验证码是:" + code+"\n\n请勿将验证码透露给第三方!");
			email.send();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	
}
