package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.os.Bundle;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.FriendsManager;
import com.vivifram.second.hitalk.manager.chat.SchoolMatesManager;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.vivifram.second.hitalk.ui.layout.SelectStudentsLayout;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.pinyin.CharacterParser;
import com.zuowei.utils.pinyin.LetterComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zuowei on 16-10-23.
 */
@LayoutInject(name = "SelectStudentsLayout")
public class SelectStudentsActivity extends BaseActivity<SelectStudentsLayout> {

    CharacterParser characterParser;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_select_students);
        characterParser = CharacterParser.getInstance();
        mLayout.setOnTitleActionListener(new SelectStudentsLayout.OnTitleActionListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onConfirm() {
                List<SchoolMate> selectedSchoolMates = mLayout.getSelectedSchoolMates();
                if (selectedSchoolMates.size() <= 0) {
                    NToast.shortToast(SelectStudentsActivity.this, R.string.select_student_warn);
                } else {
                    String[] userIds = new String[selectedSchoolMates.size()];
                    for (int i = 0; i < selectedSchoolMates.size(); i++) {
                        userIds[i] = selectedSchoolMates.get(i).getUserId();
                    }
                    ChatRoomActivity.start(SelectStudentsActivity.this, Constants.ParamsKey.Chat.TO_GROUP,userIds);
                    finish();
                }
            }
        });

        fetchFriends(true, (list, e) -> {
            if (e != null && list == null) {
                NToast.shortToast(this, R.string.friends_fetch_error);
                finish();
            }
        });
    }

    private void fetchFriends(final boolean isforce, DoneCallback<SchoolMate> doneCallback) {
        FriendsManager.getInstance().fetchFriends(isforce, new AVCallback<List<User>>() {
            @Override
            protected void internalDone0(List<User> users, AVException e) {

                ArrayList<SchoolMate> friends = new ArrayList<>();
                if (e == null && users != null){
                    for (User user : users) {
                        SchoolMate schoolMate = convertToSchoolMate(user);
                        if (schoolMate != null){
                            friends.add(schoolMate);
                        }
                    }
                    if (friends.size() > 0) {
                        Collections.sort(friends, new LetterComparator<>());
                        mLayout.addSchoolMates(friends);
                    }
                }

                if (doneCallback != null) {
                    doneCallback.done(friends,e);
                }
            }
        });
    }

    private SchoolMate convertToSchoolMate(User user) {
        if (user != null) {
            SchoolMate schoolMate  = new SchoolMate();
            schoolMate.setNickName(user.getNick())
                    .setUserId(user.getObjectId())
                    .setSex(user.getSex())
                    .setCollege(user.getCollege())
                    .setInterest(user.getInterest())
                    .setAvater(user.getAvatar());
            SchoolMatesManager.getInstance().fillLetters(schoolMate);
            return schoolMate;
        }
        return null;
    }


    private String mates[] = new String[]{"檀为为","檀为为","吴非凡",
            "吴非凡","吴非凡","吴非凡","吴非凡","邓超",
            "邓超","邓超","邓超","邓超","你瞧","杨树伟"};

    private List<SchoolMate> mockStudents() {
        ArrayList<SchoolMate> list = new ArrayList<>();
        for (String mate : mates) {
            SchoolMate schoolMate = new SchoolMate();
            schoolMate.setNickName(mate);
            schoolMate.setsInfo("清华大学");
            String pinyin = characterParser.getSelling(schoolMate.getNickName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                schoolMate.setSortLetters(sortString.toUpperCase());
            } else {
                schoolMate.setSortLetters("#");
            }
            list.add(schoolMate);
        }
        Collections.sort(list,new LetterComparator<SchoolMate>());
        return list;
    }

    public static void start(Context context){
        start(context,SelectStudentsActivity.class);
    }
}
