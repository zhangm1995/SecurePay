package com.connect;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.model.User;
import com.mysql.jdbc.Statement;
import com.security.Encrypt;

public class userConnect {
	
		public static User UserLogin(String tel, String password) {
			User user = new User();
			String sql = "select *  from `user` where tel='" + tel
					+ "' and password='" + password + "'";
			ResultSet rs = myConnect.executeQuery(sql);
			try {
				while (rs.next()) {
					user.setinfo(rs,user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return user;
		}
		
		public static User CheckPassword(int userId, String password) {
			User user = new User();
			String sql = "select *  from `user` where id=" + userId
					+ " and password='" + password + "'";
			ResultSet rs = myConnect.executeQuery(sql);
			try {
				while (rs.next()) {
					user.setinfo(rs,user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return user;
		}
		public static User SelectUser(String tel) {
			User user = new User();
			String sql = "select *  from `user` where tel=" + tel;
			ResultSet rs = myConnect.executeQuery(sql);
			try {
				while (rs.next()) {
					user.setinfo(rs,user);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return user;
		}
		
		public static int AddUser(String tel,String name,String email, String password,String salt) {
			int id=0;
		    try{
		    	String sql="insert into `user`(tel,name,email,password,salt,wrongNum,isBinding) values('"+tel+"','"
		    			+ name+"','"+email+ "','" + password+"','"+salt + "',0,0);";
		    	id = myConnect.executeUpdateId(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return id; 
		}
		
		public static int UpdateUser(String email,String name, int userId) {
			int i=0;
		    try{
		    	String sql="update `user` set email='"+email+"',name='"+name+"' where id="+userId;
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
		
		public static int UpdatePassword(int userId,String password,String salt) {
			int i=0;
		    try{
		    	String sql="update `user` set password='"+password+"',salt='"+salt+"' where id="+userId;
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
		
		public static int SetWrongState(int userId) {
			//设置错误状态
			int i=0;
		    try{
		    	java.util.Date date = new java.util.Date(); // 获取一个Date对象
				Timestamp timeStamp = new Timestamp(date.getTime()); // 讲日期时间转换为数据库中的timestamp类
		    	String sql="update `user` set wrongNum=wrongNum+1,wrongTime='"+timeStamp+"' where id="+userId;
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
		
		public static int CleanWrongState(int userId) {
			//设置错误状态
			int i=0;
		    try{
		    	String sql="update `user` set wrongNum=0,wrongTime="+null+" where id="+userId;
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
		
		
		public static int SelectBinding(int userId,int cardId) {
			//查询是否已经与该用户绑定，返回0说明还未绑定，可继续绑定
			String sql = "select id from binding where userId=" + userId+" and cardId="+cardId;
			ResultSet rs = myConnect.executeQuery(sql);
			int id=0;
			try {
				while (rs.next()) {
					id = rs.getInt("id");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return id;
		}
		
		public static int BindingCard(int userId,int cardId) {
			int i=0;
			int j=0;
		    try{
		    	String insertSql="insert into binding(userId,cardId) values("+userId+","+cardId+")";
		    	i = myConnect.executeUpdate(insertSql);
		    	if(i==1){
		    		String updateSql="update `user` set isBinding=isBinding+1 where id="+userId;
		    		j=myConnect.executeUpdate(updateSql);
		    	}
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i+j; 
		}
		
		public static int RelieveCard(int userId,int cardId) {
			int i=0;
			int j=0;
		    try{
		    	String deleteSql="delete from binding where userId="+userId+" and cardId="+cardId;
		    	i = myConnect.executeUpdate(deleteSql);
		    	if(i==1){
		    		//增加银行卡
		    		String updateSql="update `user` set isBinding=isBinding-1 where id="+userId;
		    		j=myConnect.executeUpdate(updateSql);
		    	}
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i+j; 
		}
		
}
