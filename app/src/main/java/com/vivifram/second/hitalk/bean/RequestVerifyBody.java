package com.vivifram.second.hitalk.bean;

/**
 * Created by zuowei on 16-7-31.
 */
public class RequestVerifyBody {
    String mobilePhoneNumber;
    int ttl;

    public static RequestVerifyBody build(String mobilePhoneNumber,int ttl){
        RequestVerifyBody requestVerifyBody = new RequestVerifyBody();
        requestVerifyBody.mobilePhoneNumber = mobilePhoneNumber;
        requestVerifyBody.ttl = ttl;
        return requestVerifyBody;
    }
}
