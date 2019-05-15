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
 * Servlet implementation class HelloServlet2 解除绑定银行卡逻辑
 */
@WebServlet("/RelieveServlet")
public class RelieveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RelieveServlet() {
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
	 *      response) 解除绑定银行卡
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token = req.getHeader("accessToken");
		User user = TokenAuthentication.checkToken(token);
		int userId = user.getId();
		int state = 0;
		JSONObject jsonObj = new JSONObject();
		if (userId != 0) {
			// token认证通过
			String cardId = req.getParameter("cardId");
			Bank bank = bankConnect.Selectbank(cardId, user.getTel());
			int bankId = 0;
			bankId = bank.getId();
			int isBinding = userConnect.SelectBinding(userId, bankId);
			if (isBinding == 0) {
				// 说明已经未与该用户绑定，不再执行解除绑定操作，返回3
				state = 3;
			}else if (bankId != 0) {
				// 银行卡卡号与电话验证成功
				int success = userConnect.RelieveCard(userId, bank.getId());
				if (success == 2) {
					// 解除绑定成功
					state = 1;
				}
			} else {
				state = 2;
			}

		}
		jsonObj.put("state", state);// 0失败，1成功,2电话与银行卡号不匹配，3未与该用户绑定

		System.out.println("解除绑定银行卡state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}
}
