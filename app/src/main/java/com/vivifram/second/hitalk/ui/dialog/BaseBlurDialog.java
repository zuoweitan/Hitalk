package com.vivifram.second.hitalk.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.Constants;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;

import static com.avos.avoscloud.Messages.StatusType.on;


/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-8 下午6:44
 * 修改人：zuowei
 * 修改时间：16-12-8 下午6:44
 * 修改备注：
 */
public abstract class BaseBlurDialog extends BlurDialogFragment {

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mUseRenderScript;
    private boolean mDebug;
    protected View contentView;
    protected Dialog dialog;
    private Callback callback;

    public interface Callback{
        void onCreateDone();
    }

    public static <T extends BaseBlurDialog> T newInstance(Class<T> c,int radius,
                                                   float downScaleFactor,
                                                   boolean dimming,
                                                   boolean useRenderScript,
                                                   boolean debug) {
        T fragment = null;
        try {
            fragment = c.newInstance();
        } catch (Exception e){
            NLog.e(TagUtil.makeTag(c),"new failed : ",e);
        }
        Bundle args = new Bundle();
        args.putInt(
                Constants.BlurDialog.BUNDLE_KEY_BLUR_RADIUS,
                radius
        );
        args.putFloat(
                Constants.BlurDialog.BUNDLE_KEY_DOWN_SCALE_FACTOR,
                downScaleFactor
        );
        args.putBoolean(
                Constants.BlurDialog.BUNDLE_KEY_DIMMING,
                dimming
        );
        args.putBoolean(
                Constants.BlurDialog.BUNDLE_KEY_USE_RENDERSCRIPT,
                useRenderScript
        );
        args.putBoolean(
                Constants.BlurDialog.BUNDLE_KEY_DEBUG,
                debug
        );

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mRadius = args.getInt(Constants.BlurDialog.BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(Constants.BlurDialog.BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(Constants.BlurDialog.BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(Constants.BlurDialog.BUNDLE_KEY_DEBUG);
        mUseRenderScript = args.getBoolean(Constants.BlurDialog.BUNDLE_KEY_USE_RENDERSCRIPT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity(),R.style.alter_dialog_style);
        contentView = createContentView(getActivity());
        dialog.setContentView(contentView);
        if (callback != null) {
            callback.onCreateDone();
        }
        return dialog;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog getDialog() {
        return dialog;
    }

    protected abstract View createContentView(Activity activity);

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return mUseRenderScript;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }
}
