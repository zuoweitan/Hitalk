package com.vivifram.second.hitalk.cache;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.google.gson.Gson;
import com.vivifram.second.hitalk.bean.BaseBean;
import com.vivifram.second.hitalk.broadcast.ConnectivityNotifier;
import com.zuowei.dao.greendao.Bean;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.TaskUtils;
import com.zuowei.utils.helper.BeanHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by zuowei on 16-8-25.
 */
public class BeanEventuallyQueue extends BaseEventuallyQueue{

    private ConnectivityNotifier notifier;
    private ConnectivityNotifier.ConnectivityListener listener = new ConnectivityNotifier.ConnectivityListener() {
        @Override
        public void networkConnectivityStatusChanged(boolean isConnected) {
            setConnected(isConnected);
        }
    };

    private Context context;

    private TaskQueue taskQueue = new TaskQueue();
    private TaskQueue operationSetTaskQueue = new TaskQueue();

    private TaskCompletionSource<Void> connectionTaskCompletionSource = new TaskCompletionSource<>();
    private HashMap<String, TaskCompletionSource<Bean>> pendingOperationTasks = new HashMap<>();
    private HashMap<String, TaskCompletionSource<BaseBean>> pendingEventuallyTasks = new HashMap<>();
    private final Object connectionLock = new Object();
    private final Object taskQueueSyncLock = new Object();

    private ArrayList<String> beanPinLocalIds = new ArrayList<>();

    public BeanEventuallyQueue(Context context){
        this.context = context;
        notifier = ConnectivityNotifier.getNotifier(context);
        notifier.addAndRequestOneShot(context,listener);
        resume();
    }

    @Override
    public void setConnected(boolean connected) {
        synchronized (connectionLock) {
            if (isConnected() != connected) {
                super.setConnected(connected);
                if (connected) {
                    connectionTaskCompletionSource.trySetResult(null);
                    connectionTaskCompletionSource = new TaskCompletionSource<>();
                    connectionTaskCompletionSource.trySetResult(null);
                } else {
                    connectionTaskCompletionSource = new TaskCompletionSource<>();
                }
            }
        }
    }

    private Task<Void> waitForConnectionAsync() {
        synchronized (connectionLock) {
            return connectionTaskCompletionSource.getTask();
        }
    }

    @Override
    public void onDestroy() {
        notifier.removeListener(listener);
    }

    @Override
    public void pause() {
        synchronized (connectionLock) {
            connectionTaskCompletionSource.trySetError(new PauseException());
            connectionTaskCompletionSource = new TaskCompletionSource<>();
            connectionTaskCompletionSource.trySetError(new PauseException());
        }

        synchronized (taskQueueSyncLock) {
            for (String key : pendingEventuallyTasks.keySet()) {
                pendingEventuallyTasks.get(key).trySetError(new PauseException());
            }
            pendingEventuallyTasks.clear();
        }

        try {
            TaskUtils.wait(whenAll(Arrays.asList(taskQueue,operationSetTaskQueue)));
        }catch (Exception e){

        }
    }

    @Override
    public void resume() {
        NLog.i(TagUtil.makeTag(getClass()),"resume");
        if (isConnected()) {
            connectionTaskCompletionSource.trySetResult(null);
            connectionTaskCompletionSource = new TaskCompletionSource<>();
            connectionTaskCompletionSource.trySetResult(null);
        } else {
            connectionTaskCompletionSource = new TaskCompletionSource<>();
        }

        populateQueueAsync();
    }

