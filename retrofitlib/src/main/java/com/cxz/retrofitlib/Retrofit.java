package com.cxz.retrofitlib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc
 */
public final class Retrofit {

    // 接口的请求地址
    private HttpUrl baseUrl;
    // OkHttpClient 唯一接口实现类
    private Call.Factory callFactory;

    // 缓存请求的方法
    private final Map<Method, ServiceMethod> serviceMethodMap = new ConcurrentHashMap<>();

    private Retrofit(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
    }

    // 对外提供 get 方法
    public HttpUrl baseUrl() {
        return baseUrl;
    }

    public Call.Factory callFactory() {
        return callFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 收集请求接口方法的信息
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return null;
            }
        });
    }

    // 获取方法所有的类容：方法名，方法注解，方法参数的注解，方法的参数等等
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodMap.get(method);
        if (result != null)
            return result;

        // 线程的安全同步锁
        synchronized (serviceMethodMap) {
            result = serviceMethodMap.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(this, method).build();
                serviceMethodMap.put(method, result);
            }
        }
        return result;
    }

    public static class Builder {
        // 接口的请求地址
        private HttpUrl baseUrl;
        // OkHttpClient 唯一接口实现类
        private Call.Factory callFactory;

        // 对外提供api
        public Builder baseUrl(String baseUrl) {
            if (baseUrl.isEmpty()) {
                throw new NullPointerException("baseUrl == null");
            }
            this.baseUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder baseUrl(HttpUrl baseUrl) {
            if (baseUrl == null) {
                throw new NullPointerException("baseUrl == null");
            }
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder callFactory(Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        // 构建者模式中，最终的build() create() into()
        // 属性校验，不为空的初始化赋值工作
        public Retrofit build() {
            if (this.baseUrl == null) {
                throw new IllegalStateException("baseUrl == null");
            }
            if (this.callFactory == null) {
                // 初始化赋值
                this.callFactory = new OkHttpClient();
            }
            return new Retrofit(this);
        }

    }

}
