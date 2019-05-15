package com.model;

import java.sql.ResultSet;

public class User {
	int id;
	String password;
	String name;
	String tel;
	String salt;
	String email;
	int isBinding;
	int wrongNum;
	String wrongTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public int getIsBinding() {
		return isBinding;
	}
	public void setIsBinding(int isBinding) {
		this.isBinding = isBinding;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setinfo(ResultSet rs,User user){
		try{
			user.setId(rs.getInt("id"));
			user.setName(rs.getString("name"));
			user.setTel(rs.getString("tel"));
			user.setPassword(rs.getString("password"));
			user.setSalt(rs.getString("salt"));
			user.setIsBinding(rs.getInt("isBinding"));
			user.setWrongNum(rs.getInt("wrongNum"));
			user.setWrongTime(rs.getString("wrongTime"));
			user.setEmail(rs.getString("email"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
