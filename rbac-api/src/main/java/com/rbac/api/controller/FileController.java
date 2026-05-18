package com.rbac.api.controller;

import com.rbac.common.model.dto.UpdateCurrentUserRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.SysUser;
import com.rbac.core.service.FileService;
import com.rbac.core.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system")
@Tag(name = "文件管理", description = "文件相关接口")
public class FileController {

    private final FileService fileService;
    private final SysUserService sysUserService;

    /**
     * 上传头像
     * @param file 上传的文件
     * @param session 会话
     * @return 头像访问路径
     */
    @PostMapping("profile/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像文件并返回访问路径, 仅支持上传图片文件, 上传后会更新用户信息中的头像URL")
    public Result<?> uploadAvatar(
            @Parameter(description = "要上传的头像文件", required = true)
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        // 获取当前用户名
        SysUser user = (SysUser) session.getAttribute("user");
        if (user == null) {
            return Result.failed(401, "未登录或token已过期");
        }

        // 调用服务层上传文件
        String avatarUrl = fileService.uploadAvatar(file, user.getUsername());

        // 更新用户信息中的头像URL
        UpdateCurrentUserRequest request = new UpdateCurrentUserRequest();
        request.setAvatarUrl(avatarUrl);
        // 调用服务层更新用户信息
        sysUserService.updateProfile(request, session);
        return Result.success(avatarUrl);
    }
}
