package com.vivifram.second.hitalk.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import com.vivifram.second.hitalk.R;
import com.zuowei.utils.common.TagUtil;

/**
 * Created by zuowei on 16-7-27.
 */

/*just try to pop out the window then release work*/
public class RichAutoCompleteTextView extends EditText implements Filter.FilterListener{

    private static final String TAG = TagUtil.makeTag(RichAutoCompleteTextView.class);
    private Filter mFilter;
    private ListAdapter mAdapter;
    private boolean mBlockCompletion;
    private int mDropdownLayoutId;
    private int mThreshold;
    private PopupWindow mPopup;
    private boolean mImeVisible;
    private View mContentView;
    private int mPopWidth;
    private int mPopHeight;
    private int mContentHeight;
    private int mContentWidth;
    private RichAutoctHelper mHelper;
    private Handler mHandler;

    public interface RichAutoctHelper{
        void onContentViewAttached(View view, PopupWindow popupWindow);
        void onContentChanged();
    }

    public RichAutoCompleteTextView(Context context) {
        this(context,null);
    }

    public RichAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.autoCompleteTextViewStyle);
    }

    public RichAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RichAutoCompleteTextView, defStyleAttr,0);
        mDropdownLayoutId = a.getResourceId(R.styleable.RichAutoCompleteTextView_dropdown_layout_id,NO_ID);
        mImeVisible = a.getBoolean(R.styleable.RichAutoCompleteTextView_ime_visible,true);
        if (a.hasValue(R.styleable.RichAutoCompleteTextView_popHeight)) {
            mPopHeight = a.getLayoutDimension(R.styleable.RichAutoCompleteTextView_popHeight, "popHeight");
        }
        if (a.hasValue(R.styleable.RichAutoCompleteTextView_popWidth)) {
            mPopWidth = a.getLayoutDimension(R.styleable.RichAutoCompleteTextView_popWidth, "popWidth");
        }

        a.recycle();
        setFocusable(true);
        addTextChangedListener(new MyWatcher());
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        /*mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case SHOW_POP:
                        break;
                    case DISMISS_POP:
                        break;
                }
            }
        };*/
    }

    public boolean enoughToFilter() {
        return getText().length() >= mThreshold;
    }

    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            mFilter = ((Filterable) mAdapter).getFilter();
        } else {
            mFilter = null;
        }
    }

    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onFilterComplete(int count) {
        updateDropDownForFilter(count);
    }

    private void updateDropDownForFilter(int count) {
        if (getWindowVisibility() == View.GONE) return;
        final boolean enoughToFilter = enoughToFilter();
        if (count > 0 && enoughToFilter) {
            if (hasFocus() && hasWindowFocus()) {
                showDropDown();
            }
        } else if (isPopupShowing()) {
            dismissDropDown();
        }
    }

    private void dismissDropDown() {
        if (mPopup != null && mPopup.isShowing()){
            mPopup.dismiss();
            ensureImeVisible(true);
        }
    }

    private void showDropDown() {

        if (mDropdownLayoutId != -1 ||
                mContentView != null){
            if (mPopup == null){
                if (mContentView == null){
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    mContentView = inflater.inflate(mDropdownLayoutId,null);
                }
                int width = mPopWidth == 0?getWidth(): mPopWidth;
                int height = mPopHeight == 0?getHeight(): mPopHeight;
                mPopup = new PopupWindow(mContentView,width,height);
                mPopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_window_transparent,null));
                if (mHelper != null){
                    mHelper.onContentViewAttached(mContentView,mPopup);
                }
            }else {
                if (mHelper != null){
                    mHelper.onContentChanged();
                }
            }
            if (!isPopupShowing()){
                ensureImeVisible(mImeVisible);
                //mPopup.setFocusable(true);
                mPopup.setOutsideTouchable(true);
                //try getContentHeight;
                if (mContentHeight == 0) {
                    mContentView.setAlpha(0f);
                    mPopup.showAtLocation(this, Gravity.NO_GRAVITY, getLeft(), getTop() - mContentHeight);
                    mContentView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mContentHeight == 0) {
                                mContentHeight = mContentView.getHeight();
                                mContentView.setAlpha(1f);
                                mPopup.dismiss();
                            }
                            showAtTop();
                        }
                    });
                }else {
                    showAtTop();
                }
            }
        }
    }

    private void showAtTop() {
        mPopup.showAtLocation(RichAutoCompleteTextView.this, Gravity.NO_GRAVITY, getLeft(), getTop() - mContentHeight);
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                mContentHeight = mContentView.getHeight();
                mPopup.update(RichAutoCompleteTextView.this,0,-mContentHeight-getHeight(),-1,-1);
            }
        });
    }

    public View getContentView() {
        return mContentView;
    }

    private class MyWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
            if (s.length() == 0){
                dismissDropDown();
            }
            doAfterTextChanged();
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            doBeforeTextChanged();
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    private void doBeforeTextChanged() {

    }

    private void doAfterTextChanged() {
        if (mBlockCompletion) return;
        if (enoughToFilter()) {
            if (mFilter != null) {
                mFilter.filter(getText(),this);
            }
        }
    }

    public void replaceText(CharSequence text){
        mBlockCompletion = true;
        replaceTextInner(text);
        mBlockCompletion = false;
    }

    private void replaceTextInner(CharSequence text) {
        clearComposingText();
        setText(text);
        // make sure we BaseDialog the caret at the end of the text view
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }

    public void ensureImeVisible(boolean visible) {
        /*if (!visible) Utils.hideSoftInput(getContext(),this);*/
        mPopup.setInputMethodMode(visible
                ? PopupWindow.INPUT_METHOD_NEEDED : PopupWindow.INPUT_METHOD_NOT_NEEDED);
    }

    private boolean isPopupShowing(){
        if (mPopup == null){
            return false;
        }
        return mPopup.isShowing();
    }

    public void setContentView(View contentView){
        mContentView = contentView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissDropDown();
    }

    public void setThreshold(int threshold){
        mThreshold = threshold;
    }

    public void setHelper(RichAutoctHelper helper) {
        this.mHelper = helper;
    }
}
