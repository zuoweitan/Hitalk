package com.vivifram.second.hitalk.manager;

import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zuowei on 16-7-18.
 */
public class RetrofitManager {
    private static RetrofitManager retrofitManager;

    private RetrofitManager() {
    }

    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (retrofitManager == null) {
                    retrofitManager = new RetrofitManager();
                }
            }
        }
        return retrofitManager;
    }

    public <T> T create(final Class<T> service,String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(
                baseUrl).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(service);
    }


}