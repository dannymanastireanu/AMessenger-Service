package com.alpha.messenger.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.alpha.messenger")
public class SpringJdbcConfig {
    @Value("${mysql.database}")
    private String database;
    @Value("${mysql.user}")
    private String user;
    @Value("${mysql.password}")
    private String password;

    @Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + this.database + "?autoReconnect=true&useSSL=false");
        dataSource.setUsername(this.user);
        dataSource.setPassword(this.password);

        return dataSource;
    }
}
