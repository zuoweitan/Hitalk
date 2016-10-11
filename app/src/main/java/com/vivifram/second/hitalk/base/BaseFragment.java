package com.vivifram.second.hitalk.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.ui.page.layout.BaseFragmentLayout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by zuowei on 16-7-25.
 */
public abstract class BaseFragment<T extends BaseFragmentLayout> extends Fragment {
    private static final String TAG = TagUtil.makeTag(BaseFragment.class);
    private static final String PREFIX = "com.vivifram.second.hitalk.ui.page.layout.";
    protected Context mAppCtx;
    protected T mLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppCtx = HiTalkApplication.mAppContext;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);
        mLayout = generateLayout(view);
        if (mLayout != null) {
            mLayout.setActivity(getActivity());
            mLayout.onActivitySet();
            mLayout.onViewCreate(view);
        }
        onViewCreated();
        return view;
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLayout != null) {
            mLayout.onDestroy();
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

}
