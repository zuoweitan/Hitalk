package com.vivifram.second.hitalk.base;

import android.view.View;

import com.vivifram.second.hitalk.ui.layout.BaseLayout;
import com.vivifram.second.hitalk.ui.page.layout.BaseFragmentLayout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

                        if (annotation.boundClick()) {
                            Object bindObj = bindField.get(t);
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

                        if (annotation.boundClick()) {
                            Object bindObj = bindField.get(t);
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
    },

    LAYOUT_RECYCLER_VIEWHOLDER(){
        @Override
        public <T extends BaseRecyclerViewHolder> void bindViewToHolder(T t) {
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

    public <T extends BaseLayout> void bindViewToLayout(T t){
        throw new UnsupportedOperationException();
    }
    public <T extends BaseFragmentLayout> void bindViewToFLayout(T t){
        throw new UnsupportedOperationException();
    }
    public <T extends BaseLayoutBean> void bindView(T t){
        throw new UnsupportedOperationException();
    }
    public <T extends BaseRecyclerViewHolder> void bindViewToHolder(T t){
        throw new UnsupportedOperationException();
    }
}
