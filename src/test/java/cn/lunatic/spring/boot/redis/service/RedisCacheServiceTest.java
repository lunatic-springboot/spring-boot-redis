package cn.lunatic.spring.boot.redis.service;

import cn.lunatic.spring.boot.redis.Application;
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

    @Test
    public void redisString() throws InterruptedException {
        redisCacheService.set("KK","123",1);
        Thread.sleep(2000);
        System.out.println(redisCacheService.get("KK"));
    }
}