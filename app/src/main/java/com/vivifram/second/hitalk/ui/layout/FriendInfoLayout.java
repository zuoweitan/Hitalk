package com.vivifram.second.hitalk.ui.layout;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.BindView;
import com.vivifram.second.hitalk.base.LayoutIdBinder;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.vivifram.second.hitalk.ui.view.ShrinkButton;
import com.zuowei.utils.common.DisplayUtil;
import com.zuowei.utils.common.JsonUtils;
import com.zuowei.utils.common.TextSpanUtils;
import com.zuowei.utils.helper.ChatHelper;

import java.util.List;

import static android.R.attr.id;

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

    @BindView(id = R.id.avtarIv)
    private ImageView avtarIv;
    @BindView(id = R.id.nickTv)
    private TextView nickTv;
    @BindView(id = R.id.sexIv)
    private ImageView sexIv;
    @BindView(id = R.id.collegeTv)
    private TextView collegeTv;
    @BindView(id = R.id.interestEt)
    private EditText interestEt;
    @BindView(id = R.id.titleBar)
    private BGATitlebar titlebar;
    @BindView(id = R.id.addFriendSb)
    private ShrinkButton shrinkButton;
    private OnLayoutActionListener onLayoutActionListener;

    public interface OnLayoutActionListener{
        void onBack();
        void addFriend();
        void onTalkWithFriend();
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        LayoutIdBinder.LAYOUT.bindViewToLayout(this);
        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                super.onClickLeftCtv();
                if (onLayoutActionListener != null) {
                    onLayoutActionListener.onBack();
                }
            }
        });

        shrinkButton.setTag(R.id.button_type,1);
        shrinkButton.setOnClickListener(View -> {
            shrinkButton.setEnabled(false);
            if (onLayoutActionListener != null) {
                int type = (int) shrinkButton.getTag(R.id.button_type);
                if (type == 1) {
                    onLayoutActionListener.addFriend();
                } else if (type == 2){
                    onLayoutActionListener.onTalkWithFriend();
                }
            } else {
                enableButton();
            }
        });
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
        fillInterest(schoolMate.getInterest());
    }

    private void fillInterest(String interests){
        List<Object> interestList = JsonUtils.toObjectList(interests);
        if (interestList != null){
            SpannableStringBuilder sb = new SpannableStringBuilder();
            SpannableString s;
            for (Object interestObj : interestList){
                String item = interestObj + "";
                s = new SpannableString(item);
                s.setSpan(TextSpanUtils.getTextWithBackground(mRes.getDrawable(R.drawable.text_backgroud,null),
                        DisplayUtil.dip2px(mAppCtx,16), DisplayUtil.dip2px(mAppCtx,12), mRes.getColor(R.color.hint)),
                        0,item.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                sb.append(s);
                sb.append(" ");
            }
            interestEt.clearComposingText();
            interestEt.setText(sb);
        }
    }

    public void enableButton(){
        shrinkButton.setEnabled(true);
        shrinkButton.reset();
        shrinkButton.setBackground(mRes.getDrawable(R.drawable.button_rounded_color_yellow_background));
    }

    public void disableButton(){
        shrinkButton.setEnabled(false);
        shrinkButton.setBackground(mRes.getDrawable(R.drawable.button_rounded_color_grey_background));
    }

    /**
     *
     * @param type type 1:add friend  type 2:talk type 3:wait
     */
    public void setButtonType(int type){
        shrinkButton.setTag(R.id.button_type,type);
        switch (type){
            case 1:
                    shrinkButton.setText(mRes.getString(R.string.sendMessage));
                break;
            case 2:
                    shrinkButton.setText(mRes.getString(R.string.add_friend));
                break;
            case 3:
                    shrinkButton.setText(mRes.getString(R.string.wait_for_verfiy));
                break;
        }
    }

}
