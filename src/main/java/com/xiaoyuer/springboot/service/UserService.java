package com.xiaoyuer.springboot.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoyuer.springboot.model.dto.user.UserLoginDto;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.vo.user.UserLoginVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:29:48
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册信息
     * @return 注册成功的用户id
     */
    long userRegister(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     *
     * @param userLoginDto 用户登录dto
     * @param request      请求
     * @return {@code UserLoginVO}
     */
    UserLoginVO userLogin(HttpServletRequest request, UserLoginDto userLoginDto);

    /**
     * 获取登录用户
     *
     * @param request 请求
     * @return {@code User}
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取登录用户vo
     *
     * @param user 用户
     * @return {@code UserLoginVO}
     */
    UserLoginVO getLoginUserVO(User user);

    /**
     * 按id获取用户缓存
     *
     * @param userId 用户id
     * @return {@code User}
     */
    User getUserCacheById(Long userId);

    /**
     * 将用户保存到缓存
     *
     * @param user 用户
     */
    void saveUserToCache(User user);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@code Boolean}
     */
    Boolean userLogout(HttpServletRequest request);
}
