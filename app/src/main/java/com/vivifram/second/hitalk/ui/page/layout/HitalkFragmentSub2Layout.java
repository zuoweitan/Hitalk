package com.vivifram.second.hitalk.ui.page.layout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.Topic.TopicModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by zuowei on 16-8-6.
 */
public class HitalkFragmentSub2Layout extends BaseFragmentLayout{

    public interface OnTopicItemClickListener{
        void onTopicItemClick(int position);
    }

    public HitalkFragmentSub2Layout(View root) {
        super(root);
    }

    ListView topicLv;
    TopicAdapter topicAdapter;
    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);

        topicLv = (ListView) findViewById(R.id.topicList);
        topicAdapter = new TopicAdapter();

        topicLv.setAdapter(topicAdapter);

        mockData();
    }

    private void mockData() {

        TopicModel topicModel = new TopicModel().setTopicTitle(mRes.getString(R.string.chinajoy))
                .setTopicDetail(mRes.getString(R.string.chinajoyvr));

        topicAdapter.addTopic(topicModel);
        topicAdapter.addTopic(topicModel);
    }


    class TopicAdapter extends BaseAdapter{

        ArrayList<TopicModel> topicModels;

        public TopicAdapter(){
         topicModels = new ArrayList<>();
        }

        public void addTopic(TopicModel topicModel){
            topicModels.add(topicModel);
            notifyDataSetChanged();
        }

        public void addTopics(Collection<TopicModel> topicModels){
            topicModels.addAll(topicModels);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return topicModels.size();
        }

        @Override
        public Object getItem(int position) {
            return topicModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = null;
            TopicHolder topicHolder;
            if (convertView == null) {
                v = LayoutInflater.from(mCtx).inflate(R.layout.hi_topic_item_layout,null);
                topicHolder = new TopicHolder(v);
                v.setTag(topicHolder);

            }else {
                v = convertView;
                topicHolder = (TopicHolder) v.getTag();
            }

            topicHolder.initWith((TopicModel) getItem(position));

            return v;
        }
    }

    class TopicHolder {
        View root;
        ImageView topicIconIv;
        TextView topicTitleTv;
        TextView topicDetailTv;
        public TopicHolder(View root){
            this.root = root;
            topicIconIv = (ImageView) root.findViewById(R.id.topicIconIv);
            topicTitleTv = (TextView) root.findViewById(R.id.topicTitleTv);
            topicDetailTv = (TextView) root.findViewById(R.id.topicDetailTv);
        }

        public void initWith(TopicModel topicModel){
            topicTitleTv.setText(topicModel.getTopicTitle());
            topicDetailTv.setText(topicModel.getTopicDetail());
        }
    }
}
