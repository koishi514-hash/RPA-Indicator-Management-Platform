package com.rbac.api.controller;

import com.rbac.common.model.dto.AddRobotRequest;
import com.rbac.common.model.dto.UpdateRobotRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.RobotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 机器人管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/system/robots")
@Tag(name = "机器人管理", description = "机器人相关接口")
public class RobotController {

    private final RobotService robotService;

    /**
     * 获取机器人列表
     * @param robotName 机器人名称
     * @param robotCode 机器人编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 机器人列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取机器人列表", description = "根据机器人名称、机器人编码、状态查询机器人列表，支持分页查询")
    public Result<?> getRobotList(
            @RequestParam(required = false) String robotName,
            @RequestParam(required = false) String robotCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return robotService.getRobotList(robotName, robotCode, status, pageNum, pageSize);
    }

    /**
     * 添加机器人
     * @param request 添加机器人请求
     * @return 添加结果
     */
    @PostMapping("/create")
    @Operation(summary = "添加机器人", description = "根据添加机器人请求添加机器人")
    public Result<?> addRobot(@RequestBody AddRobotRequest request) {
        return robotService.addRobot(request);
    }

    /**
     * 更新机器人
     * @param request 更新机器人请求
     * @return 更新结果
     */
    @PostMapping("/update")
    @Operation(summary = "更新机器人", description = "根据更新机器人请求更新机器人")
    public Result<?> updateRobot(@RequestBody UpdateRobotRequest request) {
        return robotService.updateRobot(request);
    }

    /**
     * 获取机器人详情
     * @param robotCode 机器人编码
     * @return 机器人详情
     */
    @GetMapping("/detail/{robotCode}")
    @Operation(summary = "获取机器人详情", description = "根据机器人编码查询机器人详情")
    public Result<?> getRobotDetail(@PathVariable String robotCode) {
        return robotService.getRobotDetail(robotCode);
    }

    /**
     * 删除机器人
     * @param robotCode 机器人编码
     * @return 删除结果
     */
    @DeleteMapping("/delete/{robotCode}")
    @Operation(summary = "删除机器人", description = "根据机器人编码删除机器人")
    public Result<?> deleteRobot(@PathVariable String robotCode) {
        return robotService.deleteRobot(robotCode);
    }

}
