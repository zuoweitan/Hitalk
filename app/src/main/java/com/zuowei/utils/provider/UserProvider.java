package com.zuowei.utils.provider;

import com.avos.avoscloud.AVUser;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.helper.UserBeanCacheHelper;
import com.zuowei.utils.helper.UserCacheHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuowei on 16-8-8.
 */
public class UserProvider implements IProvider<String,User> {


    @Override
    public void fetch(List<String> ids, final DoneCallback<User> doneCallback) {
        UserCacheHelper.getInstance().fetchUsers(ids, (userList, e) -> {
            List<User> users = new ArrayList<>();
            if (userList != null && userList.size() > 0){
                for (AVUser avUser : userList) {
                    User user = new User();
                    UserBeanCacheHelper.AvUserToUser(avUser,user);
                    users.add(user);
                }
            }
            doneCallback.done(users,e);
        });
    }
}
