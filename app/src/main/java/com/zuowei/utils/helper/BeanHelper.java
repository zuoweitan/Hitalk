package com.zuowei.utils.helper;

import com.google.gson.Gson;
import com.vivifram.second.hitalk.bean.BaseBean;
import com.zuowei.dao.greendao.Bean;
import com.zuowei.dao.greendao.BeanDao;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Task;
import de.greenrobot.dao.query.Query;

/**
 * Created by zuowei on 16-8-25.
 */
public class BeanHelper {

    public static Task<List<Bean>> findAllBeans() {
        return findAllBeans(null);
    }

    public static Task<List<Bean>> findAllBeans(final Collection<String> excludeLocalIds) {

        return Task.callInBackground(new Callable<List<Bean>>() {
            @Override
            public List<Bean> call() throws Exception {
                BeanDao beansDao = DaoHelper.getInstance().getBeansDao();
                Query<Bean> build = beansDao.queryBuilder().where(BeanDao.Properties.LocalId.notIn(excludeLocalIds))
                        .orderDesc(BeanDao.Properties.CreateAt)
                        .build();
                return build.list();
            }
        });
    }

    public static Task<Bean> saveBean(final BaseBean bean){
        return Task.callInBackground(new Callable<Bean>() {
            @Override
            public Bean call() throws Exception {
                BeanDao beansDao = DaoHelper.getInstance().getBeansDao();
                Bean bn = new Bean();
                bn.setLocalId(bean.getLocalId());
                bn.setClassName(bean.getClassName());
                bn.setContent(new Gson().toJson(bean));
                bn.setCreateAt(bean.getCreateTime());
                bn.setCommandType(bean.getCommandType());
                beansDao.insertOrReplace(bn);
                return bn;
            }
        });
    }

    public static Task<Bean> deleteBean(final Bean bean){
        return Task.callInBackground(new Callable<Bean>() {
            @Override
            public Bean call() throws Exception {
                BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
                beanDao.deleteByKey(bean.getLocalId());
                return bean;
            }
        });
    }

    public static Task<Void> deleteBean(final String id){
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
                beanDao.deleteByKey(id);
                return null;
            }
        });
    }

    public static Task<Bean> queryBean(final String id){
        return Task.callInBackground(new Callable<Bean>() {
            @Override
            public Bean call() throws Exception {
                BeanDao beanDao = DaoHelper.getInstance().getBeansDao();
                Query<Bean> build = beanDao.queryBuilder().where(BeanDao.Properties.LocalId.eq(id)).build();
                return build.unique();
            }
        });
    }
}
