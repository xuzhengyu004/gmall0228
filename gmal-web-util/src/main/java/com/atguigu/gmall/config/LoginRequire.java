package com.atguigu.gmall.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequire {
    /*设置一个默认值，如果是ture是，需要登录，如果是false，不需要登录*/
    boolean autoRedirect() default true;
}