package com.cxz.retrofitlib;

import com.cxz.retrofitlib.http.Field;
import com.cxz.retrofitlib.http.GET;
import com.cxz.retrofitlib.http.POST;
import com.cxz.retrofitlib.http.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.HttpUrl;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc 请求方法属性封装类
 */
public class ServiceMethod {

    // OkHttpClient 封装构建
    private final Call.Factory callFactory;
    // 接口请求地址
    private final HttpUrl baseUrl;
    // 方法的请求方式（"GET"  "POST"）
    private String httpMethod;
    // 方法注解的值
    private String relativeUrl;
    // 方法参数的数组（每个对象包含：参数注解值，参数值）
    private ParameterHandler[] parameterHandlers;
    // 是否有请求体（GET方式没有）
    private boolean hasBody;

    private ServiceMethod(Builder builder) {
        this.callFactory = builder.retrofit.callFactory();
        this.baseUrl = builder.retrofit.baseUrl();
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.parameterHandlers = builder.parameterHandlers;
        this.hasBody = builder.hasBody;
    }

    // 参数值
    okhttp3.Call toCall(Object... args) {
        RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl, relativeUrl, hasBody);
        ParameterHandler[] handlers = this.parameterHandlers;
        int argumentCount = args != null ? args.length : 0;

        // 方法真实的参数个数  是否等于  收集的参数个数
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("");
        }

        // 循环拼接每个参数的名 + 参数值
        for (int i = 0; i < argumentCount; i++) {
            handlers[i].apply(requestBuilder, args[i].toString());
        }

        // 创建请求
        return callFactory.newCall(requestBuilder.build());
    }

    static final class Builder {
        // OkHttpClient 封装构建
        final Retrofit retrofit;
        // 带注解的方法
        final Method method;
        // 方法的所有注解（方法中可能有多个注解）
        final Annotation[] methodAnnotations;
        // 方法参数的所有注解（一个方法有多个参数，一个参数有多个注解）
        final Annotation[][] parameterAnnotationsArray;
        // 方法的请求方式（"GET"  "POST"）
        private String httpMethod;
        // 方法注解的值
        private String relativeUrl;
        // 方法参数的数组（每个对象包含：参数注解值，参数值）
        private ParameterHandler[] parameterHandlers;
        // 是否有请求体（GET方式没有）
        private boolean hasBody;

        Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            // 获取方法的所有注解
            this.methodAnnotations = method.getAnnotations();
            // 获取方法参数的所有注解
            this.parameterAnnotationsArray = method.getParameterAnnotations();
        }

        ServiceMethod build() {
            // 遍历方法的每个注解
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            // 定义方法参数的数组长度
            int parameterCount = parameterAnnotationsArray.length;
            // 初始化方法参数的数组
            parameterHandlers = new ParameterHandler[parameterCount];

            // 遍历方法的参数
            for (int i = 0; i < parameterCount; i++) {
                // 获取每个参数的注解
                Annotation[] parameterAnnotations = parameterAnnotationsArray[i];
                // 如果参数没有任何注解
                if (parameterAnnotations == null) {
                    throw new NullPointerException("");
                }
                // 获取参数的注解值，参数值
                parameterHandlers[i] = parseParameter(i, parameterAnnotations);
            }

            return new ServiceMethod(this);
        }

        // 解析参数的所有注解
        private ParameterHandler parseParameter(int i, Annotation[] annotations) {

            ParameterHandler result = null;
            // 遍历参数的注解
            for (Annotation annotation : annotations) {
                // 注解可能是 Query Field
                ParameterHandler annotationAction = parseParameterAnnotation(annotation);

                if (annotationAction == null) {
                    continue;
                }

                result = annotationAction;
            }

            if (result == null) {
                throw new IllegalArgumentException("没有Retrofit注解的支持");
            }
            return result;
        }

        // 解析参数的注解
        private ParameterHandler parseParameterAnnotation(Annotation annotation) {
            if (annotation instanceof Query) {
                Query query = (Query) annotation;
                // 参数注解的值
                String name = query.value();
                return new ParameterHandler.Query(name);
            } else if (annotation instanceof Field) {
                Field field = (Field) annotation;
                // 参数注解的值
                String name = field.value();
                return new ParameterHandler.Field(name);
            }
            return null;
        }

        // 解析方法的注解，可能是GET 可能是POST
        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof GET) {
                // get 方法没有请求体 requestBody
                parseHttpMethodAndPath("GET", ((GET) annotation).value(), false);
            } else if (annotation instanceof POST) {
                parseHttpMethodAndPath("POST", ((POST) annotation).value(), true);
            }
        }

        private void parseHttpMethodAndPath(String httpMethod, String value, boolean hasBody) {
            // 方法请求的方式 （GET POST）
            this.httpMethod = httpMethod;
            // 方法注解的值
            this.relativeUrl = value;
            // 是否有请求体
            this.hasBody = hasBody;
        }

    }

}
