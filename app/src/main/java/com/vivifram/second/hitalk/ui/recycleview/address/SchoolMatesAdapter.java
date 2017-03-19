package com.vivifram.second.hitalk.ui.recycleview.address;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jiang.android.lib.adapter.BaseAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub1Layout;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NoDoubleClickListener;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.SchoolmatesCacheHelper;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by zuowei on 16-10-13.
 */

public class SchoolMatesAdapter extends BaseAdapter<SchoolMate,BaseAdapter.BaseViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0X01;
    public static final int NORMAL = 0x02;
    private static final int HEADSIZE = 1;

    public static final int REQUEST_STATE_SUCCESS = SchoolmatesCacheHelper.REQUEST_STATE_SUCCESS;
    public static final int REQUEST_STATE_WATING = SchoolmatesCacheHelper.REQUEST_STATE_WATING;
    public static final int REQUEST_STATE_FAILED = SchoolmatesCacheHelper.REQUEST_STATE_FAILED;

    public interface OnSchoolMatesActionListener{
        void onAddFriendRequest(String userId, Continuation<Boolean,Void> callback);
    }

    private OnSchoolMatesActionListener onSchoolMatesActionListener;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        }
        return NORMAL;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schoolmateheader, parent, false);
            return new BaseViewHolder(view) {};
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schoolmate_item, parent, false);
        return new SchoolMateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position != 0) {
            NLog.i(TagUtil.makeTag(AddressFragmentSub1Layout.class), "schoolMatesAdapter onBindViewHolder initWithModel");
            ((SchoolMateViewHolder)holder).initWithModel(getItem(position));
        }
    }

    @Override
    public long getHeaderId(int position) {
        if (position == 0){
            return position;
        }
        return getItem(position).getSortLetters().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_stickheader, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        String showValue = String.valueOf(getItem(position).getSortLetters().charAt(0));
        textView.setText(showValue);
    }

    private int getAdjustPosition(int position) {
        return position - HEADSIZE;
    }

    @Override
    public SchoolMate getItem(int position) {
        if (position == 0){
            return null;
        }
        return super.getItem(getAdjustPosition(position));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public boolean isRecyclerViewHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateRecyclerHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_recyclerheader, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindRecyclerHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void notifyDataAddChanged(int position) {
        if (position == -1){
            notifyItemInserted(getItemCount() - 1);
        }else if (position == 0){
            notifyDataSetChanged();
        }else {
            notifyItemInserted(position);
            notifyItemRangeChanged(position,super.getItemCount() - position);
        }
    }

    @Override
    public boolean isRecyclerViewFooter(int position) {
        return false;
    }

    public SchoolMatesAdapter setOnSchoolMatesActionListener(OnSchoolMatesActionListener onSchoolMatesActionListener) {
        this.onSchoolMatesActionListener = onSchoolMatesActionListener;
        return this;
    }

    public class SchoolMateViewHolder extends BaseViewHolder{

        Context ctx;
        TextView nickNameTv;
        TextView infoTv;
        Button addFriendBtn;

        public SchoolMateViewHolder(View itemView) {
            super(itemView);
            ctx = itemView.getContext();
            nickNameTv = (TextView) itemView.findViewById(R.id.nickTv);
            infoTv = (TextView) itemView.findViewById(R.id.sInfo);

            addFriendBtn = (Button) itemView.findViewById(R.id.addFriendBt);

            setOnAddFriendCalledListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    SchoolMate item = getItem(getAdapterPosition());
                    if (onSchoolMatesActionListener != null) {
                        onSchoolMatesActionListener.onAddFriendRequest(item.getUserId(), task -> {
                            Boolean result = task.getResult();
                            if (result){
                                SchoolmatesCacheHelper.getInstance().cache(item.getUserId(),SchoolmatesCacheHelper.REQUEST_STATE_WATING);
                                setRequestState(REQUEST_STATE_WATING);
                            }else {
                                SchoolmatesCacheHelper.getInstance().cache(item.getUserId(),SchoolmatesCacheHelper.REQUEST_STATE_FAILED);
                                setRequestState(REQUEST_STATE_FAILED);
                            }
                            return null;
                        });
                    }
                }
            });
        }

        public void initWithModel(SchoolMate schoolMate){
            nickNameTv.setText(schoolMate.getNickName());
            infoTv.setText(schoolMate.getsInfo());
            initFriendState(schoolMate.getUserId());
        }

        private void initFriendState(String userId) {
            SchoolmatesCacheHelper.getInstance().getSchoolmateFriendState(userId)
                    .continueWith(task -> {
                        setRequestState(task.getResult());
                       return null;
                    }, Task.UI_THREAD_EXECUTOR);
        }

        public void setRequestState(int success){
            switch (success){
                case REQUEST_STATE_FAILED:
                        addFriendBtn.setVisibility(View.VISIBLE);
                        addFriendBtn.setText(ctx.getString(R.string.add_friend));
                    break;
                case REQUEST_STATE_SUCCESS:
                        addFriendBtn.setVisibility(View.VISIBLE);
                        addFriendBtn.setText(ctx.getString(R.string.add_already));
                    break;
                case REQUEST_STATE_WATING:
                        addFriendBtn.setVisibility(View.VISIBLE);
                        addFriendBtn.setText(ctx.getString(R.string.wait_for_verfiy));
                    break;
            }
        }

        private void setOnAddFriendCalledListener(NoDoubleClickListener onAddFriendCalledListener){
            addFriendBtn.setOnClickListener(onAddFriendCalledListener);
        }
    }
}
