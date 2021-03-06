package com.steam.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZhangShizhu
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("com.steam.datasource.dao")
public class DatasourceApplication {

    public static void main(String[] args) {
        SpringApplication. run(DatasourceApplication.class, args);
    }

}
