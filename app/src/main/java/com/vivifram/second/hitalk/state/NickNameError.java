package com.vivifram.second.hitalk.state;

import com.vivifram.second.hitalk.HiTalkApplication;
import com.vivifram.second.hitalk.R;

/**
 * Created by zuowei on 16-7-27.
 */
public class NickNameError implements Error<NickNameError.EMessage> {

    private EMessage mError = EMessage.UNKNOWN;

    public enum  EMessage{
        UNKNOWN(HiTalkApplication.mAppContext.getString(R.string.unknown)),
        HAS_SPACE(HiTalkApplication.mAppContext.getString(R.string.nickHasSpace)),
        IS_NULL(HiTalkApplication.mAppContext.getString(R.string.nicknameNull)),
        IS_REPEAT(HiTalkApplication.mAppContext.getString(R.string.nicknameRepeat));

        private String mMessage;

        private EMessage(String message){
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    @Override
    public void setError(EMessage eMessage) {
        mError = eMessage;
    }

    @Override
    public EMessage getError() {
        return mError;
    }

    @Override
    public int getCode() {
        return mError.ordinal();
    }

    @Override
    public String getMessage() {
        return mError.getMessage();
    }
}
