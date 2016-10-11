package com.vivifram.second.hitalk.state;

/**
 * Created by zuowei on 16-8-4.
 */
public interface ActionCallback {
    void onError(int code,String message);
    void onSuccess();
}
