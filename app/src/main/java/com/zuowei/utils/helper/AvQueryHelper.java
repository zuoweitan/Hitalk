package com.zuowei.utils.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zuowei on 16-9-7.
 */
public class AvQueryHelper {

    public static <T extends AVObject> List<T> runQuerySync(AVQuery<T> avQuery) throws InterruptedException, AVException {
        final CountDownLatch lock = new CountDownLatch(1);
        final List<T> result = new ArrayList<>();
        final AVException[] exceptions = new AVException[1];
        avQuery.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> list, AVException e) {
                if (list != null) {
                    result.addAll(list);
                }else {
                    exceptions[0] = e;
                }
                lock.countDown();
            }
        });
        lock.await();
        if (exceptions[0] != null){
            throw exceptions[0];
        }
        return result;
    }

    public static <T extends AVObject>void runQueryAsync(AVQuery<T> avQuery,FindCallback<T> findCallback){
        avQuery.findInBackground(findCallback);
    }
}
