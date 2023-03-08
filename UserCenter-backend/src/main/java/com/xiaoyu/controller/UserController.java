package com.xiaoyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaoyu.model.domain.User;
import com.xiaoyu.model.request.UserLoginRequest;
import com.xiaoyu.model.request.UserRegisterRequest;
import com.xiaoyu.service.UserService;
import org.apache.commons.lang3.StringUtils;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaoyu.constant.UserConstant.ADMIN_ROLE;
import static com.xiaoyu.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/7 13:17
 * 用户接口
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.register(userAccount, userPassword, checkPassword);
    }


    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request );
    }


    /**
     * 根据id来删除用户(逻辑删除!!!)
     * @param userId 用户id
     * @return 返回删除是否完成
     */
    @GetMapping("/delete")
    public boolean deleteUserById(@RequestBody long userId, HttpServletRequest request) {

        // 对用户的权限进行鉴别(鉴权)
        if (isAdmin(request)) {
            return false;
        }

        if (userId <= 0) {
            return false;
        }
        return userService.removeById(userId);
    }


    @GetMapping("/search")
    public List<User> selectUserList(String username, HttpServletRequest request) {

        if (isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (username != null) {
            userQueryWrapper.like("username", username);
        }
        List<User> list = userService.list(userQueryWrapper);

        return list.stream().map( user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }


    /**
     * 鉴别用户是否是管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }

}
