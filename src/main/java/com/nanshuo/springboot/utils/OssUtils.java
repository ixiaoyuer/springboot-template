package com.nanshuo.springboot.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.nanshuo.springboot.config.OssClientConfig;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * oss 对象存储操作
 *
 * @author nanshuo
 * @date 2024/01/26 13:26:04
 */
@Component
public class OssUtils {

    private final OssClientConfig cosClientConfig;
    private final OSS ossClient;
    public OssUtils(OssClientConfig cosClientConfig, OSS ossClient) {
        this.cosClientConfig = cosClientConfig;
        this.ossClient = ossClient;
    }

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     * @return {@code PutObjectResult}
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return {@code PutObjectResult}
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return ossClient.putObject(putObjectRequest);
    }
}
