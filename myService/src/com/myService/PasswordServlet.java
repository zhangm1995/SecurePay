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

import com.connect.accountConnect;
import com.connect.tokenConnect;
import com.connect.userConnect;
import com.model.Account;
import com.model.User;
import com.security.Encrypt;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2
 * 检查密码是否正确
 */
@WebServlet("/PasswordServlet")
public class PasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PasswordServlet() {
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
	 *     检查密码是否正确
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token=req.getHeader("accessToken");
		User user=TokenAuthentication.checkToken(token);
		int userId=user.getId();
		int state=0;
		JSONObject jsonObj = new JSONObject();
		if(userId!=0){
			//认证通过
			String password = req.getParameter("password");
			String type=req.getParameter("type");
			
			if(type.equals("login")){
				//检查密码输入错误次数
				state=TokenAuthentication.checkUserWrong(user, password);
//				String encryptPwd=Encrypt.getMD5(password+user.getSalt());
//				if(encryptPwd.equals(user.getPassword())){
//					state=1;
//				}
			}else if(type.equals("pay")){
				Account account=accountConnect.SelectAccount(userId);
				state=TokenAuthentication.checkAccountWrong(account, password);
			}
			
		}
		jsonObj.put("state", state);//0失败，1成功
		System.out.println("state:" + state );
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}
