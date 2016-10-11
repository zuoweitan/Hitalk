package com.zuowei.utils.provider;

import com.vivifram.second.hitalk.state.DoneCallback;

import java.util.List;

/**
 * Created by zuowei on 16-8-8.
 */
public interface IProvider<k,T> {
    void fetch(List<k> ids, DoneCallback<T> doneCallback);
}
