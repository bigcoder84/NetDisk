package com.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Utils {
	private static DataSource dataSource;
	static {
		//在new这个对象时，底层会自动找到c3po-config.xml的配置文件
		dataSource=new ComboPooledDataSource();
	}
	public static DataSource getDataSource() {
		return dataSource;
	}
	public static Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}