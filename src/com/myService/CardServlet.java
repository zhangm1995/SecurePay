package com.myService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.accountConnect;
import com.connect.bankConnect;
import com.connect.orderConnect;
import com.connect.userConnect;
import com.model.Account;
import com.model.Bank;
import com.model.Order;
import com.model.User;
import com.security.Encrypt;
import com.security.TokenAuthentication;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2 查询银行卡逻辑
 */
@WebServlet("/CardServlet")
public class CardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CardServlet() {
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
	 *      response) 查询银行卡
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token = req.getHeader("accessToken");
		User user = TokenAuthentication.checkToken(token);
		int userId = user.getId();// 收款人
		int state = 0;
		JSONObject jsonObj = new JSONObject();
		if (userId != 0) {
			// token认证通过
			List<Bank> cardList = null;
			JSONArray jsonArray = null;
			
			cardList = bankConnect.SelectBindingCard(userId);
			jsonArray = net.sf.json.JSONArray.fromObject(cardList);
			state = 1;
			jsonObj.put("cards", jsonArray);

		}
		jsonObj.put("state", state);// 0失败，1成功

		System.out.println("查询银行卡state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}
}
