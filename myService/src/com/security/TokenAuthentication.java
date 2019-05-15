package com.security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.connect.accountConnect;
import com.connect.tokenConnect;
import com.connect.userConnect;
import com.model.Account;
import com.model.User;

public class TokenAuthentication {

	// 使用md5加密算法，通过用户密码和登录时间生成token
	public static String generateToken(String str) {
		String token = "";
		token = Encrypt.getMD5(str);
		return token;
	}

	// 用户token认证，返回相应的用户id
	public static User checkToken(String token) {
		User user = tokenConnect.selectToken(token);
		return user;
	}

	public static int checkUserWrong(User selectedUser, String pwd) {
		//检查密码错误次数
		//0 连续输错三次，3小时内拒绝登录
		//1密码校验成功
		//2密码校验失败，重新输入
		//3账号不存在
		int state=0;
		
		if(selectedUser.getId()==0){
			//账号不存在
			state=3;
			return state;
		}else{
			//账号存在
			int userId=selectedUser.getId();//用户id
			String salt = selectedUser.getSalt();
			String saltWord = Encrypt.getMD5(pwd + salt);//加密后的密码
			String wrongTime=selectedUser.getWrongTime();//获取最新错误时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String now=df.format(new Date());// new Date()为获取当前系统时间
			if(wrongTime==null||compare_date(now,wrongTime)==1){//比较是否已经过了三个小时
				//已经过了3个小时，可以验证登录了
				
				// 验证Password+salt后的md5值是否相同
				User user = userConnect.CheckPassword(userId,saltWord);
				if(user.getId()!=0){
					//校验成功,重置错误状态
					userConnect.CleanWrongState(userId);
					state=1;
					return state;
				}else{
					//校验失败，错误状态++
					userConnect.SetWrongState(userId);
					state=2;
					return state;
				}
			}else{
				//还在3小时以内
				if(selectedUser.getWrongNum()>=3){
					//三小时以内错误次数大于等于3，则拒绝登录
					state=0;
					return state;
				}else{
					//三小时以内错误次数小于3，可再验证密码是否正确
					// 验证Password+salt后的md5值是否相同
					User user = userConnect.CheckPassword(userId,saltWord);
					if(user.getId()!=0){
						//校验成功,重置错误状态
						userConnect.CleanWrongState(userId);
						state=1;
						return state;
					}else{
						userConnect.SetWrongState(userId);
						state=2;
						return state;
					}
				}
			}
		}
	}
	
	public static int checkAccountWrong(Account selectedAccount, String pwd) {
		//检查密码错误次数
		//0 连续输错三次，3小时内拒绝登录
		//1密码校验成功
		//2密码校验失败，重新输入
		//3账号不存在
		int state=0;
		
		if(selectedAccount.getId()==0){
			//账号不存在
			state=3;
			return state;
		}else{
			//账号存在
			int accountId=selectedAccount.getId();//用户id
			String salt = selectedAccount.getPaySalt();
			String saltWord = Encrypt.getMD5(pwd + salt);//加密后的密码
			String wrongTime=selectedAccount.getWrongTime();//获取最新错误时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String now=df.format(new Date());// new Date()为获取当前系统时间
			if(wrongTime==null||compare_date(now,wrongTime)==1){//比较是否已经过了三个小时
				//已经过了3个小时，可以验证登录了
				
				// 验证Password+salt后的md5值是否相同
				if(selectedAccount.getPayPassword().equals(saltWord)){
					//校验成功,重置错误状态
					accountConnect.CleanWrongState(accountId);
					state=1;
					return state;
				}else{
					//校验失败，错误状态++
					accountConnect.SetWrongState(accountId);
					state=2;
					return state;
				}
			}else{
				//还在3小时以内
				if(selectedAccount.getWrongNum()>=3){
					//三小时以内错误次数大于等于3，则拒绝登录
					state=0;
					return state;
				}else{
					//三小时以内错误次数小于3，可再验证密码是否正确
					// 验证Password+salt后的md5值是否相同
					// 验证Password+salt后的md5值是否相同
					if(selectedAccount.getPayPassword().equals(saltWord)){
						//校验成功,重置错误状态
						accountConnect.CleanWrongState(accountId);
						state=1;
						return state;
					}else{
						//校验失败，错误状态++
						accountConnect.SetWrongState(accountId);
						state=2;
						return state;
					}
				}
			}
		}
	}
	
public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            dt2.setHours(dt2.getHours()+3);
            if (dt1.getTime() >= dt2.getTime()) {
                System.out.println("dt1 在（dt2+3小时）后");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在（dt2+3小时）前");
                return 0;
            } 
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

}
