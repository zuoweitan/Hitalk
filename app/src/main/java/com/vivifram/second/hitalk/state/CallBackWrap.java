package com.vivifram.second.hitalk.state;

import com.zuowei.utils.bridge.handler.ToolKit;

import java.util.List;

/**
 * Created by zuowei on 16-9-8.
 */
public class CallBackWrap {
    public static <T> DoneCallback<T> wrapOnMain(final DoneCallback<T> doneCallback){
        return new DoneCallback<T>() {
            @Override
            public void done(final List<T> list, final Exception e) {
                ToolKit.runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        doneCallback.done(list,e);
                    }
                });
            }
        };
    }

    public static <T> SingleResult<T> wrapOnMain(final SingleResult<T> singleResult){
        return new SingleResult<T>() {
            @Override
            public void done(final T result, final Exception e) {
                ToolKit.runOnMainThreadSync(new Runnable() {
                    @Override
                    public void run() {
                        singleResult.done(result,e);
                    }
                });
            }
        };
    }
}
