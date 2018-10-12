package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.utils.C3P0Utils;

/**
 * 上传操作的持久层
 * @author 田 金 东
 *
 */
public class UploadDao {
	
	public boolean isExistFile(String filemd5) throws SQLException {
		Connection con=C3P0Utils.getConnection();
		PreparedStatement prepare = con.prepareStatement("select count(file_md5) from sys_file_items where file_md5=?");
		prepare.setString(1, filemd5);
		ResultSet result = prepare.executeQuery();
		if(result.next()) {
			long num = result.getLong(1);
			if(num>0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 在数据库中注册用户文件信息
	 * @param fileName 文件名
	 * @param uid 用户ID
	 * @param did 所在文件夹ID
	 * @param fileSize 文件大小
	 * @param filemd5 文件的MD5码
	 * @throws SQLException
	 */
	public void regUserFile(String fileName,Long uid,Long did,Long fileSize,String filemd5) throws SQLException {
		Connection con=C3P0Utils.getConnection();
		PreparedStatement prepare = con.prepareStatement("insert into user_file_items(fname,upload_time,uid,did,file_size,file_md5) values (?,SYSDATE(),?,?,?,?)");
		prepare.setString(1, fileName);
		prepare.setLong(2, uid);
		prepare.setLong(3, did);
		prepare.setLong(4, fileSize);
		prepare.setString(5, filemd5);
		prepare.executeUpdate();
	}
	
	/**
	 * 用于在数据库中注册用户首次上传文件的用户信息。
	 * @param filemd5 文件的MD5码（主键）
	 * @param fileSize 文件大小
	 * @param filePath 文件在本地操作系统中存放路径
	 * @param firstUid 第一次上传该文件的用户ID
	 * @throws SQLException
	 */
	public void regSystemFile(String filemd5,Long fileSize,String filePath,Long firstUid) throws SQLException {
		Connection con=C3P0Utils.getConnection();
		PreparedStatement prepare = con.prepareStatement("insert into sys_file_items(file_md5,file_size,file_path,upload_time,uid_first) values (?,?,?,SYSDATE(),?)");
		prepare.setString(1, filemd5);
		prepare.setLong(2, fileSize);
		prepare.setString(3, filePath);
		prepare.setLong(4, firstUid);
		prepare.executeUpdate();
	}
	@Test
	public void test() {
		try {
			regUserFile("你好.xls",1L,0L,1231231L,"dsadasdsadsa");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
