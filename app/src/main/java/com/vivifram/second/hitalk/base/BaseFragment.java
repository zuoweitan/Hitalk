package com.vivifram.second.hitalk.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.broadcast.ConnectivityNotifier;
import com.vivifram.second.hitalk.ui.page.layout.BaseFragmentLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.ParamWrap;
import com.zuowei.utils.bridge.params.chat.ClientOpenParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.ClientOpenHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuowei on 16-7-25.
 */
public abstract class BaseFragment<T extends BaseFragmentLayout> extends Fragment {
    private static final String TAG = TagUtil.makeTag(BaseFragment.class);
    private static final String PREFIX = "com.vivifram.second.hitalk.ui.page.layout.";
    protected WorkHandler workHandler;
    protected Context mAppCtx;
    protected T mLayout;
    protected boolean isInternetConnected;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAppCtx = HiTalkApplication.mAppContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_CHECK_CLIENT, clientOpenHandler);
        ConnectivityNotifier.getNotifier(mAppCtx).addListener(this::onInternetConnected);
        workHandler = new WorkHandler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);
        mLayout = generateLayout(view);
        if (mLayout != null) {
            mLayout.setActivity(getActivity());
            mLayout.onActivitySet();
            bindAllInterfaces();
            LayoutIdBinder.FLAYOUT.bindViewToFLayout(mLayout);
            mLayout.onViewCreate(view);
        }

        checkAndInstallEatMark();
        onViewCreated();
        return view;
    }

    private List<IEater> eater = new ArrayList<>();
    private void checkAndInstallEatMark() {
        Class<?>[] declaredClasses = getClass().getDeclaredClasses();
        if (declaredClasses != null) {
            for (Class<?> declaredClass : declaredClasses) {
                EatMark eatMark = declaredClass.getAnnotation(EatMark.class);
                if (eatMark != null) {
                    IEater iEater = null;
                    try {
                        Constructor<?> constructor = declaredClass.getDeclaredConstructor(this.getClass());
                        constructor.setAccessible(true);
                        iEater = (IEater) constructor.newInstance(this);
                    } catch (java.lang.InstantiationException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } catch (IllegalAccessException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } catch (NoSuchMethodException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } catch (InvocationTargetException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    }
                    if (iEater != null) {
                        eater.add(iEater);
                        EaterManager.getInstance().registerEater(eatMark.action(),iEater);
                    }
                }
            }
        }
    }

    private void bindAllInterfaces() {
        Field[] declaredFields = getClass().getDeclaredFields();
        if (declaredFields != null) {
            for (Field injectField : declaredFields) {
                InterfaceInject annotation = injectField.getAnnotation(InterfaceInject.class);
                if (annotation == null) {
                    continue;
                }
                String name = annotation.bindName();
                try {
                    Field field = mLayout.getClass().getDeclaredField(name);
                    field.setAccessible(true);
                    injectField.setAccessible(true);
                    field.set(mLayout,injectField.get(this));
                } catch (NoSuchFieldException e) {
                    NLog.e(TAG,"bindAllInterfaces failed : ",e);
                } catch (IllegalAccessException e) {
                    NLog.e(TAG,"bindAllInterfaces failed : ",e);
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLayout != null) {
            mLayout.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLayout != null) {
            mLayout.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLayout != null) {
            mLayout.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLayout != null){
            mLayout.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLayout != null) {
            mLayout.onViewDestroy();
        }
        onViewDestroyed();
        unInstallEatMark();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLayout != null) {
            mLayout.onDestroy();
        }
        if (workHandler != null) {
            workHandler.removeCallbacksAndMessages(null);
            workHandler = null;
        }

        EaterManager.getInstance().unRegisterEater(clientOpenHandler);
    }

    public void onClientOpen() {

    }

    public void onInternetConnected(boolean connected){
        isInternetConnected = connected;
    }

    private void unInstallEatMark() {
        for (IEater iEater : eater) {
            EaterManager.getInstance().unRegisterEater(iEater);
        }
    }


    protected void onViewCreated(){}

    protected void onViewDestroyed(){}

    protected abstract int getContentLayout();

    public T getLayout() {
        return mLayout;
    }

    private T generateLayout(View root) {
        LayoutInject layoutInject = getClass().getAnnotation(LayoutInject.class);
        if (layoutInject != null){
            String layoutName = layoutInject.name();
            try {
                Class layoutC = Class.forName(PREFIX + layoutName);
                Constructor constructor = layoutC.getConstructor(View.class);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(root);
            }catch (InvocationTargetException ex){
                NLog.e(TAG,"generateLayout failed : ",ex.getTargetException());
            } catch (Exception e) {
                NLog.e(TAG,"generateLayout failed : ",e);
            }
        }
        return null;
    }

    protected class WorkHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isAdded()){
                safeHandler(msg);
            }
        }

        public void safeHandler(Message msg){

        }
    }

    private ClientOpenHandler clientOpenHandler = new ClientOpenHandler() {
        @Override
        public void doJobWithParam(ParamWrap<ClientOpenParam> paramWrap) {
            if (paramWrap.getParam().mOpened){
                onClientOpen();
            }
        }
    };
}
