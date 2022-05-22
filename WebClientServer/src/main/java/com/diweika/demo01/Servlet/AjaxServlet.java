package com.diweika.demo01.Servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 这是响应success.html里的Ajax的servlet
 *
 */
@WebServlet(name = "AjaxServlet",urlPatterns = "/ajaxServlet")
public class AjaxServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //解决响应中文乱码
        response.setContentType("text/html;charset=UTF-8");

        //获取Session对象
        HttpSession session = request.getSession(false);

        //获取sessionId
        String sessionId = (String) session.getAttribute("sessionId");

        PrintWriter writer = response.getWriter();
        writer.write(sessionId);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
}
