package com.myService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.accountConnect;
import com.connect.emailCheckConnect;
import com.connect.orderConnect;
import com.connect.userConnect;
import com.model.Account;
import com.model.EmailCheck;
import com.model.User;
import com.security.Email;
import com.security.Encrypt;
import com.security.RandomUtil;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2检验验证码
 */
@WebServlet("/EmailCodeServlet")
public class EmailCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EmailCodeServlet() {
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
	 *      response) 检验验证码
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String page = req.getParameter("page");// 入口页面类型，是否有token，0代表无token，1代表有token
		String code= req.getParameter("code");
		User user=new User();
		int emailSuccess=0;
		int state = 0;
		int userId;
		JSONObject jsonObj = new JSONObject();
		if(page.equals("login")){
			//从登陆页面点击，无token，根据手机号和邮箱查找用户，再通过userid查找对应的code，验证
			String tel= req.getParameter("tel");
			user=userConnect.SelectUser(tel);//通过手机号查找用户
		}else if(page.equals("edit")){
			//修个人设置页面进，有token，通过token查找用户
			String token = req.getHeader("accessToken");
			user = TokenAuthentication.checkToken(token);
		}
		userId=user.getId();
		EmailCheck emailCheck=emailCheckConnect.selectEmailCheck(userId);
		if(emailCheck.getCode().equals(code)){
			//验证成功，修改状态为已验证
			emailSuccess=emailCheckConnect.updateEmailCheck(userId, 1);
			state=1;
		}
		
		jsonObj.put("state", state);// 0失败，1成功
		System.out.println("state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}
}
