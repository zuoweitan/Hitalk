package com.vivifram.second.hitalk.bean.address;

/**
 * Created by zuowei on 16-10-13.
 */

public class SchoolMate {

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

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    private String sortLetters;
}
