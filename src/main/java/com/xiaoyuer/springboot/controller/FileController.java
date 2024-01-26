package com.xiaoyuer.springboot.controller;

import cn.hutool.core.io.FileUtil;
import com.xiaoyuer.springboot.annotation.CheckAuth;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.constant.FileConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.manager.OssManager;
import com.xiaoyuer.springboot.model.dto.file.UploadFileDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.enums.file.FileUploadTypeEnums;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件控制器
 *
 * @author 小鱼儿
 * @date 2024/01/26 13:27:14
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private final UserService userService;
    private final OssManager ossManager;

    public FileController(OssManager ossManager, UserService userService) {
        this.ossManager = ossManager;
        this.userService = userService;
    }

    /**
     * 上传文件
     * 文件上传
     *
     * @param multipartFile 多部分文件
     * @param request       请求
     * @param uploadFileDto 上传文件dto
     * @return {@code BaseResponse<String>}
     */
    @PostMapping("/upload")
    @CheckAuth(mustRole = "user")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileDto uploadFileDto, HttpServletRequest request) {
        String type = uploadFileDto.getType();
        FileUploadTypeEnums fileUploadTypeEnums =  FileUploadTypeEnums.getEnumByValue(type);
        if (fileUploadTypeEnums == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件的参数类型错误");
        }
        validFile(multipartFile, fileUploadTypeEnums);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = String.valueOf(System.currentTimeMillis());
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("%s/%s/%s", fileUploadTypeEnums.getValue(), loginUser.getUserId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            ossManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(FileConstant.OSS_HOST_ADDRESS + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param fileUploadBizEnum 业务类型
     * @param multipartFile     多部分文件
     */
    private void validFile(MultipartFile multipartFile, FileUploadTypeEnums fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadTypeEnums.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
