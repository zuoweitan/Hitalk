package com.zuowei.utils.bridge.params;

import android.os.Bundle;

/**
 * Created by zuowei on 16-4-6.
 */
public abstract class AbstractParam {
    Bundle mBundle;

    public void setData(Bundle data){
        mBundle = data;
    }

    public Bundle getData() {
        return mBundle;
    }
}
