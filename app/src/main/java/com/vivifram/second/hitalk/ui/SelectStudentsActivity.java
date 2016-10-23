package com.vivifram.second.hitalk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.ui.layout.SelectStudentsLayout;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
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
        mLayout.addSchoolMates(mockStudents());
        mLayout.setOnTitleActionListener(new SelectStudentsLayout.OnTitleActionListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onConfirm() {

            }
        });
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
        context.startActivity(new Intent(context,SelectStudentsActivity.class));
    }
}
