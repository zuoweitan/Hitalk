package com.zuowei.dao.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SCHOOLMATE".
 */
public class Schoolmate {

    private String userId;
    /** Not-null value. */
    private String releatedId;
    private Integer friendState;
    private java.util.Date createAt;

    public Schoolmate() {
    }

    public Schoolmate(String userId) {
        this.userId = userId;
    }

    public Schoolmate(String userId, String releatedId, Integer friendState, java.util.Date createAt) {
        this.userId = userId;
        this.releatedId = releatedId;
        this.friendState = friendState;
        this.createAt = createAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** Not-null value. */
    public String getReleatedId() {
        return releatedId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setReleatedId(String releatedId) {
        this.releatedId = releatedId;
    }

    public Integer getFriendState() {
        return friendState;
    }

    public void setFriendState(Integer friendState) {
        this.friendState = friendState;
    }

    public java.util.Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(java.util.Date createAt) {
        this.createAt = createAt;
    }

}
