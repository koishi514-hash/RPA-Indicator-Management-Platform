package com.rbac.core.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */

public interface FileService {

    /**
     * 上传文件（头像）
     * @param file 上传的文件
     * @param username 用户名(用于生成文件名)
     * @return 头像访问路径
     */
    String uploadAvatar(MultipartFile file, String username);
}
