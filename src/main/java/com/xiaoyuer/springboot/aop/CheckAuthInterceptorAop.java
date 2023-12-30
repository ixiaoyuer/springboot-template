package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.CheckAuth;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.enums.UserRoleEnum;
import com.xiaoyuer.springboot.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 检查身份验证拦截器类
 * 该类用于检查用户身份验证，并确保用户具有必要的权限。
 * 实现了对 `CheckAuth` 注解和字符串类型的身份验证要求的处理。
 *
 * @author 小鱼儿
 * @date 2023/12/31 00:11:45
 */
@Component("CheckAuthInterceptor")
public class CheckAuthInterceptorAop {

    @Autowired
    private UserService userService;

    /**
     * 执行身份验证拦截器
     *
     * @param checkAuth 身份验证注解或字符串
     * @throws BusinessException 如果身份验证失败，则抛出业务异常
     */
    public void doInterceptor(Object checkAuth) throws BusinessException {
        String mustRole;

        // 判断传入的身份验证类型，并获取必要的角色信息
        if (checkAuth instanceof CheckAuth) {
            mustRole = ((CheckAuth) checkAuth).mustRole();
        } else if (checkAuth instanceof String) {
            mustRole = (String) checkAuth;
        } else {
            // 如果传入的类型不是 CheckAuth 或 String，则抛出系统错误异常
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        // 获取当前请求的 HttpServletRequest
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 检查用户是否具有必要的权限
        if (StringUtils.isNotBlank(mustRole)) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            String userRole = loginUser.getUserRole();

            // 如果用户被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            // 如果需要管理员权限，但用户不是管理员，拒绝
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
        }
    }

}