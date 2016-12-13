package com.vivifram.second.hitalk.ui.page.layout;

import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;

/**
 * Created by zuowei on 16-8-1.
 */
public class HitalkFragmentLayout extends BaseFragmentLayout {

    public interface  OnTabClickListener{
        void onTabClick(int index);
    }
    private int mCurrentTabIndex;
    private Tab tabOne_;
    private Tab tabTwo_;
    private OnTabClickListener mOnTabClickListener;
    public HitalkFragmentLayout(View root) {
        super(root);
    }

    @Override
    public void onActivitySet() {
        super.onActivitySet();
        mAct.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        createRoom(root);
    }

    private void createRoom(View root) {
        tabOne_ = new Tab(root.findViewById(R.id.tabOne),0);
        tabOne_.setSelected(true);
        tabTwo_ = new Tab(findViewById(R.id.tabTwo),1);
        tabOne_.setTitle(mCtx.getString(R.string.tab_hi));
        tabTwo_.setTitle(mCtx.getString(R.string.topic_conversation_title));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == tabOne_.getTabId()){
                    tabOne_.setSelected(true);
                    tabTwo_.setSelected(false);
                    mCurrentTabIndex = tabOne_.getIndex();
                    if (mOnTabClickListener != null){
                        mOnTabClickListener.onTabClick(tabOne_.getIndex());
                    }
                }else if (id == tabTwo_.getTabId()){
                    tabOne_.setSelected(false);
                    tabTwo_.setSelected(true);
                    mCurrentTabIndex = tabTwo_.getIndex();
                    if (mOnTabClickListener != null){
                        mOnTabClickListener.onTabClick(tabTwo_.getIndex());
                    }
                }
            }
        };

        tabOne_.setTabOnclickListener(onClickListener);
        tabTwo_.setTabOnclickListener(onClickListener);
    }

    public void setTabOneCount(int count){
        tabOne_.setNumber(count);
    }

    public void setTabTwoCount(int count){
        tabTwo_.setNumber(count);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public int getContentId(){
        return R.id.content;
    }

    public int getCurrentTabIndex() {
        return mCurrentTabIndex;
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

    public void setOnTabClickListener(OnTabClickListener mOnTabClickListener) {
        this.mOnTabClickListener = mOnTabClickListener;
    }

    private static class Tab{
        private View mRoot;
        private TextView mTitleTv;
        private TextView mNumberTv;
        private int mIndex;
        public Tab(View v, int index){
            mRoot = v;
            mTitleTv = (TextView) mRoot.findViewById(R.id.titleTv);
            mNumberTv = (TextView) mRoot.findViewById(R.id.numberTv);
            mIndex = index;
        }

        public void setTitle(String title){
            mTitleTv.setText(title);
        }

        public void setSelected(boolean selected){
            mRoot.setSelected(selected);
        }

        public void setNumber(int number){
            mNumberTv.setText(String.format(mRoot.getResources()
                    .getString(R.string.onlineNumber),number));
        }

        public void setTabOnclickListener(View.OnClickListener onclickListener){
            mRoot.setOnClickListener(onclickListener);
        }

        public int getTabId(){
            return mRoot.getId();
        }

        public int getIndex() {
            return mIndex;
        }
    }
}
