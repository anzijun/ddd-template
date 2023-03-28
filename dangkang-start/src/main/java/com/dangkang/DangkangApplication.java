package com.dangkang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;

/**
 * @date 2022/12/30 11:32
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@SpringBootApplication
public class DangkangApplication {

    public static void main(String[] args) {

        System.setProperty("profile","dev");
        ApplicationContext context = SpringApplication.run(DangkangApplication.class, args);

    }
}
