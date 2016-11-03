package com.vivifram.second.hitalk.ui.layout;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.HiTalkActivity;
import com.vivifram.second.hitalk.ui.page.AddressFragment;
import com.vivifram.second.hitalk.ui.page.BlackboardFragment;
import com.vivifram.second.hitalk.ui.page.HiTalkFragment;
import com.vivifram.second.hitalk.ui.page.MeFragment;
import com.vivifram.second.hitalk.ui.page.MessageFragment;
import com.zuowei.utils.common.DisplayUtil;

import java.util.HashMap;

import cn.bingoogolapple.badgeview.BGABadgeRadioButton;
import cn.bingoogolapple.badgeview.BGABadgeViewHelper;

/**
 * Created by zuowei on 16-7-22.
 */
public class HiTalkLayout extends BaseLayout {

    private int mTabIconIds[] = new int[]{R.drawable.hi,R.drawable.message,
    R.drawable.address,R.drawable.blackboard,R.drawable.me};
    private int mTabTipsIds[] = new int[]{R.string.tab_hi,R.string.tab_message,
    R.string.tab_address,R.string.tab_blackboard,R.string.tab_me};

    private HashMap<Integer,BGABadgeRadioButton> radioBtns;

    private SmartTabLayout mPagerTab;
    private ViewPager mPager;
    private HiTalkActivity mActivity;
    public HiTalkLayout(View rootView) {
        super(rootView);
        radioBtns = new HashMap<>();
        mActivity = (HiTalkActivity) mCtx;
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        init();
    }

    private void init() {
        mPagerTab = (SmartTabLayout) findViewById(R.id.viewpagerTab);
        initPagerTab();
        mPager = (ViewPager) findViewById(R.id.mainPager);
        FragmentPagerItems pages = FragmentPagerItems.with(mActivity)
                .add(R.string.tab_hi, HiTalkFragment.class)
                .add(R.string.tab_message, MessageFragment.class)
                .add(R.string.tab_address, AddressFragment.class)
                .add(R.string.tab_blackboard, BlackboardFragment.class)
                .add(R.string.tab_me, MeFragment.class)
                .create();
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                mActivity.getSupportFragmentManager(), pages);

        mPager.setOffscreenPageLimit(5);
        mPager.setAdapter(adapter);

        mPagerTab.setViewPager(mPager);
    }

    public BGABadgeRadioButton getRadioBtn(int index){
        if (index < 0 || index >= radioBtns.size())
            return null;
        return radioBtns.get(index);
    }

    private void initPagerTab() {
        mPagerTab.setCustomTabView((container, position, adapter) -> {
            View view = mLayoutInflater.inflate(R.layout.custom_viewpager_tab, container, false);
            BGABadgeRadioButton radioButton = (BGABadgeRadioButton) view.findViewById(R.id.RdTabItem);
            Drawable drawable = mRes.getDrawable(mTabIconIds[position % mTabIconIds.length]);
            drawable.setBounds(0,0,60,65);
            radioButton.setCompoundDrawables(null,drawable,null,null);
            radioButton.setText(mRes.getString(mTabTipsIds[position % mTabTipsIds.length]));
            radioBtns.put(position,radioButton);
            return view;
        });
    }
}
