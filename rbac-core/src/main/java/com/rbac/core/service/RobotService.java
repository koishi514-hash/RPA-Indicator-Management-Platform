package com.rbac.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rbac.common.model.dto.AddRobotRequest;
import com.rbac.common.model.dto.UpdateRobotRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Robot;

/**
 * 机器人服务接口
 */

public interface RobotService extends IService<Robot> {

    /**
     * 根据机器人ID获取机器人编码
     * @param id 机器人ID
     * @return 机器人编码
     */
    String getRobotCodeById(Long id);

    /**
     * 根据机器人编码获取机器人ID
     * @param robotCode 机器人编码
     * @return 机器人ID
     */
    Long getRobotIdByCode(String robotCode);

    /**
     * 获取机器人列表
     * @param robotName 机器人名称
     * @param robotCode 机器人编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 机器人列表
     */
    Result<?> getRobotList(String robotName, String robotCode, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 添加机器人
     * @param request 添加机器人请求
     * @return 添加结果
     */
    Result<?> addRobot(AddRobotRequest request);

    /**
     * 更新机器人
     * @param request 更新机器人请求
     * @return 更新结果
     */
    Result<?> updateRobot(UpdateRobotRequest request);

    /**
     * 获取机器人详情
     * @param robotCode 机器人编码
     * @return 机器人详情
     */
    Result<?> getRobotDetail(String robotCode);

    /**
     * 删除机器人
     * @param robotCode 机器人编码
     * @return 删除结果
     */
    Result<?> deleteRobot(String robotCode);

    /**
     * 更新机器人当前任务ID
     * @param robotCode 机器人编码
     * @param currentTaskId 当前任务ID
     * @return 更新结果
     */
    Result<?> updateCurrentTaskId(String robotCode, String currentTaskId);

    /**
     * 更新机器人最后心跳时间
     * @param robotCode 机器人编码
     * @return 更新结果
     */
    Result<?> updateLastHeartbeat(String robotCode);

}
