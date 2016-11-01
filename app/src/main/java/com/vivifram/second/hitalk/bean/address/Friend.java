package com.vivifram.second.hitalk.bean.address;

/**
 * Created by zuowei on 16-10-15.
 */

public class Friend extends LetterMark{

    private String nickName;
    private String userId;
    private String avatarUrl;

    public String getNickName() {
        return nickName;
    }

    public Friend setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Friend setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Friend setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }
}
