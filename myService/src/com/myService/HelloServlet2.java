package com.myService;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.tokenConnect;
import com.connect.userConnect;
import com.model.User;
import com.security.Encrypt;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2 登录逻辑
 */
@WebServlet("/HelloServlet2")
public class HelloServlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet2() {
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
	 *      response) 登录验证，当账号密码验证通过后，生成token返回给客户端
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		Boolean flag = false;
		String accessToken = null;
		int userId = 0;
		JSONObject jsonObj = new JSONObject();
		String tel = req.getParameter("tel");
		String password = req.getParameter("pw");
		// 查找对应用户的salt
		User selectedUser = userConnect.SelectUser(tel);
//		String salt = selectedUser.getSalt();
//		String saltWord = Encrypt.getMD5(password + salt);
//		// 验证Password+salt后的md5值是否相同
//		User user = userConnect.UserLogin(tel, saltWord);
		//检查密码输入错误次数
		int state=TokenAuthentication.checkUserWrong(selectedUser, password);
		if (state==1) {
			userId=selectedUser.getId();
			// 账号密码验证成功,通过账号密码和当前日期生成token
			flag = true;
			System.out.println(System.currentTimeMillis());
			String info = tel + password + System.currentTimeMillis();
			accessToken = TokenAuthentication.generateToken(info);
			if (tokenConnect.updateToken(userId, accessToken) == 0) {
				flag = false;// token未保存成功
			} else {
				jsonObj.put("accessToken", accessToken);
				jsonObj.put("id", userId);
				jsonObj.put("tel", selectedUser.getTel());
				jsonObj.put("username", selectedUser.getName());
			}
		} else {
			flag = false;
		}
		jsonObj.put("flag", flag);
		jsonObj.put("state", state);
		System.out.println("登录tel:" + tel + "userName:" + selectedUser.getName() + " password:" + password + " flag:" + flag
				+ " accessToken:" + accessToken);
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}
