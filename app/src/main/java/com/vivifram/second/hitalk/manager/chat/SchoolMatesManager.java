package com.vivifram.second.hitalk.manager.chat;

import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.AddRequest;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.params.address.SchoolMateStateParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.SchoolmatesCacheHelper;
import com.zuowei.utils.helper.UserBeanCacheHelper;
import com.zuowei.utils.helper.UserCacheHelper;
import com.zuowei.utils.pinyin.CharacterParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bolts.Task;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-26 下午8:39
 * 修改人：zuowei
 * 修改时间：16-10-26 下午8:39
 * 修改备注：
 */
public class SchoolMatesManager {

    private static SchoolMatesManager sInstance;

    private SchoolMatesManager(){}

    public static SchoolMatesManager getInstance(){
        if (sInstance == null) {
            synchronized (SchoolMatesManager.class){
                if (sInstance == null) {
                    sInstance = new SchoolMatesManager();
                }
            }
        }
        return sInstance;
    }

    public Task<List<SchoolMate>> queryAllSchoolMatesWithState(Map<String,Object> conditions){
        return queryAllSchoolMates(conditions).continueWithTask(task -> {
            FriendsManager.getInstance().findSendRequests(new FindCallback<AddRequest>() {
                @Override
                public void done(List<AddRequest> list, AVException e) {
                    if (list != null){
                        for (AddRequest addRequest : list) {
                            if (addRequest.getStatus() == AddRequest.STATUS_DONE){
                                SchoolmatesCacheHelper.getInstance()
                                        .update(addRequest.getToUser().getObjectId(),SchoolmatesCacheHelper.REQUEST_STATE_SUCCESS);
                            } else if (addRequest.getStatus() == AddRequest.STATUS_WAIT){
                                SchoolmatesCacheHelper.getInstance()
                                        .update(addRequest.getToUser().getObjectId(),SchoolmatesCacheHelper.REQUEST_STATE_WATING);
                            }
                        }
                        EaterManager.getInstance().broadcast(new SchoolMateStateParam());
                    }
                }
            },true,null);
            return task;
        });
    }

    public Task<List<SchoolMate>> queryAllSchoolMates(Map<String,Object> conditions){
        return Task.callInBackground(()->{
            NLog.i(TagUtil.makeTag(getClass()),"queryAllSchoolMates");
            List<SchoolMate> results = new ArrayList<>();

            AVQuery<AVUser> avQuery = AVUser.getQuery();

            avQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);

            if (conditions != null) {
                for (Map.Entry<String, Object> condition : conditions.entrySet()) {
                    avQuery.whereEqualTo(condition.getKey(),condition.getValue());
                }
            }
            avQuery.whereNotEqualTo(Constants.User.OBJECTID_C,HiTalkHelper.getInstance().getCurrentUserId());

            List<AVUser> list = avQuery.find();
            NLog.i(TagUtil.makeTag(getClass()),"queryAllSchoolMates list = "+list);
            if (list != null) {
                for (AVUser avUser : list) {
                    UserCacheHelper.getInstance().cacheUser(avUser);
                    User user = new User();
                    UserBeanCacheHelper.AvUserToUser(avUser,user);
                    UserBeanCacheHelper.getInstance().cacheUser(user);
                    SchoolMate schoolMate  = new SchoolMate();
                    schoolMate.setNickName(user.getNick())
                            .setUserId(user.getObjectId())
                            .setSex(user.getSex())
                            .setCollege(user.getCollege())
                            .setInterest(user.getInterest());
                    fillLetters(schoolMate);
                    NLog.i(TagUtil.makeTag(getClass()),"schoolMate = "+schoolMate);
                    results.add(schoolMate);
                }
            }

            return results;
        });
    }

    public void fillLetters(SchoolMate schoolMate){
        if (schoolMate == null || schoolMate.getNickName() == null){
            return;
        }

        if (TextUtils.isEmpty(schoolMate.getNickName())){
            schoolMate.setSortLetters("#");
            return;
        }

        String pinyin = CharacterParser.getInstance().getSelling(schoolMate.getNickName());
        String sortString = pinyin.substring(0, 1).toUpperCase();

        if (sortString.matches("[A-Z]")) {
            schoolMate.setSortLetters(sortString.toUpperCase());
        } else {
            schoolMate.setSortLetters("#");
        }
    }
}
