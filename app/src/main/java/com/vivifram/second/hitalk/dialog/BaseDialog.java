package com.vivifram.second.hitalk.dialog;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by zuowei on 16-8-12.
 */
public class BaseDialog extends Dialog{
    public BaseDialog(Context context) {
        super(context);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
