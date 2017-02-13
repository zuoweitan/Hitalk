package com.vivifram.second.hitalk.ui.layout;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.zuowei.utils.helper.ChatHelper;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-14 下午3:15
 * 修改人：zuowei
 * 修改时间：16-12-14 下午3:15
 * 修改备注：
 */
public class FriendInfoLayout extends BaseLayout{

    public FriendInfoLayout(View rootView) {
        super(rootView);
    }

    private ImageView avtarIv;
    private TextView nickTv;
    private ImageView sexIv;
    private TextView collegeTv;
    private TextView signatureTv;

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);

        avtarIv = (ImageView) findViewById(R.id.avtarIv);
        nickTv = (TextView) findViewById(R.id.nickTv);
        sexIv = (ImageView) findViewById(R.id.sexIv);
        collegeTv = (TextView) findViewById(R.id.collegeTv);
        signatureTv = (TextView) findViewById(R.id.signatureTv);//// TODO: 17-2-13 change the signature to interest
    }

    public void bindSchoolMate(SchoolMate schoolMate){
        if (schoolMate == null) {
            return;
        }

        ChatHelper.setUserAvatar(mAppCtx,schoolMate.getUserId(),avtarIv);//?may be wrong?
        nickTv.setText(schoolMate.getNickName());
        switch (schoolMate.getSex()){
            case Constants.User.SEX.MAIL:
                sexIv.setImageResource(R.drawable.profile_ic_male);
                break;
            case Constants.User.SEX.FEMALE:
                sexIv.setImageResource(R.drawable.profile_ic_female);
                break;
        }

        collegeTv.setText(schoolMate.getCollege());
    }

}
