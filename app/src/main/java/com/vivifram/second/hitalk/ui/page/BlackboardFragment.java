package com.vivifram.second.hitalk.ui.page;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.vivifram.second.hitalk.ui.BnPublishActivity;
import com.vivifram.second.hitalk.ui.page.layout.BlackboardFragmentLayout;
import com.vivifram.second.hitalk.ui.recycleview.blackboard.CommentConfig;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.StaticDataCacheHelper;
import com.zuowei.utils.provider.BnDataProvider;

import java.util.List;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "BlackboardFragmentLayout")
public class BlackboardFragment extends LazyFragment<BlackboardFragmentLayout>
        implements BlackboardFragmentLayout.BnItemOnClickListener
        ,BlackboardFragmentLayout.IBlackboardItemsRefreshListener
        ,BlackboardFragmentLayout.OnTitleClickListener{

    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case BlackboardFragmentLayout.TYPE_PULLREFRESH:
                    case BlackboardFragmentLayout.TYPE_LOADREFRESH:
                            loadData(msg.what);
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_blackboard_layout;
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        mLayout.setBnItemOnClickListener(this);
        mLayout.setBcItemsRefreshListener(this);
        mLayout.setmOnTitleClickListener(this);
        loadData(BlackboardFragmentLayout.TYPE_PULLREFRESH);
    }

    @Override
    public void deleteBn(String bnId) {
        //request from server
        if (mLayout != null) {
            mLayout.update2DeleteBn(bnId);
        }
    }

    @Override
    public void addFavort(int bnPosition) {
        if (mLayout != null) {
            mLayout.update2AddFavorite(bnPosition,
                    BnDataProvider.createCurUserFavortItem(mLayout.getItemRelative(bnPosition).getId()));
        }
    }

    @Override
    public void deleteFavort(int bnPosition, String favortId) {
        if (mLayout != null) {
            BnItem bnItem = mLayout.getItemRelative(bnPosition);
            if (bnItem != null){
                BnDataProvider.deleteFavortItem(bnItem.getFavortItemById(favortId));
                mLayout.update2DeleteFavort(bnPosition,favortId);
            }
        }
    }

    @Override
    public void deleteComment(int bnPosition, String commentId) {
        if (mLayout != null) {
            BnItem bnItem = mLayout.getItemRelative(bnPosition);
            if (bnItem != null){
                BnDataProvider.deleteComment(bnItem.getCommentItemById(commentId));
                mLayout.update2DeleteComment(bnPosition,commentId);
            }

        }
    }

    public void addComment(final String content, final CommentConfig config){
        if(config == null){
            return;
        }
        CommentItem newItem = null;
        if (config.mCommentType == CommentConfig.Type.PUBLIC) {
            newItem = BnDataProvider.createPublicComment(content,
                    mLayout.getItemRelative(config.mBnPosition).getId());
        } else if (config.mCommentType == CommentConfig.Type.REPLY) {
            newItem = BnDataProvider.createReplyComment(config.mReplyUser, content,
                    mLayout.getItemRelative(config.mBnPosition).getId());
        }
        if(mLayout!=null){
            mLayout.update2AddComment(config.mBnPosition, newItem);
        }
    }

    private void refreshData(int type){
        if (mHandler.hasMessages(type)){
            mHandler.removeMessages(type);
        }
        mHandler.sendEmptyMessageDelayed(type,500);
    }

    public void loadData(final int type){
        DoneCallback<BnItem> doneCallback = new DoneCallback<BnItem>() {
            @Override
            public void done(final List<BnItem> list, Exception e) {
                NLog.i(TagUtil.makeTag(BlackboardFragment.class),"loadData doneCallback mLayout = "+mLayout);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mLayout != null) {
                            mLayout.update2loadData(type, list);
                        }
                    }
                });
            }
        };
        switch (type){
            case BlackboardFragmentLayout.TYPE_PULLREFRESH:
                NLog.i(TagUtil.makeTag(BlackboardFragment.class),"loadData TYPE_PULLREFRESH");
                BnDataProvider.refreshBnDatas(doneCallback);
                break;
            case BlackboardFragmentLayout.TYPE_LOADREFRESH:
                BnDataProvider.loadMoreBnDatas(doneCallback,mLayout.getLastBt());
                break;
        }
    }

    @Override
    public void onRefresh() {
        refreshData(BlackboardFragmentLayout.TYPE_PULLREFRESH);
    }

    @Override
    public void onLoadMore() {
        refreshData(BlackboardFragmentLayout.TYPE_LOADREFRESH);
    }

    @Override
    public void onLeftClick() {
        startActivityForResult(new Intent(getActivity(),BnPublishActivity.class),0);
    }

    @Override
    public void onCenterClick() {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == 1){
                String key  = data.getStringExtra("bnItem");
                mLayout.publish(StaticDataCacheHelper.getInstance().getBnItem(key));
            }
        }
    }
}
