package com.myService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.connect.userConnect;
import com.model.User;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2
 * 查找用户
 */
@WebServlet("/SelectUserServlet")
public class SelectUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SelectUserServlet() {
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
	 *     查找用户
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String token=req.getHeader("accessToken");
		User user=TokenAuthentication.checkToken(token);
		User selectedUser;
		int userId=user.getId();
		int state=0;
		JSONObject jsonObj = new JSONObject();
		if(userId!=0){
			//认证通过
			String tel = req.getParameter("tel");
			selectedUser=userConnect.SelectUser(tel);
			if( selectedUser.getId()!=0){
				state=1;
				jsonObj.put("id", selectedUser.getId());
				jsonObj.put("name", selectedUser.getName());
				jsonObj.put("tel", selectedUser.getTel());
			}
		}
		jsonObj.put("state", state);//0失败，1成功
		
		System.out.println("查找账号state:" + state);
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}
