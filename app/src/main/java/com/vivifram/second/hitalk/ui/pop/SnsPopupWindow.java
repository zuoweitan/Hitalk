package com.vivifram.second.hitalk.ui.pop;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.blackboard.SnsActionItem;
import com.zuowei.utils.common.DisplayUtil;

import java.util.ArrayList;


/**
 * 朋友圈点赞评论的popupwindow
 * 
 * @author wei.yi
 *
 * @edit zuowei
 * 
 */
public class SnsPopupWindow extends PopupWindow implements OnClickListener{

	private RelativeLayout digBtn;
	private RelativeLayout commentBtn;
	private TextView mLikeTv;
	private ImageView mLikeAnimaView;
	private AnimationSet mAnimationSet;

	// 实例化一个矩形
	private Rect mRect = new Rect();
	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];
	// 弹窗子类项选中时的监听
	private OnItemClickListener mItemClickListener;
	// 定义弹窗子类项列表
	private ArrayList<SnsActionItem> mActionItems = new ArrayList<SnsActionItem>();

	public void setmItemClickListener(OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}
	public ArrayList<SnsActionItem> getmActionItems() {
		return mActionItems;
	}
	public void setmActionItems(ArrayList<SnsActionItem> mActionItems) {
		this.mActionItems = mActionItems;
	}


	public SnsPopupWindow(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.sns_popupwindow_layout, null);
		digBtn = (RelativeLayout) view.findViewById(R.id.digBtn);
		commentBtn = (RelativeLayout) view.findViewById(R.id.commentBtn);
		mLikeTv = (TextView) view.findViewById(R.id.tv_like);
		mLikeAnimaView = (ImageView) view.findViewById(R.id.iv_like);
		digBtn.setOnClickListener(this);
		commentBtn.setOnClickListener(this);

		this.setContentView(view);
		this.setWidth(DisplayUtil.dip2px(context, 100));
		this.setHeight(DisplayUtil.dip2px(context, 25));
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		this.setAnimationStyle(R.style.social_pop_anim);

		initItemData();
		buildAnima();
	}
	private void initItemData() {
		addAction(new SnsActionItem("赞"));
		addAction(new SnsActionItem("评论"));
	}

	public void showPopupWindow(View parent){
		parent.getLocationOnScreen(mLocation);
		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + parent.getWidth(),mLocation[1] + parent.getHeight());
		mLikeTv.setText(mActionItems.get(0).mTitle);
		if(!this.isShowing()){
			showAtLocation(parent, Gravity.NO_GRAVITY, mLocation[0] - this.getWidth()
					, mLocation[1] - ((this.getHeight() - parent.getHeight()) / 2));
		}else{
			dismiss();
		}
	}

	private void buildAnima() {
		ScaleAnimation mScaleAnimation = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mScaleAnimation.setDuration(200);
		mScaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		mScaleAnimation.setFillAfter(false);

		AlphaAnimation mAlphaAnimation = new AlphaAnimation(1, .2f);
		mAlphaAnimation.setDuration(400);
		mAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		mAlphaAnimation.setFillAfter(false);

		mAnimationSet=new AnimationSet(false);
		mAnimationSet.setDuration(400);
		mAnimationSet.addAnimation(mScaleAnimation);
		mAnimationSet.addAnimation(mAlphaAnimation);
		mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						dismiss();
					}
				}, 150);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.digBtn:
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(mActionItems.get(0), 0);
			}
			mLikeAnimaView.clearAnimation();
			mLikeAnimaView.startAnimation(mAnimationSet);
			break;
		case R.id.commentBtn:
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(mActionItems.get(1), 1);
			}
			dismiss();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 添加子类项
	 */
	public void addAction(SnsActionItem action) {
		if (action != null) {
			mActionItems.add(action);
		}
	}
	
	/**
	 * 功能描述：弹窗子类项按钮监听事件
	 */
	public interface OnItemClickListener {
		void onItemClick(SnsActionItem item, int position);
	}
}
