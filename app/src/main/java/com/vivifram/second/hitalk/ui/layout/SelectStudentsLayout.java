package com.vivifram.second.hitalk.ui.layout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.ui.recycleview.address.DividerDecoration;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.vivifram.second.hitalk.ui.view.slidebar.IndexAdapter;
import com.vivifram.second.hitalk.ui.view.slidebar.Indexable;
import com.vivifram.second.hitalk.ui.view.slidebar.ZSideBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuowei on 16-10-23.
 */

public class SelectStudentsLayout extends BaseLayout {

    public interface OnTitleActionListener{
        void onBack();
        void onConfirm();
    }

    public SelectStudentsLayout(View rootView) {
        super(rootView);
    }

    private RecyclerView studentsRv;
    private SelectStudentsAdapter selectStudentsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private BGATitlebar bgaTitlebar;
    private ZSideBar zSideBar;

    private List<SchoolMate> schoolMates;

    OnTitleActionListener onTitleActionListener;

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        zSideBar = (ZSideBar) findViewById(R.id.zsidebar);
        studentsRv = (RecyclerView) findViewById(R.id.studentsRv);

        selectStudentsAdapter = new SelectStudentsAdapter();

        linearLayoutManager = new LinearLayoutManager(mAppCtx);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        studentsRv.setLayoutManager(linearLayoutManager);
        studentsRv.setHasFixedSize(true);

        studentsRv.setAdapter(selectStudentsAdapter);
        studentsRv.addItemDecoration(new DividerDecoration(mAppCtx));
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(selectStudentsAdapter);
        studentsRv.addItemDecoration(headersDecor);

        schoolMates = new ArrayList<>();

        zSideBar.setupWithRecycler(studentsRv);

        bgaTitlebar = (BGATitlebar) findViewById(R.id.titleBar);
        setTitleDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickLeftCtv() {
                super.onClickLeftCtv();
                if (onTitleActionListener != null) {
                    onTitleActionListener.onBack();
                }
            }
        });
    }

    public SelectStudentsLayout setOnTitleActionListener(OnTitleActionListener onTitleActionListener) {
        this.onTitleActionListener = onTitleActionListener;
        return this;
    }

    private void setTitleDelegate(BGATitlebar.BGATitlebarDelegate delegate){
        bgaTitlebar.setDelegate(delegate);
    }

    public void addSchoolMate(SchoolMate schoolMate){
        schoolMates.add(schoolMate);
        selectStudentsAdapter.notifyItemInserted(schoolMates.size() - 1);
    }

    public void addSchoolMates(List<SchoolMate> schoolMates){
        this.schoolMates.addAll(schoolMates);
        selectStudentsAdapter.notifyDataSetChanged();
    }


    class SelectStudentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements StickyRecyclerHeadersAdapter,IndexAdapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SelectStudentViewHolder(LayoutInflater.from(mAppCtx).inflate(R.layout.student_select_item_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SelectStudentViewHolder){
                SelectStudentViewHolder selectStudentViewHolder = (SelectStudentViewHolder) holder;
                selectStudentViewHolder.initWith(getData(position));
            }
        }

        @Override
        public long getHeaderId(int position) {
            return getData(position).getSortLetters().charAt(0);
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
            String showValue = String.valueOf(getData(position).getSortLetters().charAt(0));
            textView.setText(showValue);
        }

        @Override
        public int getItemCount() {
            return schoolMates.size();
        }

        @Override
        public boolean isRecyclerViewHeader(int position) {
            return false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateRecyclerHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public void onBindRecyclerHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public boolean isRecyclerViewFooter(int position) {
            return false;
        }

        public SchoolMate getData(int position){
            return schoolMates.get(position);
        }

        @Override
        public Indexable getItem(int position) {
            return getData(position);
        }
    }

    class SelectStudentViewHolder extends RecyclerView.ViewHolder{

        private TextView nickTv;
        public SelectStudentViewHolder(View itemView) {
            super(itemView);
            nickTv = (TextView) itemView.findViewById(R.id.nickTv);
        }

        public void initWith(SchoolMate schoolMate){
            nickTv.setText(schoolMate.getNickName());
        }
    }
}
