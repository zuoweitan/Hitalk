package com.vivifram.second.hitalk.ui.page.layout;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.bean.Emojicon;
import com.vivifram.second.hitalk.bean.EmojiconGroupEntity;
import com.vivifram.second.hitalk.model.EmojiconDatas;
import com.zuowei.utils.common.SmileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zuowei on 16-8-3.
 */
public class ChatInputMenuLayout extends LinearLayout {
    FrameLayout mPrimaryMenuContainer;
    FrameLayout mEmojiconMenuContainer;

    protected ChatPrimaryMenuBaseLayout mChatPrimaryMenu;
    protected EmojiconMenuBaseLayout mEmojiconMenu;
    protected ChatExtendMenuLayout mChatExtendMenu;
    protected FrameLayout mChatExtendMenuContainer;
    protected LayoutInflater layoutInflater;

    private Handler mHandler = new Handler();
    private ChatInputMenuListener mChatInputMenuListener;
    private Context mCtx;
    private boolean mInit;

    public ChatInputMenuLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ChatInputMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatInputMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        mCtx = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.chat_input_menu_layout, this);
        mPrimaryMenuContainer = (FrameLayout) findViewById(R.id.primary_menu_container);
        mEmojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
        mChatExtendMenuContainer = (FrameLayout) findViewById(R.id.extend_menu_container);

        mChatExtendMenu = (ChatExtendMenuLayout) findViewById(R.id.extend_menu);
    }

    public void init(List<EmojiconGroupEntity> emojiconGroupList) {
        if(mInit){
            return;
        }
        if(mChatPrimaryMenu == null){
            mChatPrimaryMenu = (ChatPrimaryMenuLayout) layoutInflater.inflate(R.layout.chat_primary_menu_layout, null);
        }
        mPrimaryMenuContainer.addView(mChatPrimaryMenu);
        if(mEmojiconMenu == null){
            mEmojiconMenu = (EmojiconMenu) layoutInflater.inflate(R.layout.emojicon_menu_layout, null);
            if(emojiconGroupList == null){
                emojiconGroupList = new ArrayList<>();
                emojiconGroupList.add(new EmojiconGroupEntity(R.drawable.ee_1,
                        Arrays.asList(EmojiconDatas.getData())));
            }
            ((EmojiconMenu)mEmojiconMenu).init(emojiconGroupList);
        }
        mEmojiconMenuContainer.addView(mEmojiconMenu);

        processChatMenu();

        mChatExtendMenu.init();

        mInit = true;
    }

    public void init(){
        init(null);
    }

    /**
     * 设置自定义的表情栏，该控件需要继承自EaseEmojiconMenuBase，
     * 以及回调你想要回调出去的事件给设置的EaseEmojiconMenuListener
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EmojiconMenuBaseLayout customEmojiconMenu){
        this.mEmojiconMenu = customEmojiconMenu;
    }

    /**
     * 设置自定义的主菜单栏，该控件需要继承自EaseChatPrimaryMenuBase，
     * 以及回调你想要回调出去的事件给设置的EaseEmojiconMenuListener
     * @param customPrimaryMenu
     */
    public void setCustomPrimaryMenu(ChatPrimaryMenuBaseLayout customPrimaryMenu){
        this.mChatPrimaryMenu = customPrimaryMenu;
    }

    public ChatPrimaryMenuBaseLayout getPrimaryMenu(){
        return mChatPrimaryMenu;
    }

    public ChatExtendMenuLayout getExtendMenu(){
        return mChatExtendMenu;
    }

    public EmojiconMenuBaseLayout getEmojiconMenu(){
        return mEmojiconMenu;
    }


    /**
     * 注册扩展菜单的item
     *
     * @param name
     *            item名字
     * @param drawableRes
     *            item背景
     * @param itemId
     *            id
     * @param listener
     *            item点击事件
     */
    public void registerExtendMenuItem(String name, int drawableRes, int itemId,
                                       ChatExtendMenuLayout.ChatExtendMenuItemClickListener listener) {
        mChatExtendMenu.registerMenuItem(name, drawableRes, itemId, listener);
    }

    /**
     * 注册扩展菜单的item
     *
     * @param nameRes
     *            item名字
     * @param drawableRes
     *            item背景
     * @param itemId
     *            id
     * @param listener
     *            item点击事件
     */
    public void registerExtendMenuItem(int nameRes, int drawableRes, int itemId,
                                       ChatExtendMenuLayout.ChatExtendMenuItemClickListener listener) {
        mChatExtendMenu.registerMenuItem(nameRes, drawableRes, itemId, listener);
    }

    protected void processChatMenu() {
        // 发送消息栏
        mChatPrimaryMenu.setChatPrimaryMenuListener(new ChatPrimaryMenuBaseLayout.ChatPrimaryMenuListener() {

            @Override
            public void onSendBtnClicked(String content) {
                if (mChatInputMenuListener != null)
                    mChatInputMenuListener.onSendMessage(content);
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClicked() {
                toggleMore();
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
            }


            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if(mChatInputMenuListener != null){
                    return mChatInputMenuListener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // emojicon menu
        mEmojiconMenu.setEmojiconMenuListener(new EmojiconMenuBaseLayout.EmojiconMenuListener() {

            @Override
            public void onExpressionClicked(Emojicon emojicon) {
                if(emojicon.getType() != Emojicon.Type.BIG_EXPRESSION){
                    if(emojicon.getEmojiText() != null){
                        mChatPrimaryMenu.onEmojiconInputEvent(SmileUtils.getSmiledText(mCtx,emojicon.getEmojiText()));
                    }
                }else{
                    if(mChatInputMenuListener != null){
                        mChatInputMenuListener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                mChatPrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }

    /**
     * 显示或隐藏图标按钮页
     *
     */
    protected void toggleMore() {
        if (mChatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mChatExtendMenuContainer.setVisibility(View.VISIBLE);
                    mChatExtendMenu.setVisibility(View.VISIBLE);
                    mEmojiconMenu.setVisibility(View.GONE);
                }
            }, 50);
        } else {
            if (mEmojiconMenu.getVisibility() == View.VISIBLE) {
                mEmojiconMenu.setVisibility(View.GONE);
                mChatExtendMenu.setVisibility(View.VISIBLE);
            } else {
                mChatExtendMenuContainer.setVisibility(View.GONE);
            }

        }

    }

    /**
     * 显示或隐藏表情页
     */
    protected void toggleEmojicon() {
        if (mChatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mChatExtendMenuContainer.setVisibility(View.VISIBLE);
                    mChatExtendMenu.setVisibility(View.GONE);
                    mEmojiconMenu.setVisibility(View.VISIBLE);
                }
            }, 50);
        } else {
            if (mEmojiconMenu.getVisibility() == View.VISIBLE) {
                mChatExtendMenuContainer.setVisibility(View.GONE);
                mEmojiconMenu.setVisibility(View.GONE);
            } else {
                mChatExtendMenu.setVisibility(View.GONE);
                mEmojiconMenu.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        mChatPrimaryMenu.hideKeyboard();
    }

    /**
     * 隐藏整个扩展按钮栏(包括表情栏)
     */
    public void hideExtendMenuContainer() {
        mChatExtendMenu.setVisibility(View.GONE);
        mEmojiconMenu.setVisibility(View.GONE);
        mChatExtendMenuContainer.setVisibility(View.GONE);
        mChatPrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * 系统返回键被按时调用此方法
     *
     * @return 返回false表示返回键时扩展菜单栏时打开状态，true则表示按返回键时扩展栏是关闭状态<br/>
     *         如果返回时打开状态状态，会先关闭扩展栏再返回值
     */
    public boolean onBackPressed() {
        if (mChatExtendMenuContainer.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }


    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.mChatInputMenuListener = listener;
    }


    public interface ChatInputMenuListener {
        /**
         * 发送消息按钮点击
         *
         * @param content 文本内容
         */
        void onSendMessage(String content);

        /**
         * 大表情被点击
         *
         * @param emojicon
         */
        void onBigExpressionClicked(Emojicon emojicon);

        /**
         * 长按说话按钮touch事件
         *
         * @param v
         * @param event
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);
    }
}
