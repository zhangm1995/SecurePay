package com.myService;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.accountConnect;
import com.connect.orderConnect;
import com.connect.tokenConnect;
import com.connect.userConnect;
import com.model.Account;
import com.model.Order;
import com.model.User;
import com.security.TokenAuthentication;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class
 * 查询账户信息和订单信息逻辑
 */
@WebServlet("/AccountServlet")
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AccountServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      查询该用户的账号信息
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		double balance;
		List<Order> orderList = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = new JSONObject();
		String token=req.getHeader("accessToken");
		User user=TokenAuthentication.checkToken(token);
		int userId=user.getId();
		String userName=user.getName();
		String tel=user.getTel();
		if(userId==0){
			//token未认证通过，返回false
			jsonObject.put("state", "false");
		}else{
			//token认证通过，继续操作
			balance=accountConnect.SelectBalance(userId);
			orderList=orderConnect.SelectOrder(userId);
			jsonArray= net.sf.json.JSONArray.fromObject(orderList);
			jsonObject.put("state", "true");
			jsonObject.put("userId", userId);
			jsonObject.put("userName", userName);
			jsonObject.put("tel", tel);
			jsonObject.put("balance", balance);
			jsonObject.put("order", jsonArray);
			jsonObject.put("email", user.getEmail());
			jsonObject.put("isBinding", user.getIsBinding());
		}
		System.out.println("详情"+jsonObject.toString());
		out.print(jsonObject);
		out.flush();
		out.close();
	}

}
