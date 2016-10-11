package com.vivifram.second.hitalk.dialog.blackboard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.ui.page.layout.BlackboardFragmentLayout;
import com.zuowei.utils.provider.BnDataProvider;

public class CommentDialog extends Dialog implements
		View.OnClickListener {

	private Context mContext;
	private BlackboardFragmentLayout.BnItemOnClickListener bnItemOnClickListener;
	private CommentItem mCommentItem;
	private int mCirclePosition;

	public CommentDialog(Context context, BlackboardFragmentLayout.BnItemOnClickListener bnItemOnClickListener,
						 CommentItem commentItem, int circlePosition) {
		super(context, R.style.comment_dialog);
		mContext = context;
		this.bnItemOnClickListener = bnItemOnClickListener;
		this.mCommentItem = commentItem;
		this.mCirclePosition = circlePosition;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_dialog_layout);
		initWindowParams();
		initView();
	}

	private void initWindowParams() {
		Window dialogWindow = getWindow();
		// 获取屏幕宽、高用
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (display.getWidth() * 0.65);

		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
	}

	private void initView() {
		TextView copyTv = (TextView) findViewById(R.id.copyTv);
		copyTv.setOnClickListener(this);
		TextView deleteTv = (TextView) findViewById(R.id.deleteTv);
		if (mCommentItem != null
				&& BnDataProvider.curUser.getObjectId().equals(
						mCommentItem.getUser().getObjectId())) {
			deleteTv.setVisibility(View.VISIBLE);
		} else {
			deleteTv.setVisibility(View.GONE);
		}
		deleteTv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.copyTv:
			if (mCommentItem != null) {
				ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(mCommentItem.getContent());
			}
			dismiss();
			break;
		case R.id.deleteTv:
			if (bnItemOnClickListener != null && mCommentItem != null) {
				bnItemOnClickListener.deleteComment(mCirclePosition, mCommentItem.getId());
			}
			dismiss();
			break;
		default:
			break;
		}
	}

}
