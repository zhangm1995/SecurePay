package com.connect;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.model.Bank;
import com.model.Order;

public class bankConnect {

	public static Bank Selectbank(String cardId,String tel) {
		Bank bank = new Bank();
		String sql = "select *  from bank where cardId=" + cardId+" and tel='"+tel+"'";
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				bank.setinfo(rs, bank);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return bank;
	}
	
	public static List<Bank> SelectBindingCard(int userId) {
		List<Bank> bankList = new ArrayList<Bank>();
		String sql = "SELECT bank.* FROM `bank`,binding WHERE bank.id=binding.cardId AND binding.userId="+userId;
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				Bank bank=new Bank();
				bank.setinfo(rs, bank);
				bankList.add(bank);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return bankList;
	}

	// 充值，银行卡充值到账户
	public static int Recharge(int userId, String cardId, String amount) {
		int i = 0;
		int j = 0;
		int k = 0;
		try {
			// 银行卡金额减少
			String payerSql = "UPDATE bank SET balance=balance-" + amount + " WHERE cardId=" + cardId;
			i = myConnect.executeUpdate(payerSql);
			if (i == 1) {
				// 收款人金额增加
				String payeeSql = "UPDATE account SET balance=balance+" + amount + " WHERE userId=" + userId;
				j = myConnect.executeUpdate(payeeSql);
				if (j == 1) {
					// 创建交易记录
					java.util.Date date = new java.util.Date(); // 获取一个Date对象
					Timestamp timeStamp = new Timestamp(date.getTime()); // 讲日期时间转换为数据库中的timestamp类

					String addSql = "insert into `order` (payer,payee,amount,state,createtime) " + "values(0," + userId
							+ "," + amount + "," + 1 + ",'" + timeStamp + "')";
					k = myConnect.executeUpdate(addSql);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i + j + k;
	}

	// 提现，账户提现到银行卡
	public static int Withdraw(int userId, String cardId, String amount) {
		int i = 0;
		int j = 0;
		int k = 0;
		try {
			// 银行卡金额增加
			String payerSql = "UPDATE bank SET balance=balance+" + amount + " WHERE cardId=" + cardId;
			i = myConnect.executeUpdate(payerSql);
			if (i == 1) {
				// 付款人金额减少
				String payeeSql = "UPDATE account SET balance=balance-" + amount + " WHERE userId=" + userId;
				j = myConnect.executeUpdate(payeeSql);
				if (j == 1) {
					// 创建交易记录
					java.util.Date date = new java.util.Date(); // 获取一个Date对象
					Timestamp timeStamp = new Timestamp(date.getTime()); // 讲日期时间转换为数据库中的timestamp类

					String addSql = "insert into `order` (payer,payee,amount,state,createtime) " + "values(" + userId
							+ ",0," + amount + "," + 1 + ",'" + timeStamp + "')";
					k = myConnect.executeUpdate(addSql);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i + j + k;
	}

}
