package com.zgurski;

import com.zgurski.configuration.WebMVC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = "com.zgurski")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebMvc
@Import(
        {
        WebMVC.class
//                ,
//                HibernateConfig.class
        }
)
@EnableCaching
@EnableTransactionManagement
public class TableplanApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(TableplanApplicationStarter.class, args);
    }
}