package com.vivifram.second.hitalk.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：16-10-25 下午3:22
 * 修改人：zuowei
 * 修改时间：16-10-25 下午3:22
 * 修改备注：
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EatMark {
    String action();
    Class target() default Object.class;
}
