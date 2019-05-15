package com.model;

import java.sql.ResultSet;

public class Bank {
	int id;
	String cardId;
	double balance;
	String tel;
	String name;
	String password;
	String salt;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public void setinfo(ResultSet rs,Bank bank){
		try{
			bank.setId(rs.getInt("id"));
			bank.setCardId(rs.getString("cardId"));
			bank.setBalance(rs.getDouble("balance"));
			bank.setName(rs.getString("name"));
			bank.setTel(rs.getString("tel"));
			bank.setPassword(rs.getString("password"));
			bank.setSalt(rs.getString("salt"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
