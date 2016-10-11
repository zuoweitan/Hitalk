package com.zuowei.utils.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zuowei on 16-9-7.
 */
public class AvObjectSaveHelper {
    public static void runSaveSync(AVObject avObject) throws InterruptedException, AVException {
        final CountDownLatch lock = new CountDownLatch(1);
        final AVException[] exceptions = new AVException[1];
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                exceptions[0] = e;
                lock.countDown();
            }
        });
        lock.await();
        if (exceptions[0] != null){
            throw exceptions[0];
        }
    }

    public static void runSaveFileSync(AVFile avFile) throws InterruptedException, AVException {
        final CountDownLatch lock = new CountDownLatch(1);
        final AVException[] exceptions = new AVException[1];
        avFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                exceptions[0] = e;
                lock.countDown();
            }
        });
        lock.await();
        if (exceptions[0] != null){
            throw exceptions[0];
        }
    }
}
