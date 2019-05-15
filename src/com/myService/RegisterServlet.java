package com.myService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.accountConnect;
import com.connect.tokenConnect;
import com.connect.userConnect;
import com.model.User;
import com.security.Encrypt;
import com.security.RandomUtil;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterServlet() {
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
	 *      response) 注册验证 用户密码md5后传输过来password 生成4位随机salt
	 *      再将passwor+salt字符串拼接，再次md5，存入Password字段
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		JSONObject jsonObj = new JSONObject();
		int state = 0;// 0注册失败，1注册成功，2该账号已存在
		int insertId = 0;
		String tel = req.getParameter("tel");
		String userName = req.getParameter("name");
		String email=req.getParameter("email");
		String loginPassword = req.getParameter("loginPassword");// 登录密码
		String payPassword = req.getParameter("payPassword");// 支付密码

		User selectedUser = userConnect.SelectUser(tel);
		if (selectedUser.getId() != 0) {
			// 如果该账号已存在,返回
			state = 2;
			System.out.println("账号存在");
		} else {
			// 生成4位随机salt
			String loginSalt = RandomUtil.generateString(4);
			String paySalt = RandomUtil.generateString(4);

			// 将password+salt字符串拼接，再次md5，存入Password字段
			String encryptLoginWord = Encrypt.getMD5(loginPassword + loginSalt);
			String encryptPayWord = Encrypt.getMD5(payPassword + paySalt);

			// 获取新增ID
			insertId = userConnect.AddUser(tel, userName,email, encryptLoginWord, loginSalt);

			if (insertId != 0) {
				// 添加成功
				// 添加账户
				int accountState = accountConnect.addAccount(insertId, 0.0, encryptPayWord, paySalt);
				String info = tel + loginPassword + System.currentTimeMillis();
				String accessToken = TokenAuthentication.generateToken(info);
				if (tokenConnect.addToken(insertId, accessToken) == 0 || accountState == 0) {
					state = 0;// token未保存成功
				} else {
					state = 1;
					jsonObj.put("id", insertId);
					jsonObj.put("accessToken", accessToken);
				}
			}
		}
		System.out.println("登录state" + state);
		jsonObj.put("state", state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}
