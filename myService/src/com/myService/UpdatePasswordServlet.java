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
import com.model.User;
import com.security.Encrypt;
import com.security.RandomUtil;
import com.security.TokenAuthentication;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class HelloServlet2
 * 修改密码
 */
@WebServlet("/UpdatePasswordServlet")
public class UpdatePasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdatePasswordServlet() {
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
		String page = req.getParameter("page");// 入口页面类型，是否有token，0代表无token，1代表有token
		User user=new User();
		int state = 0;
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
		
		int userId=user.getId();
		if(userId!=0){
			//认证通过
			String type=req.getParameter("type");
			String newPassword = req.getParameter("password");
			String salt=RandomUtil.generateString(4);
			//将password+salt字符串拼接，再次md5
			String saltWord=Encrypt.getMD5(newPassword+salt);
			//更新Password和salt字段
			if(type.equals("login")){
				state=userConnect.UpdatePassword(userId,saltWord,salt);
			}else if(type.equals("pay")){
				state=accountConnect.UpdatePassword(userId, saltWord, salt);
			}
			
		}
		jsonObj.put("state", state);//0失败，1成功
		System.out.println("state:" + state );
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}
