package com.myService;

import java.io.IOException;
import java.io.PrintWriter;

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
import com.model.User;
import com.security.Encrypt;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2 充值提现逻辑
 */
@WebServlet("/CardPayServlet")
public class CardPayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CardPayServlet() {
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
	 *      response) 充值提现
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token = req.getHeader("accessToken");
		User user = TokenAuthentication.checkToken(token);
		int userId = user.getId();// 用户ID
		int state = 0;
		String alert="密码错误，操作失败";
		JSONObject jsonObj = new JSONObject();
		if (userId != 0) {
			// token认证通过
			String cardId = req.getParameter("cardId");// 银行卡号
			String payMoney = req.getParameter("payMoney");// 转账金额
			String situation = req.getParameter("situation");//判断是转账还是充值
			String payWord = req.getParameter("payWord");// 验证密码是否正确
			if(situation.equals("recharge")){
				//充值，校验银行卡密码
				Bank bank = bankConnect.Selectbank(cardId, user.getTel());
				int bankId = bank.getId();
				if(bankId!=0){
					// 银行卡卡号与电话验证成功,并且未绑定
					String saltword = payWord + bank.getSalt();
					if (Encrypt.getMD5(saltword).equals(bank.getPassword())) {
						// 验证银行卡密码通过,执行充值
						int success = bankConnect.Recharge(userId, cardId, payMoney);
						if (success == 3) {
							// 充值成功
							state = 1;
							alert="充值成功";
						}
					}
				}
			}else if(situation.equals("withdraw")){
				//提现，校验账户密码
				Account account=accountConnect.SelectAccount(userId);
				int passState=TokenAuthentication.checkAccountWrong(account, payWord);
				if (passState==1) {
					// 验证密码通过,执行提现操作
					int success = bankConnect.Withdraw(userId, cardId, payMoney);
					if (success == 3) {
						// 提现成功
						state = 1;
						alert="提现成功";
					}
				}else{
					state=passState;
				}
			}
			
			
		}
		jsonObj.put("state", state);// 0失败，1成功
		jsonObj.put("info",alert);//提示信息

		System.out.println("state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}
}
