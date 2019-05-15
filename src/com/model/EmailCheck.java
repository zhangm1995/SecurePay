package com.model;

import java.sql.ResultSet;

public class EmailCheck {
	int id;
	int userId;
	String code;
	String type;
	int state;
	String createTime;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public void setinfo(ResultSet rs,EmailCheck emailCheck){
		try{
			emailCheck.setId(rs.getInt("id"));
			emailCheck.setUserId(rs.getInt("userId"));
			emailCheck.setCode(rs.getString("code"));
			emailCheck.setType(rs.getString("type"));
			emailCheck.setState(rs.getInt("state"));
			emailCheck.setCreateTime(rs.getString("createTime"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
