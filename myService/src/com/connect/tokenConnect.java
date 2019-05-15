package com.connect;

import java.sql.ResultSet;

import com.model.User;
import com.security.Encrypt;

public class tokenConnect {
	//验证token，根据token查询用户id
	public static User selectToken(String token) {
		User user=new User();
		String sql = "SELECT * FROM `user` WHERE id= (SELECT userId FROM accesstoken WHERE token='"+token+"')";
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				user.setinfo(rs, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return user;
	}
	
	//生成新的token后，存入数据库，与用户id关联
	public static int addToken(int userId, String token) {
		int i=0;
	    try{
	    	String sql="insert into accessToken(userId,token) values("
	    			+ userId+ ",'" + token + "')";
	    	i = myConnect.executeUpdate(sql);
	    }catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i; 
	}
	
	//生成新的token后，存入数据库，与用户id关联
		public static int updateToken(int userId, String token) {
			int i=0;
		    try{
		    	String sql="update accessToken set token='"+token+"' where userId="+userId;
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
	
	//生成新的token后，存入数据库，与用户id关联
		public static int deleteToken(String token) {
			int i=0;
		    try{
		    	String sql="delete from accesstoken where token='"+token+"'";
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
}
