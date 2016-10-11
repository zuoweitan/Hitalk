package com.zuowei.utils.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zuowei on 16-7-29.
 */
public class JsonUtils {
    private static String TAG = TagUtil.makeTag(JsonUtils.class);

    public static String toJsonString(List<Object> list){
        if (list == null) return null;
        JSONObject object = new JSONObject();
        try {
            for (int i = 0; i < list.size(); i++) {
                try {
                    object.put(i+"",list.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            NLog.e(TAG,"JsonUtils failed :",e);
           return null;
        }
        return object.toString();
    }
}
