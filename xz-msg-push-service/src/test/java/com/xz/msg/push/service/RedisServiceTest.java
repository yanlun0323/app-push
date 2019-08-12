package com.xz.msg.push.service;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.xz.msg.push.entity.User;
import com.xz.msg.push.service.redis.RedisService;


public class RedisServiceTest extends BasicTestRunner{

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
	@Resource(name = "JSONRedisCache")
    private RedisService redisService;;

    @Test
    public void test() throws Exception {
        stringRedisTemplate.opsForValue().set("Coder", "Yan");
        
        System.out.println(stringRedisTemplate.opsForValue().get("Coder"));
        
        Assert.assertEquals("Yan", stringRedisTemplate.opsForValue().get("Coder"));
    }
    
    @Test
    public void testObj() throws Exception {
        User defaultUser = new User();
        defaultUser.setAppkey("Coder.Yan");
        redisService.save("User:Coder.Yan", defaultUser);
        
        User user = redisService.read("User:Coder.Yan", User.class);
        System.out.println(user);
    }
}