package com.vivifram.second.hitalk.ui.layout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.wheelview.adapter.BaseWheelAdapter;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-7-27.
 */
public class CollegeAdapter extends BaseWheelAdapter<String> {

    private Context mContext;
    private ListView mHost;

    public CollegeAdapter(Context context,ListView host) {
        mContext = context;
        mHost = host;
    }

    @Override
    protected View bindView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wheel_item, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.itemNameTv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mList.get(position));
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHost.getOnItemClickListener().onItemClick(mHost,v,position,getItemId(position));
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
