package com.vivifram.second.hitalk.ui.layout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.AddRequest;
import com.vivifram.second.hitalk.ui.recycleview.address.DividerDecoration;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.helper.UserBeanCacheHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bolts.Continuation;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-11-1 下午2:14
 * 修改人：zuowei
 * 修改时间：16-11-1 下午2:14
 * 修改备注：
 */
public class NewFriendConfirmLayout extends BaseLayout {


    public interface OnTitleActionListener{
        void onBack();
    }

    public NewFriendConfirmLayout(View rootView) {
        super(rootView);
    }

    private SpringView nSv;
    private RecyclerView nfRv;
    private BGATitlebar titlebar;


    private OnTitleActionListener onTitleActionListener;
    private NewFriendsAdapter newFriendsAdapter;

    public interface OnAcceptActionListener{
        void onClick(AddRequest addRequest, Continuation<Boolean,Void> continuation);
    }

    private OnAcceptActionListener onAcceptActionListener;

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);

        nSv = (SpringView) findViewById(R.id.nSv);
        nfRv = (RecyclerView) findViewById(R.id.nfRv);

        nfRv.setLayoutManager(new LinearLayoutManager(mAppCtx));
        nfRv.setHasFixedSize(true);
        nfRv.addItemDecoration(new DividerDecoration(mAppCtx));

        newFriendsAdapter = new NewFriendsAdapter();
        nfRv.setAdapter(newFriendsAdapter);

        titlebar = (BGATitlebar) findViewById(R.id.titleBar);
        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                super.onClickLeftCtv();
                if (onTitleActionListener != null) {
                    onTitleActionListener.onBack();
                }
            }
        });

    }

    public NewFriendConfirmLayout setOnAcceptActionListener(OnAcceptActionListener onAcceptActionListener) {
        this.onAcceptActionListener = onAcceptActionListener;
        return this;
    }

    public void setOnFreshListener(SpringView.OnFreshListener onFreshListener){
        nSv.setListener(onFreshListener);
    }

    public void setData(List<AddRequest> data){
        if (data != null && data.size() > 0) {
            newFriendsAdapter.addRequests.clear();
            newFriendsAdapter.addRequests.addAll(data);
            newFriendsAdapter.notifyDataSetChanged();
        }
    }

    public NewFriendConfirmLayout setOnTitleActionListener(OnTitleActionListener onTitleActionListener) {
        this.onTitleActionListener = onTitleActionListener;
        return this;
    }

    public void add(AddRequest... addRequests){
        if (addRequests != null) {
            List<AddRequest> addRequestList = Arrays.asList(addRequests);
            int count = newFriendsAdapter.getItemCount();
            newFriendsAdapter.addRequests.addAll(addRequestList);
            newFriendsAdapter.notifyItemRangeInserted(count - 1,addRequestList.size());
        }
    }

    public void add(List<AddRequest> addRequests){
        if (addRequests != null) {
            int count = newFriendsAdapter.getItemCount();
            newFriendsAdapter.addRequests.addAll(addRequests);
            newFriendsAdapter.notifyItemRangeInserted(count - 1,addRequests.size());
        }
    }


    public int getCurrentSize(){
        return newFriendsAdapter.getItemCount();
    }

    public void notifyFreshDone(){
        nSv.onFinishFreshAndLoad();
    }


    class NewFriendsAdapter extends RecyclerView.Adapter<NewFriendsHolder>{

        private ArrayList<AddRequest> addRequests;

        NewFriendsAdapter(){
            setHasStableIds(true);
            addRequests = new ArrayList<>();
        }

        @Override
        public NewFriendsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NewFriendsHolder(LayoutInflater.from(mAppCtx)
                    .inflate(R.layout.add_request_item,parent,false));
        }

        @Override
        public void onBindViewHolder(NewFriendsHolder holder, int position) {
            holder.initWith(getItem(position));
            holder.bindAcceptAction(v -> {
                if (onAcceptActionListener != null) {
                    onAcceptActionListener.onClick(getItem(position),task -> {
                        Boolean result = task.getResult();
                        holder.changeAcptBtnState(result);
                        return null;
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return addRequests.size();
        }

        public AddRequest getItem(int position){
            return addRequests.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }
    }

    class NewFriendsHolder extends RecyclerView.ViewHolder{
        private TextView nickTv;
        private ImageView iconIv;
        private TextView infoTv;
        private TextView acceptBtn;
        public NewFriendsHolder(View itemView) {
            super(itemView);

            nickTv = (TextView) itemView.findViewById(R.id.nickTv);
            iconIv = (ImageView) itemView.findViewById(R.id.iconIv);
            infoTv = (TextView) itemView.findViewById(R.id.nfInfo);
            acceptBtn = (TextView) itemView.findViewById(R.id.acceptTv);

        }

        public void bindAcceptAction(View.OnClickListener onClickListener){
            acceptBtn.setOnClickListener(onClickListener);
        }

        public void changeAcptBtnState(boolean accept){
            if (accept){
                acceptBtn.setSelected(true);
                acceptBtn.setText(R.string.accepted);
            }else {
                acceptBtn.setSelected(false);
                acceptBtn.setText(R.string.accept);
            }
        }

        public void initWith(AddRequest item) {
            AVUser avUser = item.getFromUser();
            if (avUser != null) {
                User user = UserBeanCacheHelper.AvUserToUser(avUser,new User());
                nickTv.setText(user.getNick());

                Glide.with(mAppCtx).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(iconIv);

                infoTv.setText(mAppCtx.getText(R.string.preIntroduce)+user.getNick());

                int status = item.getStatus();
                if (status == AddRequest.STATUS_WAIT) {
                    acceptBtn.setSelected(false);
                    acceptBtn.setText(R.string.accept);
                } else if (status == AddRequest.STATUS_DONE) {
                    acceptBtn.setSelected(true);
                    acceptBtn.setText(R.string.accepted);
                }
            }
        }
    }
}
