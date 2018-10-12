package com.dao;

import static org.hamcrest.CoreMatchers.nullValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.domain.Directory;
import com.domain.FileItem;
import com.domain.User;
import com.utils.C3P0Utils;

/**
 * 该类用于登录操作中的数据库操作
 * @author 田 金 东
 *
 */
public class LoginDao {

	public User login(String username, String password) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		String sql = "select uid,user_email,user_size from user  where user_email=? and user_password=?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setString(1, username);
		prepareStatement.setString(2, password);
		ResultSet result = prepareStatement.executeQuery();
		if (result.next()) {
			User user = new User();
			user.setUid(result.getLong("uid"));
			user.setEmail(result.getString("user_email"));
			user.setInitSize(result.getLong("user_size"));
			return user;
		}
		connection.close();
		return null;
	}

	public Long getUsedSize(Long uid) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		String sql = "select sum(file_size) from user_file_items where uid=?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setLong(1, uid);
		ResultSet query = prepareStatement.executeQuery();
		Long size = null;
		if (query.next()) {
			size = query.getLong(1);
		}
		connection.close();
		return size;
	}

	public boolean rePassword(String username, String password,
			String rePassword) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		String sql = "update user set user_password=? where user_email=? and user_password=?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setString(1, rePassword);
		prepareStatement.setString(2, username);
		prepareStatement.setString(3, password);

		int row = prepareStatement.executeUpdate();
		if (row == 0) {
			return false;
		} else if (row == 1) {
			return true;
		}
		connection.close();
		return false;
	}

	public List<Directory> getDirectories(Long uid, Long rid)
			throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		String sql = "select * from user_file_drictory where rdid=? and uid=?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setLong(1, rid);
		prepareStatement.setLong(2, uid);
		ResultSet result = prepareStatement.executeQuery();
		ArrayList<Directory> list = new ArrayList<>();
		while (result.next()) {
			list.add(new Directory(result.getLong("did"),
					result.getString("dname"), result.getLong("rdid"),
					result.getLong("uid")));
		}
		connection.close();
		return list;
	}

	public List<FileItem> getFileItems(Long uid, Long did) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		String sql = "select * from user_file_items where did=? and uid=?";
		PreparedStatement prepareStatement = connection.prepareStatement(sql);
		prepareStatement.setLong(1, did);
		prepareStatement.setLong(2, uid);
		ResultSet result = prepareStatement.executeQuery();
		ArrayList<FileItem> list = new ArrayList<>();
		while (result.next()) {
			FileItem item = new FileItem();
			item.setDid(result.getLong("did"));
			item.setFid(result.getLong("fid"));
			item.setFile_md5(result.getString("file_md5"));
			item.setFile_size(result.getLong("file_size"));
			item.setFname(result.getString("fname"));
			item.setUid(result.getLong("uid"));
			list.add(item);
		}
		return list;
	}

	public int mkDir(Directory dir) throws SQLException {

		Connection connection = C3P0Utils.getConnection();
		//由于did设置了主键自增，所以不用指定did
		String sql = "insert into user_file_drictory(dname,rdid,uid) values(?,?,?)";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setString(1, dir.getDname());
		prepare.setLong(2, dir.getRdid());
		prepare.setLong(3, dir.getUid());

		return prepare.executeUpdate();
	}

	/**
	 * 
	 * @param uid
	 * @param rid
	 * @param dname
	 * @return true 代表当前目录下没有同名文件,可以使用该文件名
	 * 			false 代表当前目录下有同名文件,不能使用该文件名
	 * @throws SQLException 
	 */
	public boolean isExistName(Directory dir) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//由于did设置了主键自增，所以不用指定did
		String sql = "select * from user_file_drictory where dname=? and rdid=? and uid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setString(1, dir.getDname());
		prepare.setLong(2, dir.getRdid());
		prepare.setLong(3, dir.getUid());
		ResultSet executeQuery = prepare.executeQuery();
		if (executeQuery.next()) {
			return false;
		} else {
			return true;
		}
	}

	public void deleteDir(Long uid, Long did) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//由于did设置了主键自增，所以不用指定did
		String sql = "delete from user_file_drictory where did=? and uid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setLong(1, did);
		prepare.setLong(2, uid);
		prepare.executeUpdate();
		connection.close();
	}

	public void deleteFileItem(long uid, long fid) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//由于did设置了主键自增，所以不用指定did
		String sql = "delete from user_file_items where fid=? and uid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setLong(1, fid);
		prepare.setLong(2, uid);
		prepare.executeUpdate();
		connection.close();
	}

	/**
	 * 判断当前文件夹的名称在当前父文件夹下是否重名
	 * @param did
	 * @return true 该文件名可以使用
	 *         false  该文件夹名重名
	 * @throws SQLException 
	 */
	public boolean dirNameIsExist(Long uid,Long did, String dname) throws SQLException {
		Connection connection = C3P0Utils.getConnection();

		//就要判断文件夹是否重名也要判断文件是否重名
		String sql = "select * from user_file_drictory where rdid=(select rdid from user_file_drictory where did=?) and dname=? and uid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setLong(1, did);
		prepare.setString(2, dname);
		prepare.setLong(3, uid);
		ResultSet result = prepare.executeQuery();
		if (result.next()) {
			return false;
		}
		prepare = connection.prepareStatement(
				"select * from user_file_items where fname=? and did=(select rdid from user_file_drictory where did=?) and uid=?");
		prepare.setString(1, dname);
		prepare.setLong(2, did);
		prepare.setLong(3, uid);
		ResultSet result2 = prepare.executeQuery();
		if (result2.next()) {
			return false;
		}
		return true;
	}
	/**
	 * 判断当前文件所在文件夹下有没指定名称的文件或文件夹
	 * @param fid
	 * @param fname
	 * @return
	 * @throws SQLException
	 */
	public boolean fileNameIsExist(Long uid,Long fid, String fname) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//先判断文件是否重名
		String sql = "select * from user_file_items where did=(select did from user_file_items where fid=?) and fname=? and uid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setLong(1, fid);
		prepare.setString(2, fname);
		prepare.setLong(3, uid);
		ResultSet result = prepare.executeQuery();
		if (result.next()) {
			return false;
		}
		prepare = connection.prepareStatement(
				"select * from user_file_drictory where dname=? and rdid=(select did from user_file_items where fid=?) and uid=?");
		prepare.setString(1, fname);
		prepare.setLong(2, fid);
		prepare.setLong(3, uid);
		ResultSet result2 = prepare.executeQuery();
		if (result2.next()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 修改文件夹名称
	 * @param did
	 * @param newName
	 * @throws SQLException
	 */
	public void reNameDir(Long did,String newName) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//先判断文件是否重名
		String sql = "update user_file_drictory set dname=? where did=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setString(1, newName);
		prepare.setLong(2, did);
		
		prepare.executeUpdate();
	}
	
	/**
	 * 修改文件名称
	 * @param fid
	 * @param newName
	 * @throws SQLException
	 */
	public void reNameFile(Long fid,String newName) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//先判断文件是否重名
		String sql = "update user_file_items set fname=? where fid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setString(1, newName);
		prepare.setLong(2, fid);
		prepare.executeUpdate();
	}
	public Long getInitSize(Long uid) throws SQLException {
		Connection connection = C3P0Utils.getConnection();
		//先判断文件是否重名
		String sql = "select user_size from user where uid=?";
		PreparedStatement prepare = connection.prepareStatement(sql);
		prepare.setLong(1, uid);
		ResultSet result = prepare.executeQuery();
		if(result.next()) {
			return result.getLong(1);
		}
		return null;
	}

	@Test
	public void test() throws SQLException {
//		reNameFile(2L,"haha.doc");
		//System.out.println(isExistName(new Directory(0L, "新建文件夹1", 0L, 1L)));
		//deleteDir(1L, 14L);
		//System.out.println(isDirExist(1L,1L,"哈哈"));
		//System.out.println(isFileExist(1L,1L,"哈哈"));
		
	}
	
	

}
