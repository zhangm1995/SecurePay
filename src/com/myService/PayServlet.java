package com.myService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.accountConnect;
import com.connect.orderConnect;
import com.model.Account;
import com.model.User;
import com.security.Encrypt;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2 转账逻辑
 */
@WebServlet("/PayServlet")
public class PayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PayServlet() {
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
	 *      response) 转账
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token = req.getHeader("accessToken");
		User user = TokenAuthentication.checkToken(token);
		int userId = user.getId();// 付款人
		int state = 0;
		JSONObject jsonObj = new JSONObject();
		if (userId != 0) {
			// token认证通过
			String payWord = req.getParameter("payWord");// 验证密码是否正确
			Account account=accountConnect.SelectAccount(userId);
			int passwordState=TokenAuthentication.checkAccountWrong(account, payWord);
			
			if (passwordState==1) {
				// 验证密码通过,执行转账操作
				String payMoney = req.getParameter("payMoney");// 转账金额
				String payId = req.getParameter("payId");// 收款人
				int success = orderConnect.PayOrder(userId, payId, payMoney);
				if (success == 3) {
					// 转账成功
					state = 1;
				}
			}else{
				state=passwordState;
			}
		}
		jsonObj.put("state", state);// 0失败，1成功

		System.out.println("state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}
}
