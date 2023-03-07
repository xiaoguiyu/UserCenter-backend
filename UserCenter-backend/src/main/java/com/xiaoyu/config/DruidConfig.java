package com.xiaoyu.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;


/**
 * @author 小鱼
 * @version 1.0
 * @date 2023/3/6 17:04
 * druid 数据源注入
 */

@Slf4j
@Configuration
public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource getDataSource() {
        log.info("druid 配置已启用!");
        return new DruidDataSource();
    }


}
