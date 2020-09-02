package com.ponking.pblog.service.impl;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.ponking.pblog.service.IUploadService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author Peng
 * @date 2020/9/1--10:46
 **/
@Service
public class UploadServiceImpl implements IUploadService {


    /**
     * Endpoint以杭州为例，其它Region请按实际情况填写
     */
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket}")
    private String bucketName;
    /**
     * 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，
     * 请登录 https://ram.console.aliyun.com 创建RAM账号。
     */
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;


    /**
     *
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {

        String uploadUrl = null;

        try {
            // <yourObjectName>上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
//            String objectName = "image";

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）。
            InputStream inputStream = file.getInputStream();

            //文件名：uuid.扩展名

            //构建日期路径：avatar/2019/02/26/文件名
            String filePath = new DateTime().toString("yyyy/MM/dd");

            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString().replace("-", "");
            String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newName = fileName + fileType;
            String fileUrl = filePath + "/" + newName;

            ossClient.putObject(bucketName, fileUrl, inputStream);

            //获取url地址
            uploadUrl = "https://" + bucketName + "." + endpoint + "/" + fileUrl;

            // 关闭OSSClient。
            ossClient.shutdown();

            return uploadUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadUrl;
    }
}
