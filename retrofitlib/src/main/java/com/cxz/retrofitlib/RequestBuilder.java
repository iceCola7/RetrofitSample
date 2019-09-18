package com.cxz.retrofitlib;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc
 */
public class RequestBuilder {

    // 方法的请求方式（GET POST）
    private final String method;
    // 接口请求地址
    private final HttpUrl baseUrl;
    // 方法注解的值
    private String relativeUrl;
    // 请求URL构建者
    private HttpUrl.Builder urlBuilder;
    // Form 表单构建者
    private FormBody.Builder formBuilder;
    // 构建完整请求（包含URL method body）
    private final Request.Builder requestBuilder;

    public RequestBuilder(String method, HttpUrl baseUrl, String relativeUrl, boolean hasBody) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.relativeUrl = relativeUrl;

        // 初始化请求
        requestBuilder = new Request.Builder();
        // 根据是否有请求体实例化Form表单构建者
        if (hasBody) formBuilder = new FormBody.Builder();
    }

    // 拼接get请求
    public void addQueryParam(String name, String value) {
        if (relativeUrl != null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
            // 每次请求实例化 重置
            relativeUrl = null;
        }
        urlBuilder.addQueryParameter(name, value);
    }

    public void addFormField(String name, String value) {
        formBuilder.add(name, value);
    }

    public Request build() {

        // 定义局部变量。1.保证每次请求不一样；2.易回收。
        HttpUrl url;
        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl.resolve(relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException("Malformed URL. Base:" + baseUrl + ",Relative:" + relativeUrl);
            }
        }

        // 如果有请求体，构造方法中会初始化Form表单构建者，然后在实例化请求体
        RequestBody body = null;
        if (formBuilder != null) {
            body = formBuilder.build();
        }

        // 构建完整请求
        return requestBuilder
                .url(url)
                .method(method, body)
                .build();
    }

}
