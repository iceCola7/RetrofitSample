package com.cxz.retrofitlib.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc
 */
@Target(ElementType.PARAMETER) // 注解作用在属性之上
@Retention(RetentionPolicy.RUNTIME) // jvm 运行时通过反射技术获取注解的值
public @interface Field {
    String value();
}
