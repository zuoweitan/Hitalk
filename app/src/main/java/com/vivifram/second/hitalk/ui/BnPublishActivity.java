package com.vivifram.second.hitalk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BaseActivity;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.state.CallBackWrap;
import com.vivifram.second.hitalk.state.SingleResult;
import com.vivifram.second.hitalk.ui.layout.BnPublishLayout;
import com.vivifram.second.hitalk.ui.layout.adapter.BnImagesAdapter;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.StaticDataCacheHelper;
import com.zuowei.utils.provider.BnDataProvider;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by zuowei on 16-8-15.
 */
@LayoutInject(name = "BnPublishLayout")
public class BnPublishActivity extends BaseActivity<BnPublishLayout> {
    private static final String TAG = TagUtil.makeTag(BnPublishActivity.class);

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.bn_publish_layout);
        init();
    }

    private void init() {
        mLayout.setImageItemOnClickListener(new BnImagesAdapter.OnImageItemOnClickListener() {
            @Override
            public void onMore() {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setPreviewEnabled(false)
                        .setShowCamera(true)
                        .setSelected(mLayout.getSelectedImages())
                        .start(BnPublishActivity.this);
            }

            @Override
            public void onItemClick(int position) {

            }
        });

        mLayout.setOnTitleClickListener(new BnPublishLayout.OnTitleClickListener() {
            @Override
            public void onLeftClick() {
                setResult(0,null);
                finish();
            }

            @Override
            public void onCenterClick() {

            }

            @Override
            public void onRightClick() {
                NLog.i(TAG,"onRightClick");
                BnDataProvider.createNormalBnData(mLayout.getContent(), mLayout.getSelectedImages(), CallBackWrap.wrapOnMain(new SingleResult() {
                    @Override
                    public void done(Object result, Exception e) {
                        NLog.i(TAG,"result = " + result + "onRightClick e = ",e);
                        if (e != null && result == null) {
                            NToast.shortToast(getApplicationContext(),getString(R.string.publish_failed));
                            setResult(0,null);
                            finish();
                        }else {
                            Intent intent = new Intent();
                            BnItem bnData = (BnItem) result;
                            String key = bnData.toString();
                            StaticDataCacheHelper.getInstance().cacheBnItem(key,bnData);
                            intent.putExtra("bnItem", key);
                            setResult(1, intent);
                            finish();
                        }
                    }
                }));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            ArrayList<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            if (photos != null) {
                mLayout.setSelectedImages(photos);
            }
        }
    }
}
