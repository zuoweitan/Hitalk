package com.vivifram.second.hitalk.base;

import android.content.Context;
import android.view.View;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.ui.layout.BaseLayout;
import com.vivifram.second.hitalk.ui.page.layout.BaseFragmentLayout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.R.attr.name;

/**
 * 项目名称：AazenWearHome
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-2-21 下午4:53
 * 修改人：zuowei
 * 修改时间：17-2-21 下午4:53
 * 修改备注：
 */
public enum LayoutIdBinder {
    //for ActivityLayout
    LAYOUT(){
        @Override
        public <T extends BaseLayout> void bindViewToLayout(T t) {
            Field[] fields = t.getClass().getDeclaredFields();
            if (fields != null) {
                for (Field bindField : fields) {
                    BindView annotation = bindField.getAnnotation(BindView.class);
                    if (annotation == null) {
                        continue;
                    }
                    int id = annotation.id();
                    try {
                        bindField.setAccessible(true);
                        bindField.set(t,t.findViewById(id));
                    } catch (Exception e) {
                        NLog.i(TAG,"bindField failed : ",e);
                    }
                }
            }
        }

        @Override
        public <T extends BaseFragmentLayout> void bindViewToFLayout(T t) {
            throw new RuntimeException();
        }
    },
    //for FragmentLayout
    FLAYOUT(){
        @Override
        public <T extends BaseFragmentLayout> void bindViewToFLayout(T t) {
            Field[] fields = t.getClass().getDeclaredFields();
            if (fields != null) {
                for (Field bindField : fields) {
                    BindView annotation = bindField.getAnnotation(BindView.class);
                    if (annotation == null) {
                        continue;
                    }
                    int id = annotation.id();
                    try {
                        bindField.setAccessible(true);
                        bindField.set(t,t.findViewById(id));
                    } catch (Exception e) {
                        NLog.i(TAG,"bindField failed : ",e);
                    }
                }
            }
        }

        @Override
        public <T extends BaseLayout> void bindViewToLayout(T t) {
            throw new RuntimeException();
        }
    },

    LAYOUT_BEAN(){
        @Override
        public <T extends BaseLayoutBean> void bindView(T t) {
            Field[] fields = t.getClass().getDeclaredFields();
            if (fields != null) {
                for (Field bindField : fields) {
                    BindView annotation = bindField.getAnnotation(BindView.class);
                    if (annotation == null) {
                        continue;
                    }
                    int id = annotation.id();
                    try {
                        bindField.setAccessible(true);
                        bindField.set(t,t.findViewById(id));

                        if (annotation.boundClick()) {
                            Object bindObj = bindField.get(t);
                            NLog.i(TAG, "bindObj type = " + bindObj.getClass().getName());
                            Method setOnClickListener = View.class.getDeclaredMethod("setOnClickListener", View.OnClickListener.class);
                            setOnClickListener.setAccessible(true);
                            setOnClickListener.invoke(bindObj, t);
                        }

                    } catch (Exception e) {
                        NLog.i(TAG,"bindField failed : ",e);
                    }
                }
            }
        }
    };

    static String TAG = TagUtil.makeTag(LayoutIdBinder.class);

    public <T extends BaseLayout> void bindViewToLayout(T t){}
    public <T extends BaseFragmentLayout> void bindViewToFLayout(T t){}
    public <T extends BaseLayoutBean> void bindView(T t){}
}
