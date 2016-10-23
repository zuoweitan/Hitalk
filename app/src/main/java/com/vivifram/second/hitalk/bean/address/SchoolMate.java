package com.vivifram.second.hitalk.bean.address;

import com.vivifram.second.hitalk.ui.view.slidebar.Indexable;

/**
 * Created by zuowei on 16-10-13.
 */

public class SchoolMate extends LetterMark implements Indexable{

    private String nickName;

    public String getsInfo() {
        return sInfo;
    }

    public void setsInfo(String sInfo) {
        this.sInfo = sInfo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String sInfo;

    @Override
    public String getIndex() {
        return getSortLetters();
    }
}
