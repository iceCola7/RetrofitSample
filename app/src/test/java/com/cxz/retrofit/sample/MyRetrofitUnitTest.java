package com.cxz.retrofit.sample;

import com.cxz.retrofitlib.Retrofit;
import com.cxz.retrofitlib.http.GET;
import com.cxz.retrofitlib.http.Query;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc
 */
public class MyRetrofitUnitTest {

    public final static String BASE_URL = "https://wanandroid.com";

    interface WanApi {
        @GET("/wxarticle/list/408/1/json")
        Call get(@Query("k") String k);
    }


    @Test
    public void testMyRetrofit() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        WanApi wanApi = retrofit.create(WanApi.class);

        // Retrofit GET 同步请求
        {
            Call call = wanApi.get("android");
            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("Retrofit GET 同步请求====>>" + response.body().string());
            }
        }

    }

}
