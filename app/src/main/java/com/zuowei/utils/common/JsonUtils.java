package com.zuowei.utils.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
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

    public static List<Object> toObjectList(String json){
        List<Object> list;
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                list = new ArrayList<>();
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()){
                    list.add(jsonObject.get(keys.next()));
                }
                return list;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
