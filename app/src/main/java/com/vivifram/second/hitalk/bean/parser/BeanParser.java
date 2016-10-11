package com.vivifram.second.hitalk.bean.parser;

import com.avos.avoscloud.AVObject;
import com.vivifram.second.hitalk.bean.BaseBean;

import bolts.Task;

/**
 * Created by zuowei on 16-8-24.
 */
public class BeanParser<T extends BaseBean> {
    public Task<AVObject> encoder(T b){
        return null;
    }


    public T decoder(AVObject avObject){
        return null;
    }
}
