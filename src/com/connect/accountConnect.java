package com.connect;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.model.Account;

public class accountConnect {

	public static double SelectBalance(int userId) {
		double balance = 0;
		String sql = "select *  from account where userId=" + userId;
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				balance = rs.getDouble("balance");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return balance;
	}
	
	public static Account SelectAccount(int userId) {
		Account account=new Account();
		String sql = "select *  from account where userId=" + userId;
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				account.setinfo(rs,account);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return account;
	}

	// 在用户注册时生成账户
	public static int addAccount(int userId, Double balance, String password, String salt) {
		int i = 0;
		try {
			String sql = "insert into account(userId,balance,password,salt,wrongNum) values(" + userId + "," + balance + ",'"
					+ password + "','" + salt + "',0)";
			i = myConnect.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i;
	}
	
	public static int UpdatePassword(int userId,String password,String salt) {
		int i=0;
	    try{
	    	String sql="update account set password='"+password+"',salt='"+salt+"' where userId="+userId;
	    	i = myConnect.executeUpdate(sql);
	    }catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i; 
	}
	public static int SetWrongState(int accountId) {
		//设置错误状态
		int i=0;
	    try{
	    	java.util.Date date = new java.util.Date(); // 获取一个Date对象
			Timestamp timeStamp = new Timestamp(date.getTime()); // 讲日期时间转换为数据库中的timestamp类
	    	String sql="update account set wrongNum=wrongNum+1,wrongTime='"+timeStamp+"' where id="+accountId;
	    	i = myConnect.executeUpdate(sql);
	    }catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i; 
	}
	
	public static int CleanWrongState(int accountId) {
		//设置错误状态
		int i=0;
	    try{
	    	String sql="update account set wrongNum=0,wrongTime="+null+" where id="+accountId;
	    	i = myConnect.executeUpdate(sql);
	    }catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i; 
	}
}
