package com.myService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import com.model.User;
import com.security.Email;
import com.security.Encrypt;
import com.security.RandomUtil;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2 发送邮件
 */
@WebServlet("/EmailServlet")
public class EmailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EmailServlet() {
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
	 *      response) 找回密码发送邮件
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String page = req.getParameter("page");// 入口页面类型，是否有token，0代表无token，1代表有token
		User user=new User();
		int emailSuccess=0;
		int state = 0;
		String code=RandomUtil.generateNum(4);
		JSONObject jsonObj = new JSONObject();
		if(page.equals("login")){
			//从登陆页面点击，无token，根据手机号和邮箱查找用户，修改的是登陆密码
			String tel= req.getParameter("tel");
			String email = req.getParameter("email");
			user=userConnect.SelectUser(tel);//通过手机号查找用户
			if(email.equals(user.getEmail())){
				//如果与预存邮箱地址相同，发送邮件
				Email sendEmail=new Email(user.getName(),user.getTel(),email,code,"login");
				emailSuccess=sendEmail.sendEmail();
				if(emailSuccess==1){
					//邮件发送成功，将code等信息存入数据库
					state=emailCheckConnect.addEmailCheck(user.getId(), code,"login", 0);
				}
			}else{
				state=2;//邮箱地址错误
			}
		}else if(page.equals("edit")){
			//修个人设置页面进，有token
			String token = req.getHeader("accessToken");
			user = TokenAuthentication.checkToken(token);
			int userId = user.getId();// 付款人
			if (userId != 0) {
				// token认证通过
				String type= req.getParameter("type");
				Email email=new Email(user.getName(),user.getTel(),user.getEmail(),code,type);
				emailSuccess=email.sendEmail();
				if(emailSuccess==1){
					//邮件发送成功，将code等信息存入数据库
					state=emailCheckConnect.addEmailCheck(user.getId(), code,type, 0);
				}
			}
		}
		
		jsonObj.put("state", state);// 0失败，1成功,2邮箱地址错误
		System.out.println("state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}
	
}
