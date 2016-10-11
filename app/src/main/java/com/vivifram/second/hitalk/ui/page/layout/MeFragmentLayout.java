package com.vivifram.second.hitalk.ui.page.layout;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.page.layout.BaseFragmentLayout;
import com.vivifram.second.hitalk.ui.springview.container.DefaultFooter;
import com.vivifram.second.hitalk.ui.springview.container.DefaultHeader;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.vivifram.second.hitalk.ui.view.CommonItem;

/**
 * Created by zuowei on 16-9-25.
 */
public class MeFragmentLayout extends BaseFragmentLayout{

    public MeFragmentLayout(View root) {
        super(root);
    }

    private RelativeLayout meInfoLv;
    private ImageView avatarIv;
    private TextView nickTv;
    private TextView signatureTv;

    private CommonItem myBnCi;
    private CommonItem interestCci;
    private CommonItem createdCci;
    private CommonItem photosCci;


    private CommonItem accountMci;
    private CommonItem infoMci;
    private CommonItem notifyCi;
    private CommonItem aboutCi;
    private SpringView spv;

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);
        init();
    }

    private void init() {
        meInfoLv = (RelativeLayout) findViewById(R.id.meInfoLv);
        avatarIv = (ImageView) meInfoLv.findViewById(R.id.meInfoAvatar);
        nickTv = (TextView) findViewById(R.id.meInfoUserName);
        signatureTv = (TextView) findViewById(R.id.meInfoSignature);

        myBnCi = (CommonItem) findViewById(R.id.bnci);
        fillCommonItem(myBnCi,mRes.getString(R.string.myBn));
        interestCci = (CommonItem) findViewById(R.id.fcci);
        fillCommonItem(interestCci,mRes.getString(R.string.interestCv));
        createdCci = (CommonItem) findViewById(R.id.ccci);
        fillCommonItem(createdCci,mRes.getString(R.string.createdCv));
        photosCci = (CommonItem) findViewById(R.id.pvci);
        fillCommonItem(photosCci,mRes.getString(R.string.myPhotos));
        photosCci.showDivider(false);

        accountMci = (CommonItem) findViewById(R.id.acci);
        fillCommonItem(accountMci,mRes.getString(R.string.accountManager));
        infoMci = (CommonItem) findViewById(R.id.afci);
        fillCommonItem(infoMci,mRes.getString(R.string.infoManager));
        notifyCi = (CommonItem) findViewById(R.id.ntci);
        fillCommonItem(notifyCi,mRes.getString(R.string.notifyInfo));
        aboutCi = (CommonItem) findViewById(R.id.abci);
        fillCommonItem(aboutCi,mRes.getString(R.string.about));
        aboutCi.showDivider(false);

        spv = (SpringView) findViewById(R.id.meSpv);
        spv.setHeader(new DefaultHeader());
        spv.setFooter(new DefaultFooter());
    }


    private void fillCommonItem(CommonItem commonItem,String title){
        commonItem.setBackgroundColor(mRes.getColor(R.color.colorWhite));
        commonItem.setType(CommonItem.Type.SummaryTxt_DetailImg);
        commonItem.setSummaryText(title);
        commonItem.setDetailImg(R.drawable.detail_arrow);
    }

    public void setAvatar(String url){
        Glide.with(mCtx).load(url)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(avatarIv);
    }

    public void setNick(String nick){
        nickTv.setText(nick);
    }

    public void setSignature(String signature){
        signatureTv.setText(signature);
    }
}
