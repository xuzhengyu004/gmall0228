package com.atguigu.gmall.order.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyOrderConfig implements AsyncConfigurer {
    // 执行器方法
    @Override
    public Executor getAsyncExecutor() {
        // 创建一个执行器对象
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置参数
        /**
         * 设置线程数
         * 设置最大连接数
         * 设置等待队列，如果10个不够，可以有100个线程等待
         */
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(100);
        threadPoolTaskExecutor.setQueueCapacity(100);
        // 初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
