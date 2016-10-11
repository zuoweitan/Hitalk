package com.vivifram.second.hitalk.state;

import java.util.List;

/**
 * Created by zuowei on 16-8-18.
 */
public abstract class SingleResult<T> implements DoneCallback<T> {

    @Override
    public void done(List<T> list, Exception e) {
        if (list != null && list.size() > 0){
            done(list.get(0),null);
        }else {
            done((T)null,e);
        }
    }

    public abstract void done(T result,Exception e);
}
