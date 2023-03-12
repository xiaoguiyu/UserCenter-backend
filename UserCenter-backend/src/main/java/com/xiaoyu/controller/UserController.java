package com.xiaoyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaoyu.common.BaseResponse;
import com.xiaoyu.common.ErrorCode;
import com.xiaoyu.common.ResultUtils;
import com.xiaoyu.exception.CustomException;
import com.xiaoyu.model.domain.User;
import com.xiaoyu.model.request.UserLoginRequest;
import com.xiaoyu.model.request.UserRegisterRequest;
import com.xiaoyu.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        assert userRegisterRequest != null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new CustomException(ErrorCode.NULL_ERROR);
        }
        long register = userService.register(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(register);
    }


    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new CustomException(ErrorCode.NULL_ERROR, "用户登录信息为空!!");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new CustomException(ErrorCode.NULL_ERROR, "用户登录信息为空!!");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销接口
     * @return 注销是否成功的状态码
     */
    @PostMapping("/logout")
    private BaseResponse<Integer> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request));
    }


    /**
     * 根据id来删除用户(逻辑删除!!!)
     * @param userId 用户id
     * @return 返回删除是否完成
     */
    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteUserById(@RequestBody long userId, HttpServletRequest request) {

        // 对用户的权限进行鉴别(鉴权)
        if (isAdmin(request)) {
            throw new CustomException(ErrorCode.NO_AUTH, "没有管理员权限!!");
        }

        if (userId <= 0) {
            throw new CustomException(ErrorCode.PARAMS_ERROR, "用户id不正确!!");
        }
        return ResultUtils.success(userService.removeById(userId));
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        // 查询用户当前的状态信息(为什么不直接返回session的信息? 考虑到用户的信息在登录后可能发生变化, 查数据库是安全的!!!)
        User currentUser = userService.getById(user.getId());
        User safetyUser = userService.getSafetyUser(currentUser);
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> selectUserList(String username, HttpServletRequest request) {

        if (isAdmin(request)) {
            throw new CustomException(ErrorCode.NO_AUTH, "没有管理员权限!!");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (username != null) {
            userQueryWrapper.like("username", username);
        }
        List<User> list = userService.list(userQueryWrapper);

        List<User> listUser = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(listUser);
    }


    /**
     * 鉴别用户是否是管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }

}
