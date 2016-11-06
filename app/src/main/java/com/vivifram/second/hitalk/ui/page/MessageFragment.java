package com.vivifram.second.hitalk.ui.page;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.EatMark;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.manager.chat.ClientManager;
import com.vivifram.second.hitalk.ui.page.layout.MessageFragmentLayout;
import com.zuowei.utils.bridge.constant.EaterAction;
import com.zuowei.utils.bridge.params.LightParam;
import com.zuowei.utils.bridge.params.chat.ConversationParam;
import com.zuowei.utils.bridge.params.chat.MessageParam;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.handlers.AbstractHandler;
import com.zuowei.utils.helper.ConversationCacheHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            conversationList.add(ClientManager.getInstance().getClient().getConversation(convId));
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
}
