package com.zuowei.utils.common;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zuowei on 16-7-31.
 */
public class RxjavaUtils {

    public static void ObServerOnMainThread(Observable observable, Subscriber subscriber){
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public static void AsyncJob(final Runnable runnable){
        Observable observable = Observable.just("");
        observable.observeOn(Schedulers.io());
        observable.subscribe(new Action1() {
            @Override
            public void call(Object o) {
                runnable.run();
            }
        });
    }
}
