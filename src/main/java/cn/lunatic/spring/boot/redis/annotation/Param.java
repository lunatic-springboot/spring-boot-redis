package cn.lunatic.spring.boot.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存枚举
 *
 * @author ganlunatic
 * @date 2018/12/21
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * 参数命名
     * @return
     */
    String value();
}

