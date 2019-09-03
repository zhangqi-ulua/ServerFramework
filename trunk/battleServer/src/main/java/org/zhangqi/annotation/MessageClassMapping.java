package org.zhangqi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 用于Action类上的注解，标记该Action类处理哪个客户端请求
 */
public @interface MessageClassMapping {

	int value();

	boolean isNet() default true;
}
