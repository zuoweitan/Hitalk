package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.page.layout.adapter.FriendSearchSuggestionAdapter;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-12-21 下午3:21
 * 修改人：zuowei
 * 修改时间：16-12-21 下午3:21
 * 修改备注：
 */
public class SearchFriendLayout extends BaseFragmentLayout {

    public SearchFriendLayout(View root) {
        super(root);
    }

    private FloatingSearchView searchView;
    private FriendSearchSuggestionAdapter suggestionAdapter;

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);

        searchView = (FloatingSearchView) findViewById(R.id.fSearch);
        searchView.setAdapter(suggestionAdapter = new FriendSearchSuggestionAdapter());
        setSearchNavigationIcon();
        searchView.showIcon(shouldShowNavigationIcon());
    }

    private void setSearchNavigationIcon() {
        Drawable drawable = new SearchArrowDrawable(mAppCtx);
        drawable = ViewUtils.getTinted(drawable,mRes.getColor(R.color.hitalk_deep_yellow));
        searchView.setIcon(drawable);
    }

    private boolean shouldShowNavigationIcon() {
        return searchView.getMenu().findItem(R.id.menu_toggle_icon).isChecked();
    }
}
