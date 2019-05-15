package com.myService;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class HelloServlet extends  HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		 resp.setContentType("text/html");  
	        PrintWriter out = resp.getWriter();  
	        Boolean flag = false;    
	        String userName = req.getParameter("un");  
	        String password = req.getParameter("pw");   
	        if(userName.equals("htp")&&password.equals("123"))
	        {
	        	flag = true;
	        }
	        
	        else flag = false;
	        System.out.println("userName:"+userName+" password:"+password);
	        out.print(flag);  
	        out.flush();  
	        out.close(); 
	}
}
