package com.vivifram.second.hitalk.state;

import java.util.List;

/**
 * Created by zuowei on 16-8-8.
 */
public interface DoneCallback<T> {
    void done(List<T> list,Exception e);
}
