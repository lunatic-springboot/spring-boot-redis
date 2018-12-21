package cn.lunatic.spring.boot.redis.service;

import cn.lunatic.spring.boot.redis.Application;
import cn.lunatic.spring.boot.redis.annotation.UserInfoBO;
import cn.lunatic.spring.boot.redis.annotation.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisCacheServiceTest {
    @Resource
    private RedisCacheService redisCacheService;

    @Resource
    private UserService userService;

    @Test
    public void redisString() throws InterruptedException {
        redisCacheService.set("KK","123",1);
        Thread.sleep(2000);
        System.out.println(redisCacheService.get("KK"));
    }

    @Test
    public void redisAnnotation() throws InterruptedException {
        userService.getUserInfo(UserInfoBO.builder().build());
        userService.getUserInfo(UserInfoBO.builder().id(1).name("张三").build());
        userService.getUserInfo(UserInfoBO.builder().id(1).name("张三").build());
        userService.updateUserInfo(UserInfoBO.builder().id(1).name("张三").build());
        userService.getUserInfo(UserInfoBO.builder().id(1).name("张三").build());
    }

}