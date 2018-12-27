package com.my.atomikos.spring;

import com.my.atomikos.spring.service.JTAService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-atomikos.xml");
        JTAService jtaService = applicationContext.getBean("jtaService", JTAService.class);
        jtaService.insert();
    }
}
