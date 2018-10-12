package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.utils.C3P0Utils;

/**
 * 该类用于注册业务时数据库的操作
 * @author 田 金 东
 *
 */
public class RegistDao {
	
	/**
	 * 创建用户
	 * @param email
	 * @param password
	 * @throws SQLException
	 */
	public void createUser(String email, String password) throws SQLException {
		String sql = "insert into user(user_email,user_password,user_size) values(?,?,?)";
		Connection connection = C3P0Utils.getConnection();
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setString(1, email);
		prepareStatement.setString(2, password);
		prepareStatement.setString(3, new Long(5000*1024*1024).toString());
		prepareStatement.executeUpdate();
		connection.close();
		prepareStatement.close();
	}

	/**
	 * 判断该email在数据库中是否存在，如果存在返回false(该邮箱已经注册，不能使用),返回true（该邮箱可以使用）
	 * @param infoEmail
	 * @return
	 */
	public boolean checkEmail(String infoEmail) {
		String sql = "select uid from user where user_email=?";
		Connection connection = C3P0Utils.getConnection();
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setString(1, infoEmail);
			ResultSet executeQuery = prepareStatement.executeQuery();
			if(executeQuery.next()) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	

}
