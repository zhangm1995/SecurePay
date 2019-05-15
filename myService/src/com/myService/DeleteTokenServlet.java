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
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2
 * 退出登录
 */
@WebServlet("/DeleteTokenServlet")
public class DeleteTokenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteTokenServlet() {
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
	 *     修改个人信息
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token=req.getHeader("accessToken");
		User user=TokenAuthentication.checkToken(token);
		int userId=user.getId();
		JSONObject jsonObj = new JSONObject();
		int state=0;
		if(userId!=0){
			//认证通过
			state=tokenConnect.deleteToken(token);
		}else{
			state=0;
		}
		jsonObj.put("state", state);//0失败，1成功
		System.out.println("state:" + state );
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}
