package com.vivifram.second.hitalk.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 项目名称：Hitalk
 * 类描述：
 * 创建人：zuowei
 * 创建时间：17-2-21 下午5:06
 * 修改人：zuowei
 * 修改时间：17-2-21 下午5:06
 * 修改备注：
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int id();
    boolean boundClick() default false;
}
