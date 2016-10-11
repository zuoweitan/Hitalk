package com.vivifram.second.hitalk.bean.parser;

import com.vivifram.second.hitalk.bean.BaseBean;
import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.bean.blackboard.FavortItem;

/**
 * Created by zuowei on 16-8-29.
 */
public class ParserFactory {

    public static <T extends BaseBean> BeanParser getParser(T t){
        if (t instanceof BnItem){
            return new BnItemParser();
        }

        if (t instanceof CommentItem){
            return new CommentParser();
        }

        if (t instanceof FavortItem){
            return new FavortParser();
        }

        return null;
    }
}
