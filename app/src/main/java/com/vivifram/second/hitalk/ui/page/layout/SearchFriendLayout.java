package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.mypopsy.drawable.SearchArrowDrawable;
import com.mypopsy.widget.FloatingSearchView;
import com.mypopsy.widget.internal.ViewUtils;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.page.layout.adapter.FriendSearchSuggestionAdapter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultFooter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;

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

    public interface OnSearchActionChanged{
        void onTextChanged(String text);
    }

    public SearchFriendLayout(View root) {
        super(root);
    }

    private FloatingSearchView searchView;
    private FriendSearchSuggestionAdapter suggestionAdapter;

    private SpringView searchSv;
    private RecyclerView searchRv;

    private OnSearchActionChanged onSearchActionChanged;

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);

        searchView = (FloatingSearchView) findViewById(R.id.fSearch);
        searchView.setAdapter(suggestionAdapter = new FriendSearchSuggestionAdapter());
        setSearchNavigationIcon();
        searchView.showIcon(shouldShowNavigationIcon());

        searchView.setOnIconClickListener(() -> {
            searchView.setActivated(!searchView.isActivated());
        });

        searchView.setOnSearchListener(text->{
            searchView.setActivated(false);
        });

        searchView.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()){

                case R.id.menu_clear:
                    searchView.setText(null);
                    searchView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                    break;

                case R.id.menu_progress:

                    break;

                case R.id.menu_toggle_icon:
                    item.setChecked(!item.isChecked());
                    searchView.showIcon(item.isChecked());
                    break;
            }

            return true;
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showClearButton(s.length() > 0 && searchView.isActivated());
                showProgressBar(searchView.isActivated());
                if (onSearchActionChanged != null){
                    onSearchActionChanged.onTextChanged(s + "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchView.setOnSearchFocusChangedListener(focused -> {
            boolean isEmpty = TextUtils.isEmpty(searchView.getText());

            showClearButton(focused && !isEmpty);
            if (!focused) showProgressBar(false);

            if (focused){
                searchView.showIcon(true);
            } else {
                searchView.showIcon(shouldShowNavigationIcon());
            }
        });

        searchSv = (SpringView) findViewById(R.id.searchSv);
        searchSv.setHeader(new DefaultHeader());
        searchSv.setFooter(new DefaultFooter());

        searchRv = (RecyclerView) findViewById(R.id.searchRtRv);

        ViewTreeObserver viewTreeObserver = root.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewTreeObserver.removeOnPreDrawListener(this);

                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) searchSv.getLayoutParams();
                layoutParams.setMargins(0,searchView.getHeight(),0,0);
                searchSv.setLayoutParams(layoutParams);

                return true;
            }
        });
    }

    private void setSearchNavigationIcon() {
        Drawable drawable = new SearchArrowDrawable(mAppCtx);
        drawable = ViewUtils.getTinted(drawable,mRes.getColor(R.color.hitalk_deep_yellow));
        searchView.setIcon(drawable);
    }

    private boolean shouldShowNavigationIcon() {
        return searchView.getMenu().findItem(R.id.menu_toggle_icon).isChecked();
    }

    public void showClearButton(boolean show){
        searchView.getMenu().findItem(R.id.menu_clear).setVisible(show);
    }

    public void showProgressBar(boolean show){
        searchView.getMenu().findItem(R.id.menu_progress).setVisible(show);
    }
}
