package com.mrguan.tcyhwapp.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 * Created with Intellij IDEA.
 *
 * @Author: Gabriel
 * @Date: 2018-10-10
 * @Desciption:
 */

@Configuration

public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druid(){
        return new DruidDataSource();
    }

    @Bean
    public ServletRegistrationBean druidServlet(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        bean.addInitParameter("allow","127.0.0.1");
        bean.addInitParameter("resetEnable","false");
        bean.addInitParameter("loginUsername","admin");
        bean.addInitParameter("loginPassword","admin");
        return bean;
    }

    @Bean
    public FilterRegistrationBean statFilter(){
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("exclusions","*.js,*.gif,/druid/*");
        return bean;
    }
}

