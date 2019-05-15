package com.model;

import java.sql.ResultSet;
import java.util.Date;

public class Order {
	int id;
	String payer;//付款人
	String payee;//收款人
	double amount;
	int state;
	String createTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPayer() {
		return payer;
	}
	public void setPayer(String payer) {
		this.payer = payer;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	public void setinfo(ResultSet rs,Order order){
		try{
			order.setId(rs.getInt("id"));
			order.setPayee(rs.getString("payeeName"));
			order.setPayer(rs.getString("payerName"));
			order.setAmount(rs.getDouble("amount"));
			order.setState(rs.getInt("state"));
			order.setCreateTime(rs.getString("createTime"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
