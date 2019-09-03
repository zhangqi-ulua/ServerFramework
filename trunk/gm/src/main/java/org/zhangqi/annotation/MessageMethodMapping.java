package org.zhangqi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 用于函数注解，标记该函数处理哪些消息
 */
public @interface MessageMethodMapping {
	int[] value();

	boolean isNet() default false;
}
