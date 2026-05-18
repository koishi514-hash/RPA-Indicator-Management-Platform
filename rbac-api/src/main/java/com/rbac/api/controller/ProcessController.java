package com.rbac.api.controller;

import com.rbac.common.model.dto.AddProcessRequest;
import com.rbac.common.model.dto.SaveProcessStepRequest;
import com.rbac.common.model.dto.UpdateProcessRequest;
import com.rbac.common.response.Result;
import com.rbac.core.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 流程管理控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/system/processes")
@Tag(name = "流程管理", description = "流程相关接口")
public class ProcessController {

    private final ProcessService processService;

    /**
     * 分页查询流程列表
     * @param processName 流程名称
     * @param processCode 流程编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 流程列表
     */
    @GetMapping("list")
    @Operation(summary = "分页查询流程列表", description = "根据流程名称、流程编码、状态分页查询流程列表")
    public Result<?> pageProcessList(
            @RequestParam(required = false) String processName,
            @RequestParam(required = false) String processCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        return processService.pageProcessList(processName, processCode, status, pageNum, pageSize);
    }

    /**
     * 创建流程
     * @param request 流程请求
     * @return 流程ID
     */
    @PostMapping("create")
    @Operation(summary = "创建流程", description = "根据流程请求创建流程")
    public Result<?> addProcess(@Validated @RequestBody AddProcessRequest request) {
        return processService.addProcess(request);
    }

    /**
     * 更新流程
     * @param request 流程请求
     * @return 更新结果
     */
    @PostMapping("update")
    @Operation(summary = "更新流程", description = "根据流程请求更新流程")
    public Result<?> updateProcess(@Validated @RequestBody UpdateProcessRequest request) {
        return processService.updateProcess(request);
    }

    /**
     * 根据流程编码获取流程详情
     * @param processCode 流程编码
     * @return 流程详情
     */
    @GetMapping("detail/{processCode}")
    @Operation(summary = "根据流程编码获取流程详情", description = "根据流程编码获取流程详情")
    public Result<?> getProcessDetails(@PathVariable String processCode) {
        return processService.getProcessDetails(processCode);
    }

    /**
     * 根据流程编码获取流程步骤列表
     * @param processCode 流程编码
     * @return 流程步骤列表
     */
    @GetMapping("step/list/{processCode}")
    @Operation(summary = "根据流程编码获取流程步骤列表", description = "根据流程编码获取流程步骤列表")
    public Result<?> getProcessStep(@PathVariable String processCode){
        return processService.getProcessStep(processCode);
    }

    /**
     * 保存流程步骤
     * @param request 保存流程步骤请求
     * @return 保存结果
     */
    @PostMapping("step/save")
    @Operation(summary = "保存流程步骤", description = "根据保存流程步骤请求保存流程步骤")
    public Result<?> saveProcessStep(@Validated @RequestBody SaveProcessStepRequest request){
        return processService.saveProcessStep(request);
    }

    /**
     * 删除流程
     * @param processCode 流程编码
     * @return 删除结果
     */
    @DeleteMapping("delete/{processCode}")
    @Operation(summary = "删除流程", description = "根据流程编码删除流程")
    public Result<?> deleteProcess(@PathVariable String processCode){
        return processService.deleteProcess(processCode);
    }


    /**
     * 根据流程编码获取流程步骤代码列表
     * @param processCode 流程编码
     * @return 流程步骤代码列表
     */
    @GetMapping("step/code/{processCode}")
    @Operation(summary = "根据流程编码获取流程步骤代码", description = "根据流程编码获取流程步骤代码")
    public Result<?> getProcessStepList(@PathVariable String processCode){
        return processService.getProcessStepList(processCode);
    }
}
