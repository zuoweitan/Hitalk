package com.vivifram.second.hitalk.ui;

import java.util.HashMap;

/**
 * Created by zuowei on 17-2-8.
 */

public class ParamsPool extends HashMap<Integer,Object> {

    private static ParamsPool sInstance;

    public static ParamsPool $(){
        if (sInstance == null){
            sInstance = new ParamsPool();
        }
        return sInstance;
    }
}
