package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.bean.address.Friend;
import com.vivifram.second.hitalk.ui.ChatRoomActivity;
import com.vivifram.second.hitalk.ui.NewFriendConfirmActivity;
import com.vivifram.second.hitalk.ui.recycleview.address.DividerDecoration;
import com.vivifram.second.hitalk.ui.recycleview.address.FriendsAdapter;
import com.vivifram.second.hitalk.ui.springview.container.AddressRotationHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.CommonItem;
import com.vivifram.second.hitalk.ui.view.SRecyclerView;
import com.zuowei.utils.pinyin.CharacterParser;

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

    private SpringView fSv;

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
        newfCi.setOnClickListener(view-> NewFriendConfirmActivity.start(mAct));

        nearByCi = (CommonItem) findViewById(R.id.nearbyp);
        fillCommonItem(nearByCi,R.drawable.nearby,mRes.getString(R.string.nearbyp));
        nearByCi.showDivider(false);

        recyclerView = (SRecyclerView) findViewById(R.id.friendList);
        recyclerView.setHasFixedSize(true);

        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mCtx, orientation, false);
        recyclerView.setLayoutManager(layoutManager);

        friendsAdapter = new FriendsAdapter();
        friendsAdapter.setOnItemClickListener((item, position) -> {
            ChatRoomActivity.start(mAct, Constants.ParamsKey.Chat.TO_FRIEND, item.getUserId());
        });
        recyclerView.setAdapter(friendsAdapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(friendsAdapter);
        recyclerView.addItemDecoration(headersDecor);
        recyclerView.addItemDecoration(new DividerDecoration(mCtx));
        recyclerView.setItemAnimator(null);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                animateCommonItems(dy);
            }
        });

        fSv = (SpringView) findViewById(R.id.fLtSv);
        fSv.setHeader(new AddressRotationHeader(mAct,recyclerView));
    }

    public void setOnFreshListener(SpringView.OnFreshListener onFreshListener){
        fSv.setListener(onFreshListener);
    }

    public void notifyFreshDone(){
        fSv.onFinishFreshAndLoad();
    }

    public void refresh(List<Friend> result){
        friendsAdapter.addAll(result);
    }

    public CommonItem getNewfCi(){
        return newfCi;
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

    private void fillCommonItem(CommonItem commonItem, int resId, String title){
        commonItem.setBackgroundColor(mRes.getColor(R.color.colorWhite));
        commonItem.setType(CommonItem.Type.SummaryImgSummaryTxt_DetailImg);
        Drawable drawable = mRes.getDrawable(resId);
        commonItem.setSummaryImg(drawable,54,54);
        commonItem.setSummaryText(title);
        commonItem.setDetailImg(R.drawable.detail_arrow);
    }
}
