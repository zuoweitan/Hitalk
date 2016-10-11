package com.vivifram.second.hitalk.ui.layout.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by zuowei on 16-8-15.
 */
public class BnImagesAdapter extends RecyclerView.Adapter<BnImagesAdapter.ImageViewHolder>{

    public interface OnImageItemOnClickListener{
        void onMore();
        void onItemClick(int position);
    }

    private ArrayList<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;
    private OnImageItemOnClickListener mOnImageItemOnClickListener;

    private Context mContext;


    public BnImagesAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void setPhotoPaths(ArrayList<String> photoPaths){
        this.photoPaths.clear();
        this.photoPaths.addAll(photoPaths);
        this.photoPaths.add("addImage");
        notifyDataSetChanged();
    }

    public void setOnImageItemOnClickListener(OnImageItemOnClickListener mOnImageItemOnClickListener) {
        this.mOnImageItemOnClickListener = mOnImageItemOnClickListener;
    }

    @Override public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(me.iwf.photopicker.R.layout.__picker_item_photo, parent, false);
        return new ImageViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == getItemCount() -1){
                    if (mOnImageItemOnClickListener != null) {
                        mOnImageItemOnClickListener.onMore();
                    }
                }else {
                    if (mOnImageItemOnClickListener != null) {
                        mOnImageItemOnClickListener.onItemClick(position);
                    }
                }
            }
        });

        if (position == getItemCount() - 1) {
            holder.ivPhoto.setImageResource(R.drawable.image_add);
        }else {
            Uri uri = Uri.fromFile(new File(photoPaths.get(position)));

            Glide.with(mContext)
                    .load(uri)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(me.iwf.photopicker.R.drawable.__picker_ic_photo_black_48dp)
                    .error(me.iwf.photopicker.R.drawable.__picker_ic_broken_image_black_48dp)
                    .into(holder.ivPhoto);
        }
    }


    @Override public int getItemCount() {
        return photoPaths.size();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;
        public ImageViewHolder(View itemView) {
            super(itemView);
            ivPhoto   = (ImageView) itemView.findViewById(me.iwf.photopicker.R.id.iv_photo);
            vSelected = itemView.findViewById(me.iwf.photopicker.R.id.v_selected);
            vSelected.setVisibility(View.GONE);
        }

        public void setOnClickListener(View.OnClickListener onClickListener){
            if (ivPhoto != null) {
                ivPhoto.setOnClickListener(onClickListener);
            }
        }
    }

}
