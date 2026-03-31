package com.smartcoffee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.smartcoffee.mapper")
@EnableTransactionManagement
public class SmartCoffeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCoffeeApplication.class, args);
        System.out.println("SmartCoffee Backend Started Successfully!");
    }
}
