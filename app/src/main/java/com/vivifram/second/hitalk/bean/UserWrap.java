package com.vivifram.second.hitalk.bean;

import com.avos.avoscloud.AVUser;
import com.zuowei.utils.common.Md5Utils;

import java.util.HashMap;

/**
 * Created by zuowei on 16-7-29.
 */
public class UserWrap extends HashMap<String,Object>{

    public UserWrap putNickName(String nickName){
        put("nickName",nickName);
        return this;
    }
    public UserWrap putCollege(String college){
        put("college",college);
        put("collegeCode", Md5Utils.stringToMD5(college));
        return this;
    }
    public UserWrap putSex(int sex){
        put("sex",sex);
        return this;
    }
    public UserWrap putInterest(String interest){
        put("interest",interest);
        return this;
    }

    public static UserWrap build(AVUser avUser){
        UserWrap userWrap = new UserWrap();
        userWrap.putNickName((String) avUser.get("nickName"));
        userWrap.putCollege((String) avUser.get("college"));
        if (avUser.get("sex") != null) {
            userWrap.putSex((Integer) avUser.get("sex"));
        }
        userWrap.putInterest((String) avUser.get("interest"));
        return userWrap;
    }

    public String getNickName() {
        return (String) get("nickName");
    }

    public String getCollege(){
        return (String) get("college");
    }

    public int getSex(){
        if (get("sex") == null){
            return 1;
        }
        return (int) get("sex");
    }

    public String getInterest(){
        return (String) get("interest");
    }
}
