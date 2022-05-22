package com.diweika.demo01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.diweika.demo01"})
@ComponentScan(basePackages = {"com.diweika.demo01.controller"})
@ServletComponentScan(basePackages = "com.diweika.demo01.Servlet")//在这里注册Servlet扫描
public class Demo01Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo01Application.class, args);
    }

}
