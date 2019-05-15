package com.model;

import java.sql.ResultSet;

public class Account {
	int id;
	int userId;
	Double balance;
	String payPassword;
	String paySalt;
	int wrongNum;
	String wrongTime;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getPaySalt() {
		return paySalt;
	}

	public void setPaySalt(String paySalt) {
		this.paySalt = paySalt;
	}

	public int getWrongNum() {
		return wrongNum;
	}

	public void setWrongNum(int wrongNum) {
		this.wrongNum = wrongNum;
	}

	public String getWrongTime() {
		return wrongTime;
	}

	public void setWrongTime(String wrongTime) {
		this.wrongTime = wrongTime;
	}

	public void setinfo(ResultSet rs,Account account){
		try{
			account.setId(rs.getInt("id"));
			account.setUserId(rs.getInt("userId"));
			account.setBalance(rs.getDouble("balance"));
			account.setPayPassword(rs.getString("password"));
			account.setPaySalt(rs.getString("salt"));
			account.setWrongNum(rs.getInt("wrongNum"));
			account.setWrongTime(rs.getString("wrongTime"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
