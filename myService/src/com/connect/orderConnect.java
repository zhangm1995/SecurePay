package com.connect;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.model.Account;
import com.model.Order;
import com.security.Encrypt;

public class orderConnect {

	public static List<Order> SelectOrder(int userId) {
		List<Order> orderList = new ArrayList<Order>();
		String sql = "SELECT * " + "FROM (SELECT o.*,u.`name` payeeName FROM `order` o,`user` u WHERE o.payee=u.id) a "
				+ " NATURAL JOIN " + "(SELECT o.*,u.`name` payerName FROM `order` o,`user` u WHERE o.payer=u.id) b"
				+ " WHERE a.id=b.id and (payee=" + userId + " or payer=" + userId + ") ORDER BY createtime DESC;";
		// String sql = "SELECT * FROM `order` where payee=" + userId + " or
		// payer=" + userId + ";";
		ResultSet rs = myConnect.executeQuery(sql);
		try {
			while (rs.next()) {
				Order order = new Order();
				order.setinfo(rs, order);
				orderList.add(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return orderList;
	}

	public static int PayOrder(int payer, String payee, String amount) {
		int i = 0;
		int j = 0;
		int k = 0;
		try {
			// 付款人金额减少
			String payerSql = "UPDATE account SET balance=balance-" + amount + " WHERE userId=" + payer;
			i = myConnect.executeUpdate(payerSql);
			if (i == 1) {
				// 收款人金额增加
				String payeeSql = "UPDATE account SET balance=balance+" + amount + " WHERE userId=" + payee;
				j = myConnect.executeUpdate(payeeSql);
				if (j == 1) {
					java.util.Date date = new java.util.Date(); // 获取一个Date对象
					Timestamp timeStamp = new Timestamp(date.getTime()); // 讲日期时间转换为数据库中的timestamp类

					String addSql = "insert into `order` (payer,payee,amount,state,createtime) " + "values(" + payer + ","
							+ payee + "," + amount+","+1 + ",'" + timeStamp + "')";
					k = myConnect.executeUpdate(addSql);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		myConnect.close();
		return i+j+k;
	}

}
