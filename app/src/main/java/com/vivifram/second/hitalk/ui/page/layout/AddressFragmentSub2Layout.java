package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.address.Friend;
import com.vivifram.second.hitalk.ui.recycleview.address.DividerDecoration;
import com.vivifram.second.hitalk.ui.recycleview.address.FriendsAdapter;
import com.vivifram.second.hitalk.ui.view.CommonItem;
import com.vivifram.second.hitalk.ui.view.SRecyclerView;
import com.zuowei.utils.pinyin.CharacterParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuowei on 16-10-11.
 */

public class AddressFragmentSub2Layout extends BaseFragmentLayout {

    public AddressFragmentSub2Layout(View root) {
        super(root);
    }

    private CommonItem newfCi;
    private CommonItem nearByCi;

    private SRecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private CharacterParser characterParser;

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        init();
    }

    private void init() {
        characterParser = CharacterParser.getInstance();

        newfCi = (CommonItem) findViewById(R.id.newf);
        fillCommonItem(newfCi,R.drawable.newf,mRes.getString(R.string.newf));
        newfCi.showDivider(true);
        nearByCi = (CommonItem) findViewById(R.id.nearbyp);
        fillCommonItem(nearByCi,R.drawable.nearby,mRes.getString(R.string.nearbyp));
        nearByCi.showDivider(false);

        recyclerView = (SRecyclerView) findViewById(R.id.friendList);
        recyclerView.setHasFixedSize(true);

        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mCtx, orientation, false);
        recyclerView.setLayoutManager(layoutManager);

        friendsAdapter = new FriendsAdapter();

        initData(friendsAdapter);

        recyclerView.setAdapter(friendsAdapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(friendsAdapter);
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
        int maxOffset = 2 * newfCi.getHeight();
        if (offset >= 0 && offset <= maxOffset){
            if (headerView == null) {
                newfCi.setTranslationY(-maxOffset);
                nearByCi.setTranslationY(-maxOffset);
            }else {
                newfCi.setTranslationY(-offset);
                nearByCi.setTranslationY(-offset);
            }
        }
    }

    private View getHeaderView(){
        if (recyclerView.getChildCount() > 0){
            View child = recyclerView.getChildAt(0);
            int position = recyclerView.getChildAdapterPosition(child);
            if (friendsAdapter.isRecyclerViewHeader(position)){
                return child;
            }
            return null;
        }
        return null;
    }

    private void initData(FriendsAdapter friendsAdapter) {
        friendsAdapter.addAll(makeFake());
    }

    private String friends[] = new String[]{"张三","李四","王五",
            "非凡","非常","飞天","飞舞地","等端子",
            "等妻子","等鸭子","等烤鸭"};

    private List<Friend> makeFake() {
        ArrayList<Friend> list = new ArrayList<>();
        for (String temp : friends) {
            Friend friend = new Friend();
            friend.setNickName(temp);
            String pinyin = characterParser.getSelling(friend.getNickName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                friend.setSortLetters(sortString.toUpperCase());
            } else {
                friend.setSortLetters("#");
            }

            list.add(friend);
        }


        return list;
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
