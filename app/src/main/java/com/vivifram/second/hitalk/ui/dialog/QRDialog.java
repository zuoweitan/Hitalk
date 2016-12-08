package com.vivifram.second.hitalk.ui.dialog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.AvatarView;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-8 下午6:40
 * 修改人：zuowei
 * 修改时间：16-12-8 下午6:40
 * 修改备注：
 */
public class QRDialog extends BaseBlurDialog{

    private AvatarView avatarImv;
    private TextView nickTv;
    private TextView collegeTv;
    private ImageView qrImv;

    @Override
    protected View createContentView(Activity activity) {
        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_qr_layout,null);

        float[] outerR = new float[] { 8, 8, 8, 8, 8, 8, 8, 8 };
        RectF inset = new RectF(0, 0, 0, 0);
        float[] innerRadii = new float[] { 8, 8, 8, 8, 8, 8, 8, 8 };
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(outerR,inset,innerRadii));
        shapeDrawable.getPaint().setColor(activity.getResources().getColor(R.color.colorWhite));
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        v.setBackground(shapeDrawable);

        return v;
    }

    public QRDialog setAvatar(String url){
        if (avatarImv == null){
            avatarImv = (AvatarView) getDialog().findViewById(R.id.avatarImv);
        }
        Glide.with(getActivity()).load(url).placeholder(R.drawable.default_avatar).into(avatarImv);
        return this;
    }

    public QRDialog setNick(String nick){
        if (nickTv == null){
            nickTv = (TextView) getDialog().findViewById(R.id.nickTv);
        }
        nickTv.setText(nick);
        return this;
    }

    public QRDialog setCollege(String college){
        if (collegeTv == null){
            collegeTv = (TextView) getDialog().findViewById(R.id.collegeTv);
        }
        collegeTv.setText(college);
        return this;
    }

    public QRDialog setQR(Bitmap bitmap){
        if (qrImv == null) {
            qrImv = (ImageView) getDialog().findViewById(R.id.qrImv);
        }
        qrImv.setImageBitmap(bitmap);
        return this;
    }
}
