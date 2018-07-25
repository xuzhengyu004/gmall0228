package com.atguigu.gmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//表示这个类是一个配置文件
public class RedisConfig {

    //读取配置文件中的redis的ip地址
    // @Value是从spring boot项目中application.properties中读取的配置
    //如果配置文件中没有host，那么默认就是disabled
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    //相当于取得对象
    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        //给连接池初始化
        RedisUtil redisUtil=new RedisUtil();
        redisUtil.initJedisPool(host,port,database);
        return redisUtil;
    }

}
