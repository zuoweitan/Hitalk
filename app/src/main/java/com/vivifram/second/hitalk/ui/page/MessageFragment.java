package com.vivifram.second.hitalk.ui.page;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.Constants;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.ui.ChatRoomActivity;
import com.vivifram.second.hitalk.ui.page.layout.MessageFragmentLayout;
import com.zuowei.utils.bridge.EaterManager;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.MainPageParam;
import com.zuowei.utils.bridge.params.ViewHolderOnclickParam;
import com.zuowei.utils.bridge.params.chat.ConversationParam;
import com.zuowei.utils.bridge.params.chat.MessageParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.helper.ConversationCacheHelper;
import com.zuowei.utils.helper.HiTalkHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by zuowei on 16-7-25.
 */
@LayoutInject(name = "MessageFragmentLayout")
public class MessageFragment extends LazyFragment<MessageFragmentLayout> {
    @Override
    protected void lazyLoad() {
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_message_layout;
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        updateConversationList();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        updateConversationList();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateConversationList();
    }

    private void updateConversationList() {
        List convIdList = ConversationCacheHelper.getInstance().getSortedConversationList();
        ArrayList<AVIMConversation> conversationList = new ArrayList();
        Iterator i$ = convIdList.iterator();

        while(i$.hasNext()) {
            String convId = (String)i$.next();
            AVIMConversation conversation = ClientManager.getInstance().getClient().getConversation(convId);
            conversationList.add(conversation);
        }

        mLayout.setData(conversationList);
    }

    @EatMark(action = EaterAction.ACTION_DO_CHECK_MESSAGE)
    public class MessageReceiver extends AbstractHandler<MessageParam>{

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof MessageParam;
        }

        @Override
        public void doJobWithParam(MessageParam param) {
            NLog.i(TagUtil.makeTag(MessageFragment.class), "MessageReceiver param = " + param);
            updateConversationList();
        }
    }

    @EatMark(action = EaterAction.ACTION_DO_CHECK_CONVERSATION)
    public class OfflineMessageReceiver extends AbstractHandler<ConversationParam>{

        @Override
        public boolean isParamAvailable(LightParam param) {
            if (param != null && param instanceof ConversationParam){
                ConversationParam conversationParam = (ConversationParam) param;
                return conversationParam.getActionType() == ConversationParam.ACTION_OFFLINE_MESSAGE_COUNT_CHANGED;
            }
            return false;
        }

        @Override
        public void doJobWithParam(ConversationParam param) {
            updateConversationList();
        }
    }

    @EatMark(action = EaterAction.ACTION_VIEWHOLDER_ONCLICK)
    public class ViewHolderOnClick extends AbstractHandler<ViewHolderOnclickParam> {

        @Override
        public boolean isParamAvailable(LightParam param) {
            return param != null && param instanceof ViewHolderOnclickParam;
        }

        @Override
        public void doJobWithParam(ViewHolderOnclickParam param) {
            if (param.getType() == Constants.ViewHolderType.VIEWHOLDER_IN_MESSAGEFRAGMENT) {
                AVIMConversation avimConversation =
                        (AVIMConversation) mLayout.getData(param.getPosition());
                if (Objects.equals(avimConversation.getConversationId(), HiTalkHelper.getInstance().getModel().getSquareConversationId())) {
                    EaterManager.getInstance().broadcast(new MainPageParam());
                } else {
                    ChatRoomActivity.start(getActivity(), avimConversation);
                }
            }
        }
    }
}
