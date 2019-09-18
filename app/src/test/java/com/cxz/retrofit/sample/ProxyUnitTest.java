package com.cxz.retrofit.sample;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc
 */
public class ProxyUnitTest {

    interface WanApi {
        @GET("/article/list/{page}/json")
        Call<ResponseBody> getArticleList(@Path("page") int page);
    }

    @Test
    public void proxy() {

        WanApi wanApi = (WanApi) Proxy.newProxyInstance(WanApi.class.getClassLoader(), new Class[]{WanApi.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                // 获取方法的名称
                System.out.println("获取方法的名称===>>" + method.getName());
                // 获取方法的注解
                GET get = method.getAnnotation(GET.class);
                // 获取方法注解的值
                System.out.println("获取方法注解的值===>>" + get.value());
                // 获取方法的参数的注解
                Annotation[][] parameterAnnotation = method.getParameterAnnotations();
                for (Annotation[] annotations : parameterAnnotation) {
                    System.out.println("获取方法的参数的注解===>>" + Arrays.toString(annotations));
                }
                // 获取方法参数的值
                System.out.println("获取方法参数的值===>>" + Arrays.toString(args));
                return null;
            }
        });

        wanApi.getArticleList(0);
    }

}
