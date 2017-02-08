package com.vivifram.second.hitalk.ui.page.layout;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.NoDoubleClickListener;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 17-2-5.
 */

public class SearchFriend2Layout extends BaseFragmentLayout{

    public interface OnSearchBarListener{
        void onback();
        void onSearchStart(String s);
    }

    public SearchFriend2Layout(View root) {
        super(root);
    }

    private EditText searchEt;
    private LinearLayout searchStartLt;
    private TextView searchStartTv;
    private ImageView backIv;
    private ImageView clearIv;
    private TextView notFountTv;
    private OnSearchBarListener onSearchBarListener;

    private SpannableStringBuilder startContentBuilder = new SpannableStringBuilder();

    @Override
    public void onViewCreate(View root) {
        super.onViewCreate(root);

        searchEt = (EditText) findViewById(R.id.searchEt);
        searchStartLt = (LinearLayout) findViewById(R.id.searchStartLt);
        searchStartTv = (TextView) findViewById(R.id.searchStartTv);

        backIv = (ImageView) findViewById(R.id.backIv);
        clearIv = (ImageView) findViewById(R.id.clearIv);
        notFountTv = (TextView) findViewById(R.id.notFoundTv);

        backIv.setOnClickListener(View -> {
            if (onSearchBarListener != null) {
                onSearchBarListener.onback();
            }
        });

        clearIv.setOnClickListener(View -> {
            if (searchEt != null) {
                searchEt.setText("");
            }
        });

        searchStartLt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                NLog.i(TagUtil.makeTag(getClass()),"onNoDoubleClick search key = "+searchEt.getText());
                if (onSearchBarListener != null && !TextUtils.isEmpty(searchEt.getText())) {
                    onSearchBarListener.onSearchStart(searchEt.getText() + "");
                }
            }
        });

        showSearchContent(false);
        showNotFount(false);

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)){
                    showSearchContent(false);
                    showClear(false);
                } else {
                    showSearchContent(true);
                    showClear(true);
                    setStartContent(s + "");
                }

                showNotFount(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void showSearchContent(boolean show){
        if (searchStartLt != null) {
            searchStartLt.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void showClear(boolean show){
        if (clearIv != null) {
            clearIv.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void showNotFount(boolean show){
        if (notFountTv != null) {
            notFountTv.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void setStartContent(String text){
        startContentBuilder.clearSpans();
        startContentBuilder.clear();
        SpannableString searchStr = new SpannableString(mRes.getString(R.string.search) + ":");
        searchStr.setSpan(new ForegroundColorSpan(Color.BLACK),0,searchStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        SpannableString startContent = new SpannableString(text);
        startContent.setSpan(new ForegroundColorSpan(mRes.getColor(R.color.hitalk_yellow))
                ,0,startContent.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        startContentBuilder.append(searchStr).append(startContent);
        searchStartTv.setText(startContentBuilder);
    }
}
