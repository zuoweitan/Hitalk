package com.vivifram.second.hitalk.ui.page.layout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.SelectStudentsActivity;
import com.vivifram.second.hitalk.ui.page.layout.adapter.MessagePreviewRvAdapter;
import com.vivifram.second.hitalk.ui.pop.message.MessageMenuPopup;
import com.vivifram.second.hitalk.ui.springview.container.DefaultFooter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-9-25.
 */
public class MessageFragmentLayout extends BaseFragmentLayout {

    public MessageFragmentLayout(View root) {
        super(root);
    }

    private BGATitlebar titlebar;

    private SpringView messageLtSv;
    private RecyclerView messageLtRv;

    private MessagePreviewRvAdapter messagePreviewRvAdapter;

    private MessageMenuPopup messageMenuPopup;

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        init();
    }

    private void init() {
        messageLtSv = (SpringView) findViewById(R.id.messageLtSv);
        messageLtRv = (RecyclerView) findViewById(R.id.messageLtRv);

        messageLtSv.setHeader(new DefaultHeader());
        messageLtSv.setFooter(new DefaultFooter());

        messagePreviewRvAdapter = new MessagePreviewRvAdapter();
        messageLtRv.setLayoutManager(new LinearLayoutManager(mCtx));
        messageLtRv.setHasFixedSize(true);
        messageLtRv.setAdapter(messagePreviewRvAdapter);

        titlebar = (BGATitlebar) findViewById(R.id.titleBar);
        titlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate(){
            @Override
            public void onClickRightCtv() {
                super.onClickRightCtv();
                messageMenuPopup.showPopupWindow(titlebar.getRightCtv());
            }
        });

        messageMenuPopup = new MessageMenuPopup(mAct);
        messageMenuPopup.setOnMenuItemClick(pos->{
            switch (pos){
                case 0:
                    SelectStudentsActivity.start(mCtx);
                    break;
            }
        });
    }


}
