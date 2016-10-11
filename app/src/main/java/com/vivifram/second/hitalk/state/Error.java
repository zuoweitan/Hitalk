package com.vivifram.second.hitalk.state;

/**
 * Created by zuowei on 16-7-27.
 */
public interface Error<T> {
    void setError(T t);
    T getError();
    int getCode();
    String getMessage();
}
