package com.vivifram.second.hitalk.bean.search;

/**
 * Created by zuowei on 16-12-25.
 */

public class FriendSearchItem {

    public String id;
    public String avatar;
    public String nickName;
    public String college;

    public FriendSearchItem setId(String id) {
        this.id = id;
        return this;
    }

    public FriendSearchItem setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public FriendSearchItem setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public FriendSearchItem setCollege(String college) {
        this.college = college;
        return this;
    }
}
