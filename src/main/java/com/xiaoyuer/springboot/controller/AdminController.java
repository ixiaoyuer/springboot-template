package com.xiaoyuer.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckAuth;
import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.constant.UserConstant;
import com.xiaoyuer.springboot.exception.ThrowUtils;
import com.xiaoyuer.springboot.model.dto.user.UserAddDto;
import com.xiaoyuer.springboot.model.dto.user.UserQueryDto;
import com.xiaoyuer.springboot.model.dto.user.UserUpdateDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.vo.user.UserVO;
import com.xiaoyuer.springboot.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 *
 * @author 小鱼儿
 * @date 2024/01/23 20:48:22
 */
@Slf4j
@Api(tags = "管理员模块")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // domain 增删改查相关

    /**
     * 添加用户
     *
     * @param userAddDto 用户添加dto
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加用户", notes = "添加用户")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddDto userAddDto) {
        return ResultUtils.success(userService.addUser(userAddDto));
    }

    /**
     * 删除用户(管理员权限)
     *
     * @param userId 用户id
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除用户", notes = "删除用户")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> deleteUser(@RequestBody @ApiParam(value = "用户id", required = true) Long userId) {
        ThrowUtils.throwIf(!userService.removeById(userId), ErrorCode.OPERATION_ERROR, "删除用户失败,无该用户");
        return ResultUtils.success(userId);
    }

    /**
     * 修改用户信息
     *
     * @param userUpdateDto 用户更新dto
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateDto, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "修改用户信息失败,无该用户信息");
        return ResultUtils.success(user.getUserId());
    }

    /**
     * 按id获取用户
     *
     * @param userId 用户id
     * @return {@code BaseResponse<User>}
     */
    @GetMapping("/get")
    @ApiOperation(value = "按id获取用户", notes = "按id获取用户")
    @CheckAuth(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(@ApiParam(value = "用户id", required = true) @CheckParam(required = NumberConstant.TRUE_VALUE, nullErrorMsg = "用户id不能为空") Long userId) {
        User user = userService.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ResultUtils.success(user);
    }

    /**
     * 获取用户列表
     */
    @PostMapping("/list/page")
    @Check(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryDto userQueryDto) {
        long current = userQueryDto.getCurrent();
        long size = userQueryDto.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryDto));
        return ResultUtils.success(userPage);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id id
     * @return {@code BaseResponse<UserVO>}
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取包装类", notes = "根据 id 获取包装类")
    public BaseResponse<UserVO> getUserVOById(@CheckParam(required = NumberConstant.TRUE_VALUE, nullErrorMsg = "用户id不能为空") Long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 用户密码重置
     *
     * @param userId 用户id
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/password/reset")
    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @CheckAuth(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> userPasswordReset(@RequestBody @ApiParam(value = "用户id", required = true) @CheckParam(required = NumberConstant.TRUE_VALUE, nullErrorMsg = "用户id不能为空") Long userId) {
        return ResultUtils.success(userService.userPasswordResetByAdmin(userId));
    }

    // end domain 增删改查相关
}
