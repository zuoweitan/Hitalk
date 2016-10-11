package com.zuowei.utils.common;

import java.util.concurrent.CancellationException;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by zuowei on 16-8-29.
 */
public class TaskUtils {

    public static <T> T wait(Task<T> task) throws Exception{
        try {
            task.waitForCompletion();
            if (task.isFaulted()) {
                Exception error = task.getError();
                throw error;
            } else if (task.isCancelled()) {
                throw new RuntimeException(new CancellationException());
            }
            return task.getResult();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
