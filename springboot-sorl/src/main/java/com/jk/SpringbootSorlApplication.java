package com.jk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jk.dao")
public class SpringbootSorlApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSorlApplication.class, args);
    }

}
