package com.zuowei.utils.common;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-28 上午11:14
 * 修改人：zuowei
 * 修改时间：16-10-28 上午11:14
 * 修改备注：
 */
public class SyncUtils {

    private static final HashMap<Integer,CountDownLatch> sLatchCache = new HashMap<>();

    public static synchronized int getSyncLatch(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int key = countDownLatch.hashCode();
        sLatchCache.put(key,countDownLatch);
        return key;
    }

    public static void notify(int latch){
        CountDownLatch countDownLatch = sLatchCache.get(latch);
        if (countDownLatch != null) {
            countDownLatch.countDown();
            sLatchCache.remove(countDownLatch.hashCode());
        }
    }

    public static void wait(int latch){
        CountDownLatch countDownLatch = sLatchCache.get(latch);
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
