package com.vivifram.second.hitalk.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.ui.ParamsPool;
import com.vivifram.second.hitalk.ui.layout.BaseLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zuowei on 16-7-14.
 */
public class BaseActivity<T extends BaseLayout> extends FragmentActivity {
    private static final String TAG = TagUtil.makeTag(BaseActivity.class);
    private static final String PREFIX = "com.vivifram.second.hitalk.ui.layout.";
    protected Context mAppCtx;
    protected T mLayout;
    protected InputMethodManager inputMethodManager;
    private static CopyOnWriteArrayList<BaseActivity> sAliveActivities = new CopyOnWriteArrayList<>();
    private SparseArray sParamsPool = ParamsPool.$();
    protected Object params;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        hideSoftKeyboard();
        mAppCtx = HiTalkApplication.mAppContext;

        sAliveActivities.add(this);
        checkAndInstallEatMark();

        params = removeParams(getIntent());
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
                        /*if (eatMark.target() != Object.class) {
                            Class abstractHandlerClass = declaredClass;
                            for(; abstractHandlerClass != AbstractHandler.class && abstractHandlerClass != Object.class
                                    ; abstractHandlerClass = abstractHandlerClass.getSuperclass());
                            if (abstractHandlerClass == AbstractHandler.class) {
                                Field target = abstractHandlerClass.getDeclaredField("target");
                                target.setAccessible(true);
                                target.set(iEater, eatMark.target());
                            }
                        }*/
                    } catch (InstantiationException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } catch (IllegalAccessException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } catch (NoSuchMethodException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } catch (InvocationTargetException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    } /*catch (NoSuchFieldException e) {
                        NLog.e(TAG, "checkAndInstallEatMark failed :",e);
                    }*/
                    if (iEater != null) {
                        eater.add(iEater);
                        EaterManager.getInstance().registerEater(eatMark.action(),iEater);
                    }
                }
            }
        } else {
            NLog.i(TAG, "No EatMarks");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mLayout != null) {
            mLayout.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayout != null) {
            mLayout.onDestroy();
        }
        sAliveActivities.remove(this);
        unInstallEatMark();

    }

    private void unInstallEatMark() {
        for (IEater iEater : eater) {
            EaterManager.getInstance().unRegisterEater(iEater);
        }
        eater.clear();
        eater = null;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mLayout != null) {
            mLayout.onWindowAttached();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLayout != null) {
            mLayout.onWindowDetached();
        }
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    protected View getBaseView(){
        return getWindow().getDecorView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mLayout = generateLayout();
        if (mLayout != null) {
            bindAllInterfaces();
            LayoutIdBinder.LAYOUT.bindViewToLayout(mLayout);
            mLayout.onContentViewCreate(getBaseView());
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

    private T generateLayout() {
        LayoutInject layoutInject = getClass().getAnnotation(LayoutInject.class);
        if (layoutInject != null){
            String layoutName = layoutInject.name();
            try {
                Class layoutC = Class.forName(PREFIX + layoutName);
                Constructor constructor = layoutC.getConstructor(View.class);
                constructor.setAccessible(true);
                return (T) constructor.newInstance(getBaseView());
            }catch (InvocationTargetException ex){
                NLog.e(TAG,"generateLayout failed : ",ex.getTargetException());
            } catch (Exception e) {
                NLog.e(TAG,"generateLayout failed : ",e);
            }
        }
        return null;
    }

    protected void notifyActivityDestroy(Class c){
        BaseActivity target = null;
        for (BaseActivity sAliveActivity : sAliveActivities) {
            if (c.equals(sAliveActivity.getClass())){
                target = sAliveActivity;
                break;
            }
        }
        if (target != null) {
            target.finish();
        }
    }

    protected void notifyAllActivityDestroy(){
        for (BaseActivity activity:sAliveActivities){
            if (activity.isDestroyed() || activity.isFinishing())
                continue;
            activity.finish();
        }

        sAliveActivities.clear();
    }


    public static <T extends BaseActivity> void start(Context ctx,Class<T> c){
        if (ctx == null) {
            return;
        }
        Intent intent = new Intent(ctx,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static <T extends BaseActivity> void start(Context ctx,Class<T> c,int key){
        if (ctx == null) {
            return;
        }
        Intent intent = new Intent(ctx,c);
        intent.putExtra("key",key);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    private Object removeParams(Intent intent){
        if (intent != null) {
            int key = intent.getIntExtra("key",-1);
            Object params = sParamsPool.get(key);
            sParamsPool.remove(key);
            return params;
        }
        return null;
    }
}
