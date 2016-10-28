package com.vivifram.second.hitalk.bean.address;

import com.vivifram.second.hitalk.ui.view.slidebar.Indexable;

/**
 * Created by zuowei on 16-10-13.
 */

public class SchoolMate extends LetterMark implements Indexable{

    private String nickName;

    private String userId;

    private String sInfo;

    public String getNickName() {
        return nickName;
    }

    public SchoolMate setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getsInfo() {
        return sInfo;
    }

    public SchoolMate setsInfo(String sInfo) {
        this.sInfo = sInfo;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public SchoolMate setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String getIndex() {
        return getSortLetters();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SchoolMate)) {
            return false;
        }
        SchoolMate temp = (SchoolMate) o;
        if (temp.userId == null || userId == null)
            return false;
        return userId.equals(temp.userId);
    }

    @Override
    public String toString() {
        return "SchoolMate{" +
                "nickName='" + nickName + '\'' +
                ", userId='" + userId + '\'' +
                ", sInfo='" + sInfo + '\'' +
                '}' + " letter = "+getSortLetters();
    }
}
