package com.diweika.demo01.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//RequestMapping里的地址表示浏览器访问时输入的地址，即想访问Login()方法时，url为"localhost:8080/login"
//在这里注册login.html，即当你想要在浏览器里通过输入/*来访问时，要先return"*"
@Controller
public class LoginController {

   // TODO:

    @RequestMapping("/login")
    public String Login(){
        return "login";
    }

    @RequestMapping("/testSession")
    public String testSession(){
        return "testSession";
    }

    @RequestMapping("/testAjax")
    public String success(){
        return "testAjax";
    }

    @RequestMapping("/ajax")
    public String LoginForm(){
        return "ajax";
    }

    //在这里注册loginForm，即当提交到/loginForm时先执行这里
    @RequestMapping("/loginForm")
    public String doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //获取用户提交参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //获取Session对象
        HttpSession session = request.getSession(true);//true表示有用老的，没有就建新的

        //获取SessionId
        String sessionId = session.getId();

        //向Session域中写入属性
        session.setAttribute("username",username);
        session.setAttribute("sessionId",sessionId);

        //在控制台上显示Session
        System.out.println("username = "+username);
        System.out.println("sessionId = "+sessionId);

        //验证用户名
        if(username.equals("ALICE123@YAHOO.COM")&&password.equals("123456")){
            return  "success"; //在这里关联success.html文件
        }
        return "fail"; //在这里关联fail.html文件

    }

}


