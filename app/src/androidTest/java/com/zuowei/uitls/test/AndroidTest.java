package com.zuowei.uitls.test;

import android.test.AndroidTestCase;

import com.zuowei.utils.helper.LoginHelper;

/**
 * Created by zuowei on 16-7-31.
 */
public class AndroidTest extends AndroidTestCase {

    public void testRetrofit(){
        LoginHelper.getInstance().requestVerifyMobilePhone("15000435912");
    }
}
