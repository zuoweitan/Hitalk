package com.vivifram.second.hitalk.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.ui.layout.BaseLayout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.UIUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        //理论上应该放在launcher activity,放在基类中所有集成此库的app都可以避免此问题

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
        mAppCtx = HiTalkApplication.mAppContext;

        sAliveActivities.add(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mLayout != null) {
            mLayout.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayout != null) {
            mLayout.onDestroy();
        }
        sAliveActivities.remove(this);
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
            mLayout.onContentViewCreate(getBaseView());
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
}
