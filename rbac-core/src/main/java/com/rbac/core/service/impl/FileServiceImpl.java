package com.rbac.core.service.impl;

import com.rbac.common.exception.BusinessException;
import com.rbac.core.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件服务实现类
 */

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.avatar.prefix}")
    private String avatarPrefix;

    /**
     * 上传头像
     * @param file 上传的文件
     * @param username 用户名
     * @return 头像访问路径
     */
    @Override
    public String uploadAvatar(MultipartFile file, String username) {
        // 检查文件
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("文件类型错误, 仅支持上传图片文件");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = null;
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 生成安全的用户名(替换特殊字符, 避免冲突)
        username = username.replaceAll("[^a-zA-Z0-9]", "_");
        // 生成唯一文件名
        String fileName = username + "_" + System.currentTimeMillis();
        if (suffix != null) {
            fileName += suffix;
        }

        try {
            // 创建存储路径
            String avatarDir = uploadPath + "avatar/";
            File dir = new File(avatarDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("创建头像目录失败: {}", avatarDir);
                    throw new BusinessException("创建头像目录失败");
                }
            }

            // 保存文件
            File dest = new File(avatarDir + fileName);
            file.transferTo(dest);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new BusinessException("上传文件失败");
        }
        // 生成访问路径
        return avatarPrefix + fileName;
    }
}
