package com.vivifram.second.hitalk.bean;

import com.avos.avoscloud.AVException;

/**
 * Created by zuowei on 16-7-31.
 */
public class AvResult extends AVException {
    public String error;
    public AvResult(int theCode, String theMessage) {
        super(theCode, theMessage);
    }

    public AvResult(String message, Throwable cause) {
        super(message, cause);
    }

    public AvResult(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return error;
    }

    @Override
    public String toString() {
        return "code = "+code+" error = "+getMessage();
    }
}
