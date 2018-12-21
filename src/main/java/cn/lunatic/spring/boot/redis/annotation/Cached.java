package cn.lunatic.spring.boot.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存枚举
 *
 * @author ganfeng
 * @date 2018/12/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Cached {
    /**
     * 缓存名称
     *
     * @return
     */
    String cacheName() default "";

    /**
     * 缓存Key
     *
     * @return
     */
    String[] key() default {""};

    /**
     * 缓存超时时间,单位:秒,默认1小时
     *
     * @return
     */
    int expire() default 3600;
}
