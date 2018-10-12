package test;

import java.sql.Connection;

import javax.mail.MessagingException;

import org.junit.Test;

import com.utils.EmailUtils;


public class HelloTest {
	@Test
	public void test() {
		//Connection connection = C3P0Utils.getConnection();
		try {
			EmailUtils.sendEmail("1804972747@qq.com", "41525465");
			System.out.println("dassdas");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
