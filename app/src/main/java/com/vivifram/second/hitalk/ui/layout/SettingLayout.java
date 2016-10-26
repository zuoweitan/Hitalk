package com.vivifram.second.hitalk.ui.layout;

import android.view.View;
import android.widget.Button;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.ui.view.BGATitlebar;
import com.vivifram.second.hitalk.ui.view.CommonItem;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-26 下午2:11
 * 修改人：zuowei
 * 修改时间：16-10-26 下午2:11
 * 修改备注：
 */
public class SettingLayout extends BaseLayout {

    private CommonItem accountMci;
    private CommonItem infoMci;
    private CommonItem notifyCi;
    private CommonItem aboutCi;

    private Button logoutBt;

    private BGATitlebar titlebar;

    public SettingLayout(View rootView) {
        super(rootView);
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);

        titlebar = (BGATitlebar) findViewById(R.id.titleBar);

        accountMci = (CommonItem) findViewById(R.id.acci);
        fillCommonItem(accountMci,mRes.getString(R.string.accountManager));
        infoMci = (CommonItem) findViewById(R.id.afci);
        fillCommonItem(infoMci,mRes.getString(R.string.infoManager));
        notifyCi = (CommonItem) findViewById(R.id.ntci);
        fillCommonItem(notifyCi,mRes.getString(R.string.notifyInfo));
        aboutCi = (CommonItem) findViewById(R.id.abci);
        fillCommonItem(aboutCi,mRes.getString(R.string.about));
        aboutCi.showDivider(false);

        logoutBt = (Button) findViewById(R.id.logoutBt);
    }

    public void setTitleDelegate(BGATitlebar.BGATitlebarDelegate delegate){
        titlebar.setDelegate(delegate);
    }

    public void setOnLogoutButtonClick(View.OnClickListener onLogoutButtonClick){
        logoutBt.setOnClickListener(onLogoutButtonClick);
    }

    private void fillCommonItem(CommonItem commonItem,String title){
        commonItem.setBackgroundColor(mRes.getColor(R.color.colorWhite));
        commonItem.setType(CommonItem.Type.SummaryTxt_DetailImg);
        commonItem.setSummaryText(title);
        commonItem.setDetailImg(R.drawable.detail_arrow);
    }
}
