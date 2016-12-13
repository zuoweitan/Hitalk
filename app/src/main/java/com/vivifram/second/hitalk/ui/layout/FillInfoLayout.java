package com.vivifram.second.hitalk.ui.layout;

import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.state.Error;
import com.vivifram.second.hitalk.state.NickNameError;
import com.vivifram.second.hitalk.ui.flowlayout.FlowLayout;
import com.vivifram.second.hitalk.ui.flowlayout.TagAdapter;
import com.vivifram.second.hitalk.ui.flowlayout.TagFlowLayout;
import com.vivifram.second.hitalk.ui.layout.adapter.CollegeAdapter;
import com.vivifram.second.hitalk.ui.view.RichAutoCompleteTextView;
import com.vivifram.second.hitalk.ui.view.ShrinkButton;
import com.vivifram.second.hitalk.ui.view.XEditText;
import com.vivifram.second.hitalk.ui.view.ass.AutoTextArrayAdapter;
import com.vivifram.second.hitalk.wheelview.widget.WheelView;
import com.zuowei.utils.common.DisplayUtil;
import com.zuowei.utils.common.JsonUtils;
import com.zuowei.utils.common.NToast;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.common.TextSpanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by zuowei on 16-7-26.
 */
public class FillInfoLayout extends BaseLayout implements View.OnClickListener,
        RichAutoCompleteTextView.RichAutoctHelper{

    private static final String TAG = TagUtil.makeTag(FillInfoLayout.class);
    private static final int MAN = 1;
    private static final int WOMAN = 2;
    private XEditText mNickNameEt;
    private RichAutoCompleteTextView mCollegeEt;
    private RadioGroup mSexRg;
    private EditText mInterestBt;
    private Button mInterestFakeBt;
    private ShrinkButton mCBt;
    private Error<NickNameError.EMessage> mNickErr;
    private WheelView mCollegeWv;
    private PopupWindow mInterestWindow;
    private TagFlowLayout mInterestFlow;
    private String []mInterestArray;
    private String []mCollegeArray;
    public FillInfoLayout(View rootView) {
        super(rootView);
    }

    @Override
    public void onContentViewCreate(View view) {
        super.onContentViewCreate(view);
        init();
    }

    private void init() {
        mNickErr = new NickNameError();
        mNickNameEt = (XEditText) findViewById(R.id.etNickname);
        mCollegeEt = (RichAutoCompleteTextView) findViewById(R.id.etCollege);
        mSexRg = (RadioGroup) findViewById(R.id.RgSex);
        mInterestBt = (EditText) findViewById(R.id.interestBt);
        mInterestFakeBt = (Button) findViewById(R.id.interestFakeBt);
        mCBt = (ShrinkButton) findViewById(R.id.btnfill);

        mCollegeEt.setThreshold(1);
        mCollegeEt.setHelper(this);
        mCollegeArray = mRes.getStringArray(R.array.collegesArray);
        mCollegeEt.setAdapter(new AutoTextArrayAdapter<>(mCtx,R.layout.auto_search_item,mCollegeArray));

        mCBt.setOnClickListener(this);
        mInterestFakeBt.setOnClickListener(this);

        mNickNameEt.setOnXTextChangeListener(new XEditText.OnXTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    if (s.toString().contains(" ")){
                        mNickErr.setError(NickNameError.EMessage.HAS_SPACE);
                        mNickNameEt.showMarker();
                    }else {
                        checkAvailable(s.toString());
                    }
                }else {
                    mNickErr.setError(NickNameError.EMessage.IS_NULL);
                    mNickNameEt.showMarker();
                }
            }

        });

        mNickNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (TextUtils.isEmpty(mNickNameEt.getText())){
                        mNickErr.setError(NickNameError.EMessage.HAS_SPACE);
                        mNickNameEt.showMarker();
                    }
                }
            }
        });

        mNickNameEt.setOnMarkerClickListener(new XEditText.OnMarkerClickListener() {
            @Override
            public void onMarkerClick(float x, float y) {
                NToast.shortToast(mAppCtx,mNickErr.getMessage());
            }
        });

        mInterestArray = mRes.getStringArray(R.array.interestArray);
    }

    private void checkAvailable(String nickName) {

    }


    public int getSex(){
        int sex = mSexRg.getCheckedRadioButtonId();
        switch (sex){
            case R.id.Rbman:
                return MAN;
            default:
                return WOMAN;
        }
    }

    public String getCollege(){
        return mCollegeEt.getText()+"";
    }

    public String getNickName(){
        return mNickNameEt.getText()+"";
    }

    public String getInterests(){
        if (mInterestFlow != null){
            List temp = new ArrayList();
            for (int i = 0;i < mInterestFlow.getSelectedList().size();i++){
                temp.add(mInterestArray[i]);
            }
            return JsonUtils.toJsonString(temp);
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnfill:
                    if (checkAll()){
                        doFill();
                    }else {
                        mCBt.reset();
                    }
                break;

            case R.id.interestFakeBt:
                    popInterestWindow();
                break;
        }
    }

    private void popInterestWindow() {
        if (mInterestWindow == null){
            View v = mLayoutInflater.inflate(R.layout.interest_selector_layout,null);
            initInterestFlow(v);
            mInterestWindow = new PopupWindow(v,mInterestBt.getWidth(),-2);
            mInterestWindow.setOutsideTouchable(true);
            mInterestWindow.setBackgroundDrawable(mRes.getDrawable(R.drawable.popup_window_transparent));
        }
        if (mInterestWindow.isShowing()) {
            mInterestWindow.dismiss();
        }else {
            /*mInterestWindow.showAtLocation(mInterestFakeBt, Gravity.TOP,0,
                    mInterestFakeBt.getBottom() + mInterestFakeBt.getHeight() / 2);*/
            mInterestWindow.showAsDropDown(mInterestFakeBt);
        }
    }

    private void initInterestFlow(View v) {
        mInterestFlow = (TagFlowLayout) v.findViewById(R.id.interestFlow);
        mInterestFlow.setMaxSelectCount(4);
        mInterestFlow.setAdapter(new TagAdapter<String>(mInterestArray) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mLayoutInflater.inflate(R.layout.interest_item,
                        mInterestFlow, false);
                tv.setText(s);
                return tv;
            }
        });

        mInterestFlow.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
                resetInterest(selectPosSet);
            }
        });

        mInterestFlow.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (mInterestFlow.getSelectedList().size() >= 4){
                    NToast.shortToast(mAppCtx,mRes.getString(R.string.interestFull));
                }
                return false;
            }
        });
    }

    private void resetInterest(Set<Integer> selectPosSet) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        SpannableString s;
        for (int i : selectPosSet){
            String item = mInterestArray[i];
            s = new SpannableString(item);
            s.setSpan(TextSpanUtils.getTextWithBackground(mRes.getDrawable(R.drawable.text_backgroud,null),
                    DisplayUtil.dip2px(mAppCtx,16), DisplayUtil.dip2px(mAppCtx,12), mRes.getColor(R.color.hint)),
                    0,item.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sb.append(s);
            sb.append(" ");
        }
        mInterestBt.clearComposingText();
        mInterestBt.setText(sb);
    }

    private boolean checkAll() {
        String nickName = mNickNameEt.getText().toString().trim();
        String college = mCollegeEt.getText().toString().trim();
        String interest = mInterestBt.getText().toString().trim();

        if (TextUtils.isEmpty(nickName)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.nicknameNull));
            return false;
        }
        if (TextUtils.isEmpty(college)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.collegeNull));
            return false;
        }

        if (!Arrays.asList(mCollegeArray).contains(college)){
            NToast.shortToast(mCtx, mRes.getString(R.string.college_illegal));
            return false;
        }

        if (TextUtils.isEmpty(interest)) {
            NToast.shortToast(mCtx, mRes.getString(R.string.interestNull));
            return false;
        }
        if (nickName.contains(" ")) {
            NToast.shortToast(mCtx, mRes.getString(R.string.nickHasSpace));
            return false;
        }
        return true;
    }

    private void doFill() {
        if (mOnLayoutEventListener != null){
            mOnLayoutEventListener.onContentConfirmed(this);
        }
    }

    @Override
    public void onContentViewAttached(View view, final PopupWindow popupWindow) {
        mCollegeWv = (WheelView) view.findViewById(R.id.collegeWv);
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.textColor = mRes.getColor(R.color.alpha_white);
        style.textSize = 16;
        style.selectedTextColor = mRes.getColor(R.color.colorWhite);
        style.selectedTextSize = 20;
        style.backgroundColor = mRes.getColor(R.color.transparent);
        mCollegeWv.setStyle(style);
        mCollegeWv.setWheelAdapter(new CollegeAdapter(mCtx,mCollegeWv));
        mCollegeWv.setWheelSize(5);
        mCollegeWv.setWheelData(getCollegeData());
        mCollegeWv.setSelection(0);
        mCollegeWv.setSkin(WheelView.Skin.None);
        mCollegeWv.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener<String>() {
            @Override
            public void onItemClick(int position, String o) {
                mCollegeEt.replaceText(o);
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onContentChanged() {
        mCollegeWv.setWheelData(getCollegeData());
    }

    private List getCollegeData() {
        ArrayList<String> list = new ArrayList<>();
        ListAdapter adapter = mCollegeEt.getAdapter();
        for (int i = 0;i < adapter.getCount();i++){
            list.add((String) adapter.getItem(i));
        }
        return list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mInterestWindow != null && mInterestWindow.isShowing()){
            mInterestWindow.dismiss();
        }
    }
}
