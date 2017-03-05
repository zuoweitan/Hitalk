package com.vivifram.second.hitalk.ui;

import android.util.SparseArray;

/**
 * Created by zuowei on 17-2-8.
 */

public class ParamsPool extends SparseArray<Object> {

    private static ParamsPool sInstance;

    public static ParamsPool $(){
        if (sInstance == null){
            sInstance = new ParamsPool();
        }
        return sInstance;
    }
}
