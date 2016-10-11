package com.vivifram.second.hitalk.ui.pop;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.ui.page.layout.BlackboardFragmentLayout;
import com.zuowei.utils.common.DisplayUtil;


/**
 * Created by zuowei on 16-8-14.
 */
public class CommentActionPopup extends BasePopupWindow implements View.OnClickListener{

    public static final int ACTION_COPY = 0x01;
    public static final int ACTION_DELETE = 0x02;
    public static final int ACTION_BOTH = 0x03;
    private int mAction;
    private View mPopupView;
    private LinearLayout mContentLayout;
    private BlackboardFragmentLayout.BnItemOnClickListener mBnItemOnClickListener;
    private CommentItem mCommentItem;
    private int mBnPosition;

    public CommentActionPopup(Activity context,BlackboardFragmentLayout.BnItemOnClickListener bnItemOnClickListener,
                              CommentItem commentItem, int bnPosition) {
        this(context,ACTION_BOTH,bnItemOnClickListener,commentItem,bnPosition);
    }

    public CommentActionPopup(Activity context, int actionType ,BlackboardFragmentLayout.BnItemOnClickListener bnItemOnClickListener,
                              CommentItem commentItem, int bnPosition) {
        super(context);
        mAction = actionType;
        mBnItemOnClickListener = bnItemOnClickListener;
        mCommentItem = commentItem;
        mBnPosition = bnPosition;
        fill();
    }

    public CommentActionPopup(Activity context, int actionType, int w, int h,BlackboardFragmentLayout.BnItemOnClickListener bnItemOnClickListener,
                              CommentItem commentItem, int bnPosition) {
        super(context, w, h);
        mAction = actionType;
        mBnItemOnClickListener = bnItemOnClickListener;
        mCommentItem = commentItem;
        mBnPosition = bnPosition;
        fill();
    }

    private void fill(){
        switch (mAction){
            case ACTION_BOTH:
                addAction(mContext.getString(R.string.copy),this,R.id.action_copy);
                addLine();
                addAction(mContext.getString(R.string.delete),this,R.id.action_delete);
                break;
            case ACTION_COPY:
                addAction(mContext.getString(R.string.copy),this,R.id.action_copy);
                break;
            case ACTION_DELETE:
                addAction(mContext.getString(R.string.delete),this,R.id.action_delete);
                break;
        }
    }

    @Override
    protected Animation getShowAnimation() {
        return getTranslateAnimation(250*2,0,300);
    }

    @Override
    protected View getClickToDismissView() {
        return mPopupView.findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View getPopupView() {
        mPopupView= LayoutInflater.from(mContext).inflate(R.layout.comment_action_layout,null);
        mContentLayout = (LinearLayout) mPopupView.findViewById(R.id.popup_anima);
        return mPopupView;
    }

    private void addAction(String text, View.OnClickListener onClickListener,int id){
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , DisplayUtil.dip2px(mContext,60)));
        textView.setGravity(Gravity.CENTER);
        textView.setText(text);
        textView.setId(id);
        textView.setOnClickListener(onClickListener);
        mContentLayout.addView(textView);
    }

    private void addLine(){
        View view = new View(mContext);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , DisplayUtil.dip2px(mContext,4)));
        view.setBackground(mContext.getDrawable(R.drawable.line_bg));
        mContentLayout.addView(view);
    }

    @Override
    public View getAnimaView() {
        return mContentLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_copy:
                if (mCommentItem != null) {
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(mCommentItem.getContent());
                }
                dismiss();
                break;
            case R.id.action_delete:
                if (mBnItemOnClickListener != null && mCommentItem != null) {
                    mBnItemOnClickListener.deleteComment(mBnPosition, mCommentItem.getId());
                }
                dismiss();
                break;
        }
    }
}
