package com.zuowei.utils.helper;


import com.vivifram.second.hitalk.bean.AvResult;
import com.vivifram.second.hitalk.bean.RequestVerifyBody;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zuowei on 16-7-31.
 */
public interface ILoginService  {
    @POST("/1.1/requestSmsCode")
    @Headers({
            "X-LC-Id: tul2H5WFbD4hMcI5I3aCJ3oW-gzGzoHsz",
            "X-LC-Key: OnaWi9FMWsNrSu3u8pTpjilx",
            "Content-Type: application/json"
    })
    Observable<AvResult> requestSms(@Body RequestVerifyBody body);

    @POST("/1.1/verifySmsCode/{code}")
    @Headers({
            "X-LC-Id: tul2H5WFbD4hMcI5I3aCJ3oW-gzGzoHsz",
            "X-LC-Key: OnaWi9FMWsNrSu3u8pTpjilx",
            "Content-Type: application/json"
    })
    Observable<AvResult> verifySms(@Path("code") String code,@Query("mobilePhoneNumber") String phoneNumber);
}
