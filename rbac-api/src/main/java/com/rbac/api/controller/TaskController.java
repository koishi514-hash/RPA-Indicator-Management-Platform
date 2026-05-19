package com.rbac.api.controller;

import com.rbac.common.model.dto.AddTaskRequest;
import com.rbac.common.model.dto.UpdateTaskRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system")
@Tag(name = "任务管理", description = "任务相关接口")
public class TaskController {

    private final TaskService taskService;

    /**
     * 分页查询任务列表
     * @param taskCode 任务编码（模糊查询）
     * @param taskName 任务名称（模糊查询）
     * @param status 任务状态：0-待执行，1-执行中，2-成功，3-失败
     * @param startTime 开始时间（格式：`yyyy-MM-ddTHH:mm:ss`）
     * @param endTime 结束时间（格式：`yyyy-MM-ddTHH:mm:ss`）
     * @param pageNum 页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @return 任务列表
     */
    @GetMapping("/tasks/list")
    @Operation(summary = "分页查询任务列表", description = "根据查询参数分页查询任务列表")
    public Result<?> pageTaskList (
            @RequestParam(required = false) String taskCode,
            @RequestParam(required = false) String taskName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(taskService.pageTaskList(taskCode, taskName, status, startTime, endTime, pageNum, pageSize));
    }

    /**
     * 新增任务
     * @param request 新增任务请求
     * @return 新增结果
     */
    @PostMapping("/tasks/create")
    @Operation(summary = "新增任务", description = "根据添加任务请求参数添加任务到数据库")
    public Result<?> addTask(@Validated @RequestBody AddTaskRequest request) {
        return taskService.addTask(request);
    }

    /**
     * 更新任务
     * @param request 更新任务请求
     * @return 更新结果
     */
    @PostMapping("/tasks/update")
    @Operation(summary = "更新任务", description = "根据任务ID和任务更新请求参数更新任务信息")
    public Result<?> updateTask(@Validated @RequestBody UpdateTaskRequest request){
        return taskService.updateTask(request);
    }

    /**
     * 获取任务详情
     * @param taskCode 任务编码
     * @return 任务详情
     */
    @GetMapping("/tasks/detail/{taskCode}")
    @Operation(summary = "获取任务详情", description = "根据任务编码查询任务详情")
    public Result<?> getTaskDetail(@PathVariable String taskCode) {
        return taskService.getTaskDetail(taskCode);
    }

    /**
     * 删除任务
     * @param taskCode 任务编码
     * @return 删除结果
     */
    @DeleteMapping("/tasks/delete/{taskCode}")
    @Operation(summary = "删除任务", description = "根据任务编码删除任务")
    public Result<?> deleteTask(@PathVariable String taskCode) {
        return taskService.deleteTask(taskCode);
    }

    /**
     * 执行任务
     * @param taskCode 任务编码
     * @return 执行结果
     */
    @PostMapping("/tasks/execute/{taskCode}")
    @Operation(summary = "执行任务", description = "根据任务编码执行任务")
    public Result<?> executeTask(@Parameter(description = "任务编码", required = true) @PathVariable String taskCode) {
        return taskService.executeTask(taskCode);
    }
}
