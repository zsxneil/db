package com.my.shardingjdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class ShardingjdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShardingjdbcApplication.class, args);

    }

}

