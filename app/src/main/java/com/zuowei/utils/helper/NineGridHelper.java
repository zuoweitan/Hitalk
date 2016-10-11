package com.zuowei.utils.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.vivifram.second.hitalk.R;

import java.util.List;

/**
 * Created by zuowei on 16-8-12.
 */
public class NineGridHelper {
    private NineGridHelper(){}

    public static void initWithGlide(){
        NineGridView.setImageLoader(new GlideImageLoader());
    }

    private static class GlideImageLoader implements NineGridView.ImageLoader {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.default_image)//
                    .error(R.drawable.default_image)//
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    public static void setList(Context context, NineGridView nineGridView, List<ImageInfo> images){
        nineGridView.setAdapter(new NineGridViewClickAdapter(context, images));
    }
}
