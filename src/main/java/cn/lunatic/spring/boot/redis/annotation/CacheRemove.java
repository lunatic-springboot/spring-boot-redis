package cn.lunatic.spring.boot.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 移除缓存注解
 * @author ganfeng
 * @date 2018/12/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CacheRemove {
    /**
     * 缓存名称
     * @return
     */
    String cacheName() default "";

    /**
     * 缓存Key
     * @return
     */
    String[] key() default {""};

}
