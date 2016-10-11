package com.vivifram.second.hitalk.ui.recycleview.blackboard;

import com.zuowei.dao.greendao.User;

public class CommentConfig {

    public enum Type{
        PUBLIC("public"),
        REPLY("reply");

        private String value;
        private Type(String value){
            this.value = value;
        }

    }

    public int mBnPosition;
    public int mCommentPosition;
    public Type mCommentType;
    public User mReplyUser;

    @Override
    public String toString() {
        String replyUserStr = "";
        if(mReplyUser != null){
            replyUserStr = mReplyUser.toString();
        }
        return "mBnPosition = " + mBnPosition
                + "; mCommentPosition = " + mCommentPosition
                + "; mCommentType ＝ " + mCommentType
                + "; mReplyUser = " + replyUserStr;
    }
}