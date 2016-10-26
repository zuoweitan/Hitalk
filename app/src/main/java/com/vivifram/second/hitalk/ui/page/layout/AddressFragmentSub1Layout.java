package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.ui.recycleview.address.DividerDecoration;
import com.vivifram.second.hitalk.ui.recycleview.address.SchoolMatesAdapter;
import com.vivifram.second.hitalk.ui.view.CommonItem;
import com.vivifram.second.hitalk.ui.view.SRecyclerView;
import com.zuowei.utils.pinyin.CharacterParser;

import java.util.Collection;

/**
 * Created by zuowei on 16-10-11.
 */

public class AddressFragmentSub1Layout extends BaseFragmentLayout {

    public AddressFragmentSub1Layout(View root) {
        super(root);
    }

    private CommonItem conditionSearchCi;
    private CommonItem nearByCi;

    private SRecyclerView recyclerView;
    private SchoolMatesAdapter schoolMatesAdapter;
    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        init();
    }

    private void init() {

        conditionSearchCi = (CommonItem) findViewById(R.id.conditionSearch);
        fillCommonItem(conditionSearchCi,R.drawable.condif,mRes.getString(R.string.conditionSearch));
        conditionSearchCi.showDivider(true);
        nearByCi = (CommonItem) findViewById(R.id.nearbyp);
        fillCommonItem(nearByCi,R.drawable.nearby,mRes.getString(R.string.nearbyp));
        nearByCi.showDivider(false);


        recyclerView = (SRecyclerView) findViewById(R.id.schoolmateList);
        recyclerView.setHasFixedSize(true);

        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mCtx, orientation, false);
        recyclerView.setLayoutManager(layoutManager);

        schoolMatesAdapter = new SchoolMatesAdapter();
        recyclerView.setAdapter(schoolMatesAdapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(schoolMatesAdapter);
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.addItemDecoration(new DividerDecoration(mCtx));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                animateCommonItems(dy);
            }
        });
    }

    private void animateCommonItems(int dy) {
        float offset = recyclerView.scrollVerticalOffset();
        View headerView = getHeaderView();
        int maxOffset = 2 * conditionSearchCi.getHeight();
        if (offset >= 0 && offset <= maxOffset){
            if (headerView == null) {
                conditionSearchCi.setTranslationY(-maxOffset);
                nearByCi.setTranslationY(-maxOffset);
            }else {
                conditionSearchCi.setTranslationY(-offset);
                nearByCi.setTranslationY(-offset);
            }
        }
    }

    private View getHeaderView(){
        if (recyclerView.getChildCount() > 0){
            View child = recyclerView.getChildAt(0);
            int position = recyclerView.getChildAdapterPosition(child);
            if (schoolMatesAdapter.isRecyclerViewHeader(position)){
                return child;
            }
            return null;
        }
        return null;
    }


    public void setData(Collection<SchoolMate> schoolMates){
        schoolMatesAdapter.addAll(schoolMates);
    }

    private void fillCommonItem(CommonItem commonItem, int resId, String title){
        commonItem.setBackgroundColor(mRes.getColor(R.color.colorWhite));
        commonItem.setType(CommonItem.Type.SummaryImgSummaryTxt_DetailImg);
        Drawable drawable = mRes.getDrawable(resId);
        commonItem.setSummaryImg(drawable,54,54);
        commonItem.setSummaryText(title);
        commonItem.setDetailImg(R.drawable.detail_arrow);
    }
}
