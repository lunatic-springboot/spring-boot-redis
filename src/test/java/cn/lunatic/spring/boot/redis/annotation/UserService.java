package cn.lunatic.spring.boot.redis.annotation;

import org.springframework.stereotype.Service;

/**
 * @author ganlunatic
 * @date 2018/12/21
 */
@Service
public class UserService {


    @Cached(cacheName = "USER_INFO", key = {"u.id","u.name"},expire = 10)
    public UserInfo getUserInfo(@Param("u") UserInfo userInfoBO) {
        return UserInfo.builder().id(1).name("张三").build();
    }

    @CacheRemove(cacheName = "USER_INFO", key = {"u.id","u.name"})
    public void updateUserInfo(@Param("u") UserInfo userInfoBO) {
    }
}
