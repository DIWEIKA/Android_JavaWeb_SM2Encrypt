package com.diweika.demo01.Servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这是使用GET请求发送数据到服务器端的Servlet
 * 接收AndroidMobileClient发送过来的密码处理后的数据，存放在TransmittedData里
 */
	@WebServlet(name = "ServletForGETMethod", urlPatterns = "/ServletForGETMethod")
public class ServletForGETMethod extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

			String TransmittedData= request.getParameter("TransmittedData");//获取Android传送过来的数据
			String[] array = TransmittedData.split(" "); //将TransmittedData解析成字符数组
			String[] resultArray = array[0].split(":"); //解析出处理结果resultArray
			String[] idArray = array[1].split(":"); //解析出sessionId的数组idArray
			String handleResult = resultArray[1]; //得到处理结果
			String sessionId = idArray[1]; //得到sessionId
			System.out.println("处理结果是："+handleResult);
			System.out.println("sessionId是："+sessionId);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
