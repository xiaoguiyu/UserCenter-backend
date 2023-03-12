package com.xiaoyu.service;
import java.util.Date;

import com.xiaoyu.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author xiaoyu
 * 用户服务测试
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testInsertUser() {

        User user = new User();
        user.setUsername("xiaoyu");
        user.setUserAccount("123");
        user.setAvatarUrl("https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("lin802366@gmail.com");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(0);
        user.setPlanetCode("");

        userService.save(user);
    }


    @Test
    void registerTest() {

        // 非空
        String userAccount = "";
        String userPassword = "";
        String checkPassword = "";
        String planetCode = "1";
        long result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);  // 断言

        // 账号长度判断
        userAccount = "xia";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // 相同账号测试
        userAccount = "xiaoyu";
        userPassword = "123456789";
        checkPassword = "123456789";
        result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);

        // 特殊符号测试
        userAccount = ".1234567788";
        userPassword = "xiaoyu12345";
        checkPassword = "xiaoyu12345";
        result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);


        // 密码不一致测试
        userAccount = "xiaoyu1234";
        userPassword = "xiaoyu12345";
        checkPassword = "xiaoyu123";
        result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertEquals(-1, result);


        // 正确测试
        userAccount = "xiaoyu123";
        userPassword = "xiaoyu666";
        checkPassword = "xiaoyu666";
        result = userService.register(userAccount, userPassword, checkPassword, planetCode);
        Assertions.assertTrue(result > 0);

    }


}