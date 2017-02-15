package com.vivifram.second.hitalk.ui.recycleview.blackboard;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.bean.blackboard.FavortItem;
import com.vivifram.second.hitalk.bean.blackboard.SnsActionItem;
import com.vivifram.second.hitalk.ui.page.layout.BlackboardFragmentLayout;
import com.vivifram.second.hitalk.ui.pop.CommentActionPopup;
import com.vivifram.second.hitalk.ui.pop.SnsPopupWindow;
import com.vivifram.second.hitalk.ui.recycleview.BaseRecycleViewAdapter;
import com.vivifram.second.hitalk.ui.view.BnVideoView;
import com.vivifram.second.hitalk.ui.view.CommentListView;
import com.vivifram.second.hitalk.ui.view.ExpandTextView;
import com.vivifram.second.hitalk.ui.view.PraiseListView;
import com.zuowei.utils.common.DateUtils;
import com.zuowei.utils.common.GlideCircleTransform;
import com.zuowei.utils.common.UrlUtils;
import com.zuowei.utils.helper.NineGridHelper;
import com.zuowei.utils.provider.BnDataProvider;
import com.zuowei.utils.videolist.model.VideoLoadMvpView;
import com.zuowei.utils.videolist.widget.TextureVideoView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BNAdapter extends BaseRecycleViewAdapter<BnItem,RecyclerView.ViewHolder> {

    public final static int TYPE_HEAD = 0;
    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;
    public final static int TYPE_TEXT = 4;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ACTIVED = 1;
    private static final int STATE_DEACTIVED = 2;
    private int videoState = STATE_IDLE;
    public static final int HEADVIEW_SIZE = 1;

    int curPlayIndex = -1;

    private BlackboardFragmentLayout.BnItemOnClickListener bnItemOnClickListener;
    private BlackboardFragmentLayout bnHostLayout;
    private Activity context;

    public void setBnItemOnClickListener(BlackboardFragmentLayout.BnItemOnClickListener presenter) {
        this.bnItemOnClickListener = presenter;
    }

    public BNAdapter(Activity context,BlackboardFragmentLayout blackboardFragmentLayout) {
        this.context = context;
        this.bnHostLayout = blackboardFragmentLayout;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }

        int itemType = TYPE_TEXT;
        BnItem item = datas.get(position - 1);
        if (BnItem.TYPE_URL.equals(item.getType())) {
            itemType = TYPE_URL;
        } else if (BnItem.TYPE_IMG.equals(item.getType())) {
            itemType = TYPE_IMAGE;
        } else if (BnItem.TYPE_VIDEO.equals(item.getType())) {
            itemType = TYPE_VIDEO;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == TYPE_HEAD) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bn_head_layout, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bn_item_layout, parent, false);
            viewHolder = new BnItemViewHolder(view, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        } else {

            final int bnPosition = position - HEADVIEW_SIZE;
            final BnItemViewHolder holder = (BnItemViewHolder) viewHolder;
            BnItem bnItem = datas.get(bnPosition);
            final String circleId = bnItem.getId();
            String name = bnItem.getUser().getNick();
            String headImg = bnItem.getUser().getAvatar();
            final String content = bnItem.getContent();
            Date createTime = bnItem.getCreateTime();
            final List<FavortItem> favortDatas = bnItem.getFavorters();
            final List<CommentItem> commentsDatas = bnItem.getComments();
            boolean hasFavort = bnItem.hasFavort();
            boolean hasComment = bnItem.hasComment();

            Glide.with(context).load(headImg)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .transform(new GlideCircleTransform(context))
                    .into(holder.headIv);

            holder.nameTv.setText(name);
            holder.timeTv.setText(DateUtils.getTimestampString(createTime));

            if (!TextUtils.isEmpty(content)) {
                holder.contentTv.setText(UrlUtils.formatUrlString(content));
            }
            holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);

            /*if (BnDataProvider.curUser.getObjectId().equals(bnItem.getUser().getObjectId())) {
                holder.deleteBtn.setVisibility(View.VISIBLE);
            } else {
                holder.deleteBtn.setVisibility(View.GONE);
            }*/
            holder.deleteBtn.setVisibility(View.GONE);
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除
                    if (bnItemOnClickListener != null) {
                        bnItemOnClickListener.deleteBn(circleId);
                    }
                }
            });
            if (hasFavort || hasComment) {
                if (hasFavort) {//处理点赞列表
                    holder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            String userName = favortDatas.get(position).getUser().getNick();
                            String userId = favortDatas.get(position).getUser().getObjectId();
                            Toast.makeText(HiTalkApplication.mAppContext, userName + " &id = " + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.praiseListView.setDatas(favortDatas);
                    holder.praiseListView.setVisibility(View.VISIBLE);
                } else {
                    holder.praiseListView.setVisibility(View.GONE);
                }

                if (hasComment) {//处理评论列表
                    holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(int commentPosition) {
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            if (BnDataProvider.$().curUser.getObjectId().equals(commentItem.getUser().getObjectId())) {//复制或者删除自己的评论
                                int action = isCommentFromMe(commentItem) ? CommentActionPopup.ACTION_BOTH:CommentActionPopup.ACTION_COPY;
                                CommentActionPopup actionPopup = new CommentActionPopup(context,action,bnItemOnClickListener,commentItem,bnPosition);
                                actionPopup.showPopupWindow();
                            } else {//回复别人的评论
                                if (bnItemOnClickListener != null) {
                                    CommentConfig config = new CommentConfig();
                                    config.mBnPosition = bnPosition;
                                    config.mCommentPosition = commentPosition;
                                    config.mCommentType = CommentConfig.Type.REPLY;
                                    config.mReplyUser = commentItem.getUser();
                                    bnHostLayout.updateEditTextBodyVisible(View.VISIBLE, config);
                                }
                            }
                        }
                    });
                    holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int commentPosition) {
                            //长按进行复制或者删除
                            CommentItem commentItem = commentsDatas.get(commentPosition);
                            int action = isCommentFromMe(commentItem) ? CommentActionPopup.ACTION_BOTH:CommentActionPopup.ACTION_COPY;
                            CommentActionPopup actionPopup = new CommentActionPopup(context,action,bnItemOnClickListener,commentItem,bnPosition);
                            actionPopup.showPopupWindow();
                        }
                    });
                    holder.commentList.setDatas(commentsDatas);
                    holder.commentList.setVisibility(View.VISIBLE);

                } else {
                    holder.commentList.setVisibility(View.GONE);
                }
                holder.digCommentBody.setVisibility(View.VISIBLE);
            } else {
                holder.digCommentBody.setVisibility(View.GONE);
            }

            holder.digLine.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

            final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
            //判断是否已点赞
            String curUserFavortId = bnItem.getCurUserFavortId(BnDataProvider.$().curUser.getObjectId());
            if (!TextUtils.isEmpty(curUserFavortId)) {
                snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
            } else {
                snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
            }
            snsPopupWindow.update();
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener(bnPosition, bnItem, curUserFavortId));
            holder.snsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //弹出popupwindow
                    snsPopupWindow.showPopupWindow(view);
                }
            });

            holder.urlTipTv.setVisibility(View.GONE);
            switch (holder.viewType) {
                case TYPE_URL:// 处理链接动态的链接内容和和图片
                    String linkImg = bnItem.getLinkImg();
                    String linkTitle = bnItem.getLinkTitle();
                    Glide.with(context).load(linkImg).into(holder.urlImageIv);
                    holder.urlContentTv.setText(linkTitle);
                    holder.urlBody.setVisibility(View.VISIBLE);
                    holder.urlTipTv.setVisibility(View.VISIBLE);
                    break;
                case TYPE_IMAGE:// 处理图片
                    final HashMap<String,String> photos = bnItem.getPhotos();
                    if (photos != null && photos.size() > 0) {
                        holder.nineGridView.setVisibility(View.VISIBLE);
                        List<ImageInfo> images = new ArrayList<>();
                        if (photos != null) {
                            for (String temp : photos.keySet()){
                                ImageInfo imageInfo = new ImageInfo();
                                imageInfo.setBigImageUrl(temp);
                                imageInfo.setThumbnailUrl(photos.get(temp) == null?
                                        temp : photos.get(temp));
                                images.add(imageInfo);
                            }
                        }
                        NineGridHelper.setList(context,holder.nineGridView,images);
                    } else {
                        holder.nineGridView.setVisibility(View.GONE);
                    }
                    break;
                case TYPE_VIDEO:
                    holder.videoView.setVideoUrl(bnItem.getVideoUrl());
                    holder.videoView.setVideoImgUrl(bnItem.getVideoImgUrl());//视频封面图片
                    holder.videoView.setPostion(position);
                    holder.videoView.setOnPlayClickListener(new BnVideoView.OnPlayClickListener() {
                        @Override
                        public void onPlayClick(int pos) {
                            curPlayIndex = pos;
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private List<String> getPhotos(HashMap<String, String> photos) {
        ArrayList<String> results = new ArrayList<>();
        if (photos != null) {
            for (String photo : photos.keySet()){
                if (photos.get(photo) != null){
                    results.add(photos.get(photo));
                }else {
                    results.add(photo);
                }
            }
        }
        return results;
    }

    private boolean isCommentFromMe(CommentItem commentItem){
        return commentItem != null
                && BnDataProvider.$().curUser.getObjectId().equals(
                commentItem.getUser().getObjectId());
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;//有head需要加1
    }

    @Override
    public BnItem getItem(int position) {
        if (position < 1 || position > getItemCount() - 1){
            return null;
        }
        return datas.get(position - 1);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class BnItemViewHolder extends RecyclerView.ViewHolder implements VideoLoadMvpView {
        public int viewType;

        public ImageView headIv;
        public TextView nameTv;
        public TextView urlTipTv;
        /**
         * 动态的内容
         */
        public ExpandTextView contentTv;
        public TextView timeTv;
        public TextView deleteBtn;
        public ImageView snsBtn;
        /**
         * 点赞列表
         */
        public PraiseListView praiseListView;

        public LinearLayout urlBody;
        public LinearLayout digCommentBody;
        public View digLine;

        /**
         * 评论列表
         */
        public CommentListView commentList;
        /**
         * 链接的图片
         */
        public ImageView urlImageIv;
        /**
         * 链接的标题
         */
        public TextView urlContentTv;
        /**
         * 图片
         */
        public NineGridView nineGridView;

        public BnVideoView videoView;
        // ===========================
        public SnsPopupWindow snsPopupWindow;

        public BnItemViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);
            switch (viewType) {
                case TYPE_URL:// 链接view
                    viewStub.setLayoutResource(R.layout.viewstub_urlbody_layout);
                    viewStub.inflate();
                    LinearLayout urlBodyView = (LinearLayout) itemView.findViewById(R.id.urlBody);
                    if (urlBodyView != null) {
                        urlBody = urlBodyView;
                        urlImageIv = (ImageView) itemView.findViewById(R.id.urlImageIv);
                        urlContentTv = (TextView) itemView.findViewById(R.id.urlContentTv);
                    }
                    break;
                case TYPE_IMAGE:// 图片view
                    viewStub.setLayoutResource(R.layout.viewstub_imgbody_layout);
                    viewStub.inflate();
                    NineGridView nineGridView = (NineGridView) itemView.findViewById(R.id.ngv);
                    if (nineGridView != null) {
                        this.nineGridView = nineGridView;
                    }
                    break;
                case TYPE_VIDEO:
                    viewStub.setLayoutResource(R.layout.viewstub_videobody_layout);
                    viewStub.inflate();

                    BnVideoView videoBody = (BnVideoView) itemView.findViewById(R.id.videoView);
                    if (videoBody != null) {
                        this.videoView = videoBody;
                    }
                    break;
                default:
                    break;
            }
            headIv = (ImageView) itemView.findViewById(R.id.headIv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            digLine = itemView.findViewById(R.id.lin_dig);

            contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
            urlTipTv = (TextView) itemView.findViewById(R.id.urlTipTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            deleteBtn = (TextView) itemView.findViewById(R.id.deleteBtn);
            snsBtn = (ImageView) itemView.findViewById(R.id.snsBtn);
            praiseListView = (PraiseListView) itemView.findViewById(R.id.praiseListView);

            digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);
            commentList = (CommentListView) itemView.findViewById(R.id.commentList);

            snsPopupWindow = new SnsPopupWindow(itemView.getContext());

        }

        @Override
        public TextureVideoView getVideoView() {
            return null;
        }

        @Override
        public void videoBeginning() {

        }

        @Override
        public void videoStopped() {

        }

        @Override
        public void videoPrepared(MediaPlayer player) {

        }

        @Override
        public void videoResourceReady(String videoPath) {

        }
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        private String mFavorId;
        //动态在列表中的位置
        private int mBnPosition;
        private long mLasttime = 0;
        private BnItem mBnItem;

        public PopupItemClickListener(int bnPosition, BnItem bnItem, String favorId) {
            this.mFavorId = favorId;
            this.mBnPosition = bnPosition;
            this.mBnItem = bnItem;
        }

        @Override
        public void onItemClick(SnsActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (bnItemOnClickListener != null) {
                        if ("赞".equals(actionitem.mTitle.toString())) {
                            bnItemOnClickListener.addFavort(mBnPosition);
                        } else {//取消点赞
                            bnItemOnClickListener.deleteFavort(mBnPosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if (bnItemOnClickListener != null) {
                        CommentConfig config = new CommentConfig();
                        config.mBnPosition = mBnPosition;
                        config.mCommentType = CommentConfig.Type.PUBLIC;
                        bnHostLayout.updateEditTextBodyVisible(View.VISIBLE,config);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
