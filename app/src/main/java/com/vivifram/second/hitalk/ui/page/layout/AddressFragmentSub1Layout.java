package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.CommonItem;

/**
 * Created by zuowei on 16-10-11.
 */

public class AddressFragmentSub1Layout extends BaseFragmentLayout {

    public AddressFragmentSub1Layout(View root) {
        super(root);
    }

    private CommonItem conditionSearchCi;
    private CommonItem nearByCi;

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
