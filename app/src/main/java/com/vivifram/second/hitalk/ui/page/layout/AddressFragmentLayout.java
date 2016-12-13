package com.vivifram.second.hitalk.ui.page.layout;

import android.view.View;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.AddFriendActivity;
import com.vivifram.second.hitalk.ui.pop.address.AddressMenuPopup;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;

import cn.bingoogolapple.badgeview.BGABadgeRelativeLayout;

/**
 * Created by zuowei on 16-9-26.
 */

public class AddressFragmentLayout extends BaseFragmentLayout {

    public AddressFragmentLayout(View root) {
        super(root);
    }

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        init();
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public interface  OnTabClickListener{
        void onTabClick(int index);
    }

    private OnTabClickListener onTabClickListener;

    private Tab tab1;
    private Tab tab2;
    private Tab tab3;
    private BGATitlebar titlebar;
    private AddressMenuPopup addressMenuPopup;

    private void init() {

        View tabsLt = findViewById(R.id.tabsLt);
        tab1 = new Tab(tabsLt.findViewById(R.id.tabOne));
        tab2 = new Tab(tabsLt.findViewById(R.id.tabTwo));
        tab3 = new Tab(tabsLt.findViewById(R.id.tabThree));

        tab1.setTitle(mRes.getString(R.string.alumnipage));
        tab1.setSelected(true);
        tab1.setIndex(0);
        tab2.setTitle(mRes.getString(R.string.friends));
        tab2.setSelected(false);
        tab2.setIndex(1);
        tab3.setTitle(mRes.getString(R.string.groups));
        tab3.setSelected(false);
        tab3.setIndex(2);

        View.OnClickListener onClickListener = v -> {
            tab1.setSelected(false);
            tab2.setSelected(false);
            tab3.setSelected(false);
            Tab tab = null;
            switch (v.getId()){
                case R.id.tabOne:
                        tab = tab1;
                    break;
                case R.id.tabTwo:
                        tab = tab2;
                    break;
                case R.id.tabThree:
                        tab = tab3;
                    break;
            }
            if (tab != null) {
                tab.setSelected(true);
                if (onTabClickListener != null) {
                    onTabClickListener.onTabClick(tab.index);
                }
            }
        };

        tab1.setOnTabClickListener(onClickListener);
        tab2.setOnTabClickListener(onClickListener);
        tab3.setOnTabClickListener(onClickListener);


        addressMenuPopup = new AddressMenuPopup(mAct);
        addressMenuPopup.setOnMenuItemClick(pos->{
           switch (pos){
               case 0:
                    AddFriendActivity.start(mAct);
                   break;
               case 1:
                   break;
           }
        });
        titlebar = (BGATitlebar) findViewById(R.id.titleBar);
        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickRightCtv() {
                addressMenuPopup.showPopupWindow(titlebar.getRightCtv());
            }
        });
    }

    public Tab get(int index){
        switch (index){
            case  1:
                return tab1;
            case 2:
                return tab2;
            case 3:
                return tab3;
        }
        return null;
    }

    public int getContentId(){
        return R.id.addressContent;
    }

    public class Tab{
        private int index;
        private TextView titleTv;
        private BGABadgeRelativeLayout root;
        public Tab(View v){
            root = (BGABadgeRelativeLayout) v;
            titleTv = (TextView) v.findViewById(R.id.titleTv);
        }

        public void showBadge(boolean show){
            if (show){
                root.showCirclePointBadge();
            }else {
                root.hiddenBadge();
            }
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setTitle(String title){
            titleTv.setText(title);
        }

        public void setSelected(boolean selected){
            root.setSelected(selected);
        }

        public void setOnTabClickListener(View.OnClickListener onTabClickListener){
            root.setOnClickListener(onTabClickListener);
        }
    }

}
