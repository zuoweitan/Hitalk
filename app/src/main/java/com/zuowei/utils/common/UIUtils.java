package com.zuowei.utils.common;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by zuowei on 16-7-18.
 */
public class UIUtils {
    private static final String TAG = TagUtil.makeTag(UIUtils.class);
    /**
     * Adjust the size based on measureSpec.
     * @param desiredSize
     * @param measureSpec
     * @return
     */
    public static int getAdjustedSize(int desiredSize, int measureSpec){
        int result = desiredSize;
        switch(View.MeasureSpec.getMode(measureSpec)){
            case View.MeasureSpec.AT_MOST:
                result = Math.min(desiredSize, View.MeasureSpec.getSize(measureSpec));
                break;
            case View.MeasureSpec.EXACTLY:
                result = View.MeasureSpec.getSize(measureSpec);
                break;
            case View.MeasureSpec.UNSPECIFIED:
                break;
        }
        return result;
    }

    public static void startActivitySafety(Context context, Intent intent){
        try{
            context.startActivity(intent);
        }catch (Exception e){
            NLog.e(TAG,"startActivitySafety failed : ",e);
        }
    }

    public static void startActivitySafety(Context context, Class c){
        Intent intent = new Intent(context,c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivitySafety(context,intent);
    }
}