    @Override
    public Task<Bean> enqueueEventuallyAsync(final BaseBean bean) {
        final TaskCompletionSource<Bean> tcs = new TaskCompletionSource<>();

        taskQueue.enqueue(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> toAwait) throws Exception {
                return enqueueEventuallyAsync(bean, toAwait, tcs);
            }
        });

        return tcs.getTask();
    }

    private Task<Void> enqueueEventuallyAsync(final BaseBean bean, Task<Void> toAwait, final TaskCompletionSource<Bean> tcs) {
        return toAwait.continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> toAwait) throws Exception {

                return BeanHelper.saveBean(bean).continueWithTask(new Continuation<Bean, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Bean> task) throws Exception {
                        Bean bean = task.getResult();
                        Exception error = task.getError();
                        if (error != null) {
                            NLog.e(TagUtil.makeTag(BeanEventuallyQueue.class),error);
                            return Task.forError(error);
                        }

                        pendingOperationTasks.put(bean.getLocalId(),tcs);

                        populateQueueAsync();

                        return task.makeVoid();
                    }
                });
            }
        });
    }

    private Task<Void> populateQueueAsync() {
        NLog.i(TagUtil.makeTag(getClass()),"populateQueueAsync");
        return taskQueue.enqueue(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> toAwait) throws Exception {
                return populateQueueAsync(toAwait);
            }
        });
    }

    private Task<Void> populateQueueAsync(Task<Void> toAwait) {
        NLog.i(TagUtil.makeTag(getClass()),"populateQueueAsync with toAwait = "+toAwait);
        return toAwait.continueWithTask(new Continuation<Void, Task<List<Bean>>>() {
            @Override
            public Task<List<Bean>> then(Task<Void> task) throws Exception {
                // We don't want to enqueue any EventuallyPins that are already queued.
                Task<List<Bean>> allBeans = BeanHelper.findAllBeans(beanPinLocalIds);
                return allBeans;
            }
        }).continueWithTask(new Continuation<List<Bean>, Task<List<Bean>>>() {
            @Override
            public Task<List<Bean>> then(Task<List<Bean>> task) throws Exception {
                Exception e = task.getError();
                NLog.i(TagUtil.makeTag(getClass()),"findAllBeans error = "+e);
                if (e == null){
                    return task;
                }
                return Task.forError(e);
            }
        }).onSuccessTask(new Continuation<List<Bean>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<List<Bean>> task) throws Exception {
                List<Bean> beanses = task.getResult();
                NLog.i(TagUtil.makeTag(getClass()),"beanses = " + beanses);
                for (final Bean bean : beanses) {
                    // We don't need to wait for this.
                    NLog.i(TagUtil.makeTag(getClass()),"bean = "+bean);
                    if (bean != null) {
                        runEventuallyAsync(bean);
                    }
                }

                return task.makeVoid();
            }
        });
    }

    private Task<Void> runEventuallyAsync(final Bean bean) {
        final String localId = bean.getLocalId();
        if (beanPinLocalIds.contains(localId)){
            return Task.forResult(null);
        }
        NLog.i(TagUtil.makeTag(getClass()),"runEventuallyAsync enqueue operationSetTaskQueue");
        beanPinLocalIds.add(localId);

        operationSetTaskQueue.enqueue(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(final Task<Void> toAwait) throws Exception {
                return runEventuallyAsync(bean, toAwait).continueWithTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        NLog.i(TagUtil.makeTag(getClass()),"operationSetTaskQueue runEventuallyAsync result e = "+task.getError());
                        beanPinLocalIds.remove(localId);
                        return task;
                    }
                });
            }
        });

        return Task.forResult(null);
    }

    private Task<Void> runEventuallyAsync(final Bean bean, Task<Void> toAwait) {
        return toAwait.continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return waitForConnectionAsync();
            }
        }).onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return runInnerLocked(bean).continueWithTask(new Continuation<Bean, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Bean> task) throws Exception {
                        Exception e = task.getError();
                        if (e != null && e instanceof PauseException){
                            return task.makeVoid();
                        }
                        TaskCompletionSource<Bean> tcs =
                                pendingOperationTasks.remove(bean.getLocalId());
                        if (tcs != null) {
                            if (e != null) {
                                tcs.setError(e);
                            } else {
                                tcs.setResult(task.getResult());
                            }
                        }
                        return task.makeVoid();
                    }
                });
            }
        });
    }

    private Task<Bean> runInnerLocked(final Bean bean){
        return waitForConnectionAsync().onSuccessTask(new Continuation<Void, Task<Bean>>() {
            @Override
            public Task<Bean> then(Task<Void> task) throws Exception {

                try {
                    BaseBean baseBean = (BaseBean) new Gson().fromJson(bean.getContent(),Class.forName(bean.getClassName()));
                    /*Task<AVObject> avObjectTask = baseBean.getParser().encoder(baseBean);
                    avObjectTask.waitForCompletion();
                    AVObject avObject = avObjectTask.getResult();

                    if (avObject == null) {
                        throw new NullPointerException("baseBean parser failed");
                    }*/

                    return process(baseBean).continueWithTask(new Continuation<BaseBean, Task<Bean>>() {
                        @Override
                        public Task<Bean> then(Task<BaseBean> task) throws Exception {
                            BaseBean b = task.getResult();
                            Exception e = task.getError();
                            if (e != null) {
                                if (e instanceof PauseException){
                                    return Task.forError(e);
                                }

                                if (e instanceof  AVException &&
                                        ((AVException)e).getCode() == AVException.CONNECTION_FAILED){
                                    NLog.i(TagUtil.makeTag(getClass()),"process error because of CONNECTION_FAILED");
                                    return runInnerLocked(bean);
                                }

                                return Task.forError(e);
                            }

                            //try to delete
                            if (b != null) {
                                return BeanHelper.deleteBean(bean);
                            }
                            return Task.forResult(bean);
                        }
                    });
                }catch (Exception e){
                    return Task.forError(e);
                }
            }
        });
    }

    private Task<BaseBean> process(final BaseBean baseBean) {
        final TaskCompletionSource tc;
        if (pendingEventuallyTasks.containsKey(baseBean.getLocalId())){
            tc = pendingEventuallyTasks.get(baseBean.getLocalId());
        }else {
            tc = Task.create();
            pendingEventuallyTasks.put(baseBean.getLocalId(),tc);
        }

        baseBean.runCommand().continueWith(new Continuation<AVObject, Object>() {
            @Override
            public Object then(Task<AVObject> task) throws Exception {
                Exception e = task.getError();
                if (e != null) {
                    tc.setError(e);
                }else {
                    tc.setResult(baseBean);
                }
                return null;
            }
        });

        return tc.getTask();
    }

    public Task<Void> checkOperationSetTaskQueue(){
        return operationSetTaskQueue.enqueue(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                NLog.i(TagUtil.makeTag(getClass()),"checkOperationSetTaskQueue task = "+task);
                return task;
            }
        });
    }

    private volatile boolean isOnShot;
    public void requestOneShot(){
        if (!isOnShot){
            isOnShot = true;
            populateQueueAsync();
            checkOperationSetTaskQueue().continueWith(new Continuation<Void, Object>() {
                @Override
                public Object then(Task<Void> task) throws Exception {
                    isOnShot = false;
                    return null;
                }
            });
        }
    }

    private Task<Void> whenAll(Collection<TaskQueue> taskQueues) {
        List<Task<Void>> tasks = new ArrayList<>();

        for (TaskQueue taskQueue : taskQueues) {
            Task<Void> task = taskQueue.enqueue(new Continuation<Void, Task<Void>>() {
                @Override
                public Task<Void> then(Task<Void> toAwait) throws Exception {
                    return toAwait;
                }
            });

            tasks.add(task);
        }

        return Task.whenAll(tasks);
    }

    private static class PauseException extends Exception {
        // This class was intentionally left blank.
    }
}
