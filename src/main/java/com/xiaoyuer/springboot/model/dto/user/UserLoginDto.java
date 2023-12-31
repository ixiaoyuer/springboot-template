package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录dto
 *
 * @author 小鱼儿
 * @date 2024/01/04 18:58:48
 */
@Data
@ApiModel(value = "UserLoginDto", description = "用户登录信息DTO")
public class UserLoginDto implements Serializable {


    private static final long serialVersionUID = -5262836669010105900L;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(nullErrorMsg = "账号不能为空", minLength = 3, maxLength = 16, lenghtErrorMsg = "账号长度必须在3-16之间")
    private String userAccount;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(nullErrorMsg = "密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "密码长度必须在6-18之间")
    private String userPassword;

    /**
     * 图片验证码
     */
    @ApiModelProperty(value = "图片验证码", required = true)
    @CheckParam(nullErrorMsg = "图片验证码不能为空", regex = VerifyRegexEnums.IMAGE_CAPTCHA, regexErrorMsg = "图片验证码错误")
    private String imageCaptcha;
}
