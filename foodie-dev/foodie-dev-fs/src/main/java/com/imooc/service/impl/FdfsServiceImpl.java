package com.imooc.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.resource.FileResource;
import com.imooc.service.FdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FdfsServiceImpl implements FdfsService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FileResource fileResource;

    @Override
    public String upload(MultipartFile file, String fileExtName) throws Exception {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        String fullPath = storePath.getFullPath();
        return fullPath;
    }

    /*
    使用阿里云的oss 上传
     */
    @Override
    public String uploadOSS(MultipartFile file, String fileExtName, String userId) throws Exception {

        OSS ossClient = new OSSClientBuilder().build(fileResource.getEndpoint(),fileResource.getAccessKeyId(),fileResource.getBucketName());
        InputStream inputStream = file.getInputStream();
        String objectName = fileResource.getObjectName() + "/" + userId + "/" + userId + "."+fileExtName;
        ossClient.putObject(fileResource.getBucketName(),objectName,inputStream);
        ossClient.shutdown();
        return objectName;
    }

}
