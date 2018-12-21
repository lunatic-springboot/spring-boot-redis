package cn.lunatic.spring.boot.redis.bo;

import lombok.Builder;
import lombok.Data;

/**
 * 与@Param配套使用
 *
 * @author ganfeng
 * @date 2018/12/21
 */
@Data
@Builder
public class ParamFieldBO {
    /**
     * 参数对象
     */
    private Object object;
    /**
     * 参数对象值
     */
    private String fieldSet;
}
