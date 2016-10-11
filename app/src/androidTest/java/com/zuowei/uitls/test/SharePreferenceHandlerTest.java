package com.zuowei.uitls.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.vivifram.second.hitalk.ui.HiTalkActivity;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.IEater;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.SharePreferenceParam;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.SharePreferenceHandler;

/**
 * Created by zuowei on 16-7-12.
 */
public class SharePreferenceHandlerTest extends ActivityInstrumentationTestCase2<HiTalkActivity> implements
        IEater{

    private static final String TAG = TagUtil.makeTag(SharePreferenceHandlerTest.class);
    private Context mCtx;
    private SharePreferenceHandler mSpf;

    public SharePreferenceHandlerTest(){
        super(HiTalkActivity.class);
    }

    public void setup (Context context){
        mCtx = context;
        mSpf = SharePreferenceHandler.getInstance();
        mSpf.setContext(mCtx);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_WITH_PREFERENCE,mSpf);
        EaterManager.getInstance().registerEater(EaterAction.ACTION_DO_WITH_PREFERENCE,this);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setup(getActivity());
    }

    public void testOnEat(){
        SharePreferenceParam spfp =  new SharePreferenceParam();
        spfp.setName("1");
        spfp.setValue("hello world");
        EaterManager.getInstance().broadcast(spfp);
    }

    @Override
    public void onEat(LightParam param) {
        Log.i(TagUtil.makeTag(SharePreferenceHandlerTest.class),"onEat");
    }
}
