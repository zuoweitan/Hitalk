package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.bean.blackboard.FavortItem;
import com.vivifram.second.hitalk.ui.recycleview.blackboard.BNAdapter;
import com.vivifram.second.hitalk.ui.recycleview.blackboard.CommentConfig;
import com.vivifram.second.hitalk.ui.recycleview.blackboard.DivItemDecoration;
import com.vivifram.second.hitalk.ui.springview.container.BlackboardRotationFooter;
import com.vivifram.second.hitalk.ui.springview.container.BlackboardRotationHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.vivifram.second.hitalk.ui.view.CommentListView;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.Utils;

import java.util.List;

/**
 * Created by zuowei on 16-8-11.
 */
public class BlackboardFragmentLayout extends BaseFragmentLayout
        implements SpringView.OnFreshListener{
    public final static int TYPE_PULLREFRESH = 1;
    public final static int TYPE_LOADREFRESH = 2;

    public interface OnTitleClickListener{
        void onLeftClick();
        void onCenterClick();
        void onRightClick();
    }

    public interface IBlackboardItemsRefreshListener{
        void onRefresh();
        void onLoadMore();
    }

    public interface BnItemOnClickListener {
        void deleteBn(final String bnId);

        void addFavort(final int circlePosition);

        void deleteFavort(final int circlePosition, final String favortId);

        void deleteComment(final int circlePosition, final String commentId);

        void addComment(final String content, final CommentConfig config);
    }
    private SpringView mSv;
    private RecyclerView mBlackboardItemRv;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout mCommentLayout;
    private EditText mCet;
    private CommentConfig mCommentConfig;
    private int mSelectBnItemH;
    private int mSelectCommentItemOffset;
    private int mCurrentKeyboardH;
    private int mScreenHeight;
    private int mEditTextBodyHeight;
    private RelativeLayout mBodyLayout;
    private BGATitlebar mTitleBar;
    private BNAdapter mBnAdapter;
    private ImageView mSendIv;
    private IBlackboardItemsRefreshListener mBcItemsRefreshListener;
    private BnItemOnClickListener mBnItemOnClickListener;
    private OnTitleClickListener mOnTitleClickListener;
    public BlackboardFragmentLayout(View root) {
        super(root);
    }

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        init();
    }

    private void init() {
        mTitleBar = (BGATitlebar) findViewById(R.id.titleBar);
        mTitleBar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onLeftClick();
                }
            }

            @Override
            public void onClickTitleCtv() {
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onCenterClick();
                }
            }

            @Override
            public void onClickRightCtv() {
                if (mOnTitleClickListener != null) {
                    mOnTitleClickListener.onRightClick();
                }
            }
        });
        mSv = (SpringView) findViewById(R.id.bbSv);
        mSv.setListener(this);
        mSv.setHeader(new BlackboardRotationHeader(mCtx,false));
        mSv.setFooter(new BlackboardRotationFooter(mCtx,false));
        mSv.setGive(SpringView.Give.BOTH);
        mSv.setType(SpringView.Type.FOLLOW);

        mBlackboardItemRv = (RecyclerView) findViewById(R.id.blackBoardRv);
        mLayoutManager = new LinearLayoutManager(mCtx);
        mBlackboardItemRv.setLayoutManager(mLayoutManager);
        mBlackboardItemRv.addItemDecoration(new DivItemDecoration(2, true));
        mBlackboardItemRv.setHasFixedSize(true);
        mBnAdapter = new BNAdapter(mAct,this);
        mBlackboardItemRv.setAdapter(mBnAdapter);
        mCommentLayout = (LinearLayout) findViewById(R.id.editCommentLayout);
        mCet = (EditText) mCommentLayout.findViewById(R.id.bcEt);
        mSendIv = (ImageView) mCommentLayout.findViewById(R.id.sendIv);
        mBlackboardItemRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCommentLayout.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });

        /*mBlackboardItemRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState != RecyclerView.SCROLL_STATE_IDLE){
                    Glide.with(mCtx).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Glide.with(mCtx).resumeRequests();
            }
        });*/

        mSendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBnItemOnClickListener != null) {
                    String content =  mCet.getText().toString().trim();
                    if(TextUtils.isEmpty(content)){
                        NToast.shortToast(mAppCtx,mAppCtx.getString(R.string.commentNull));
                        return;
                    }
                    mBnItemOnClickListener.addComment(content, mCommentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });

        setViewTreeObserver();
    }

    private void setViewTreeObserver() {
        mBodyLayout = (RelativeLayout) findViewById(R.id.contentLayout);
        final ViewTreeObserver swipeRefreshLayoutVTO = mBodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                mBodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH =  getStatusBarHeight();
                int screenH = mBodyLayout.getRootView().getHeight();
                if(r.top != statusBarH ){
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);

                if(keyboardH == mCurrentKeyboardH){
                    return;
                }

                mCurrentKeyboardH = keyboardH;
                mScreenHeight = screenH;
                mEditTextBodyHeight = mCet.getHeight();

                if(keyboardH<150){
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                if(mLayoutManager!=null && mCommentConfig != null){
                    mLayoutManager.scrollToPositionWithOffset(mCommentConfig.mBnPosition + BNAdapter.HEADVIEW_SIZE, getRecycleViewOffset(mCommentConfig));
                }
            }
        });
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mAppCtx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mAppCtx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getRecycleViewOffset(CommentConfig commentConfig) {
        if(commentConfig == null)
            return 0;
        int rcOffset = mScreenHeight - mSelectBnItemH - mCurrentKeyboardH - mEditTextBodyHeight - mTitleBar.getHeight();
        if(commentConfig.mCommentType == CommentConfig.Type.REPLY){
            rcOffset = rcOffset + mSelectCommentItemOffset;
        }
        return rcOffset;
    }

    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        mCommentConfig = commentConfig;
        mCommentLayout.setVisibility(visibility);

        measureBNItemHighAndCommentItemOffset(commentConfig);

        if(View.VISIBLE==visibility){
            mCet.requestFocus();
            Utils.showSoftInput( mCet.getContext(),  mCet);

        }else if(View.GONE==visibility){
            Utils.hideSoftInput( mCet.getContext(),  mCet);
        }
    }

    private void measureBNItemHighAndCommentItemOffset(CommentConfig commentConfig){
        if(commentConfig == null)
            return;

        int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        View selectCircleItem = mLayoutManager.getChildAt(commentConfig.mBnPosition + BNAdapter.HEADVIEW_SIZE - firstPosition);

        if(selectCircleItem != null){
            mSelectBnItemH = selectCircleItem.getHeight();
        }

        if(commentConfig.mCommentType == CommentConfig.Type.REPLY){
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if(commentLv!=null){
                View selectCommentItem = commentLv.getChildAt(commentConfig.mCommentPosition);
                if(selectCommentItem != null){
                    mSelectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if(parentView != null){
                            mSelectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    public void setBnItemOnClickListener(BnItemOnClickListener bnItemOnClickListener){
        mBnItemOnClickListener = bnItemOnClickListener;
        if (mBnAdapter != null) {
            mBnAdapter.setBnItemOnClickListener(bnItemOnClickListener);
        }
    }

    public void setBcItemsRefreshListener(IBlackboardItemsRefreshListener mBcItemsRefreshListener) {
        this.mBcItemsRefreshListener = mBcItemsRefreshListener;
    }

    public void setmOnTitleClickListener(OnTitleClickListener onTitleClickListener){
        mOnTitleClickListener = onTitleClickListener;
    }

    public void update2DeleteBn(String bnId) {
        if (bnId != null) {
            List<BnItem> bnItems = mBnAdapter.getDatas();
            for(int i=0; i<bnItems.size(); i++){
                if(bnId.equals(bnItems.get(i).getId())){
                    bnItems.remove(i);
                    mBnAdapter.notifyItemRemoved(i + 1);
                    mBnAdapter.notifyItemRangeChanged(i + 1,mBnAdapter.getItemCount() - i - 1);
                    return;
                }
            }
        }
    }

    public void update2AddFavorite(int bnPosition, FavortItem addItem) {
        if(addItem != null){
            BnItem item = mBnAdapter.getDatas().get(bnPosition);
            item.getFavorters().add(addItem);
            item.setHasFavort(true);
            mBnAdapter.notifyItemChanged(bnPosition + 1);
        }
    }

    public void update2DeleteFavort(int bnPosition, String favortId) {
        BnItem item = mBnAdapter.getDatas().get(bnPosition);
        List<FavortItem> items = item.getFavorters();
        for(int i=0; i<items.size(); i++){
            if(favortId.equals(items.get(i).getId())){
                items.remove(i);
                mBnAdapter.notifyItemChanged(bnPosition + 1);
                return;
            }
        }
        if (items.size() == 0){
            item.setHasFavort(false);
        }
    }

    public void update2AddComment(int bnPosition, CommentItem addItem) {
        if(addItem != null){
            BnItem item = mBnAdapter.getDatas().get(bnPosition);
            item.getComments().add(addItem);
            item.setHasComment(true);
            NLog.i(TagUtil.makeTag(getClass()),"update2AddComment bnPosition = "+bnPosition+",addItem = "+addItem);
            mBnAdapter.notifyItemChanged(bnPosition + 1);
        }
        mCet.setText("");
    }

    public void update2DeleteComment(int bnPosition, String commentId) {
        BnItem item = mBnAdapter.getDatas().get(bnPosition);
        List<CommentItem> items = item.getComments();
        for(int i=0; i<items.size(); i++){
            if(commentId.equals(items.get(i).getId())){
                items.remove(i);
                mBnAdapter.notifyItemChanged(bnPosition + 1);
                return;
            }
        }
        if (items.size() == 0){
            item.setHasComment(false);
        }
    }

    public void update2loadData(int loadType, List<BnItem> datas) {
        if (loadType == TYPE_PULLREFRESH){
            mBnAdapter.setDatas(datas);
        }else if(loadType == TYPE_LOADREFRESH){
            mBnAdapter.getDatas().addAll(datas);
        }
        mBnAdapter.notifyDataSetChanged();
        NLog.i(TagUtil.makeTag(getClass()),"update2loadData finish");
        finishFresh();
    }

    public void publish(BnItem bnItem) {
        NLog.i(TagUtil.makeTag(BlackboardFragmentLayout.class),"publish bnItem = "+bnItem);
        if (bnItem != null){
            mBnAdapter.getDatas().add(0,bnItem);
            mBnAdapter.notifyDataSetChanged();
        }
    }

    public void finishFresh(){
        if (mSv != null) {
            mSv.onFinishFreshAndLoad();
        }
    }

    @Override
    public void onRefresh() {
        if (mBcItemsRefreshListener != null) {
            mBcItemsRefreshListener.onRefresh();
        }else {
            mSv.onFinishFreshAndLoad();
        }
    }

    @Override
    public void onLoadmore() {
        if (mBcItemsRefreshListener != null) {
            mBcItemsRefreshListener.onLoadMore();
        }else {
            mSv.onFinishFreshAndLoad();
        }
    }

    public BnItem getLastBt(){
        if (mBnAdapter != null){
            return mBnAdapter.getItem(mBnAdapter.getItemCount() - 1);
        }
        return null;
    }

    public BnItem getItem(int position){
        if (mBnAdapter != null){
            return mBnAdapter.getItem(position);
        }
        return null;
    }

    public BnItem getItemRelative(int bnPosition){
        if (mBnAdapter != null){
            return mBnAdapter.getItem(bnPosition + 1);
        }
        return null;
    }
}
