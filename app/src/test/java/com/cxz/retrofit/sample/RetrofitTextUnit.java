package com.cxz.retrofit.sample;

import org.junit.Test;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author chenxz
 * @date 2019/9/18
 * @desc 单元测试
 */
public class RetrofitTextUnit {

    public final static String BASE_URL = "https://wanandroid.com";

    interface WanApi {
        @GET("/article/list/{page}/json")
        Call<ResponseBody> getArticleList(@Path("page") int page);
    }

    @Test
    public void testRetrofit() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        WanApi wanApi = retrofit.create(WanApi.class);

        // Retrofit GET 同步请求
        {
            Call<ResponseBody> call = wanApi.getArticleList(0);
            retrofit2.Response<ResponseBody> response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("Retrofit GET 同步请求====>>" + response.body().string());
            }
        }

    }

}
