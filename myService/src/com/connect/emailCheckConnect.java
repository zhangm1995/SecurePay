package com.connect;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.model.EmailCheck;

public class emailCheckConnect {
	//根据用户ID查找还未验证的邮箱验证码
	public static EmailCheck selectEmailCheck(int userId) {
		EmailCheck emailCheck=new EmailCheck ();
		String sql = "SELECT * FROM emailCheck WHERE userId="+userId+" and state=0";
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				emailCheck.setinfo(rs, emailCheck);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return emailCheck;
	}
	
	//先删再插
	public static int addEmailCheck(int userId, String code,String type,int state) {
		int i=0;
	    try{
	    	//先删除，在插入
	    	String deleteSql="delete from emailCheck where userId="+userId;
	    	myConnect.executeUpdate(deleteSql);
	    	java.util.Date date = new java.util.Date(); // 获取一个Date对象
			Timestamp timeStamp = new Timestamp(date.getTime()); // 讲日期时间转换为数据库中的timestamp类
	    	String sql="insert into emailCheck(userId,code,type,state,createTime) values("
	    			+ userId+ ",'" + code +"','"+type+"',"+state+",'"+timeStamp+ "')";
	    	i = myConnect.executeUpdate(sql);
	    }catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i; 
	}
	
	//更改验证码状态
		public static int updateEmailCheck(int userId, int state) {
			int i=0;
		    try{
		    	String sql="update emailCheck set state="+state+" where userId="+userId;
		    	i = myConnect.executeUpdate(sql);
		    }catch (Exception e) {
				e.printStackTrace();
			}
			myConnect.close();
			return i; 
		}
	
}
