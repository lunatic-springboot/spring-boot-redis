package cn.lunatic.spring.boot.redis.annotation;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ganfeng
 * @date 2018/12/21
 */
@Data
@Builder
public class UserInfoBO implements Serializable {
    private static final long serialVersionUID = 3258484060952395911L;
    private int id;
    private String name;
}
