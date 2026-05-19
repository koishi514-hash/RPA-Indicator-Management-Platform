package com.rbac.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rbac.common.model.dto.AddRobotRequest;
import com.rbac.common.model.dto.UpdateRobotRequest;
import com.rbac.common.response.Result;
import com.rbac.core.domain.entity.Robot;
import com.rbac.core.domain.mapper.RobotMapper;
import com.rbac.core.service.RobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机器人服务实现类
 */

@Slf4j
@Service
public class RobotServiceImpl extends ServiceImpl<RobotMapper, Robot> implements RobotService {

    /**
     * 根据机器人ID获取机器人编码
     * @param id 机器人ID
     * @return 机器人编码
     */
    @Override
    public String getRobotCodeById(Long id) {
        Robot robot = baseMapper.selectById(id);
        if (robot == null) {
            return "无该数据";
        }
        return robot.getRobotCode();
    }

    /**
     * 根据机器人编码获取机器人ID
     * @param robotCode 机器人编码
     * @return 机器人ID
     */
    @Override
    public Long getRobotIdByCode(String robotCode) {
        Robot robot = baseMapper.selectOne(new LambdaQueryWrapper<Robot>().eq(Robot::getRobotCode, robotCode));
        if (robot == null) {
            return null;
        }
        return robot.getId();
    }

    /**
     * 获取机器人列表
     * @param robotName 机器人名称
     * @param robotCode 机器人编码
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 机器人列表
     */
    @Override
    public Result<?> getRobotList(String robotName, String robotCode, Integer status, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<Robot> queryWrapper = new LambdaQueryWrapper<>();
        if (robotName != null && !robotName.isEmpty()) {
            queryWrapper.like(Robot::getRobotName, robotName);
        }
        if (robotCode != null && !robotCode.isEmpty()) {
            queryWrapper.like(Robot::getRobotCode, robotCode);
        }
        if (status != null) {
            queryWrapper.eq(Robot::getStatus, status);
        }

        // 分页查询
        Page<Robot> page = new Page<>(pageNum, pageSize);
        page = page(page, queryWrapper);

        // 统计机器人状态
        Long total = count(queryWrapper);
        Long online = count(new LambdaQueryWrapper<Robot>().eq(Robot::getStatus, 1));
        Long offline = count(new LambdaQueryWrapper<Robot>().eq(Robot::getStatus, 0));

        // 处理机器人列表数据
        List<Map<String, Object>> records = new ArrayList<>();
        List<Robot> robotList = page.getRecords();
        if (robotList == null || robotList.isEmpty()) {
            return Result.success("success", "无该数据");
        }
        for (Robot robot : robotList) {
            Map<String, Object> record = new HashMap<>();
            record.put("robotId", robot.getId());
            record.put("robotCode", robot.getRobotCode());
            record.put("robotName", robot.getRobotName());
            record.put("robotType", robot.getRobotType());
            record.put("status", robot.getStatus());
            record.put("currentTaskId", robot.getCurrentTaskId() != null ? robot.getCurrentTaskId() : "空闲");
            record.put("lastHeartbeat", robot.getLastHeartbeat());
            record.put("createTime", robot.getCreateTime());
            record.put("updateTime", robot.getUpdateTime());
            records.add(record);
        }
        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("statistics", Map.of(
                "total", total,
                "online", online,
                "offline", offline
        ));
        data.put("total", total);
        data.put("records", records);
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.success("success", data);
    }

    /**
     * 添加机器人
     * @param request 添加机器人请求
     * @return 添加结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> addRobot(AddRobotRequest request) {
        // 检查机器人编码是否已存在
        Robot existingRobot = baseMapper.selectOne(new LambdaQueryWrapper<Robot>()
                .eq(Robot::getRobotCode, request.getRobotCode()));
        if (existingRobot != null) {
            return Result.failed("机器人编码已存在");
        }

        try {
            // 创建新机器人
            Robot newRobot = new Robot();
            newRobot.setRobotCode(request.getRobotCode());
            newRobot.setRobotName(request.getRobotName());
            newRobot.setRobotType(request.getRobotType());
            newRobot.setDescription(request.getDescription());
            newRobot.setStatus(request.getStatus());
            newRobot.setCurrentTaskId(null);
            newRobot.setLastHeartbeat(LocalDateTime.now());
            newRobot.setCreateTime(LocalDateTime.now());
            newRobot.setUpdateTime(LocalDateTime.now());

            // 插入数据库
            baseMapper.insert(newRobot);

            return Result.success();
        } catch (Exception e) {
            log.error("添加机器人失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed("添加机器人失败");
        }
    }

    /**
     * 更新机器人
     * @param request 更新机器人请求
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateRobot(UpdateRobotRequest request) {
        try {
            // 检查机器人是否存在
            Robot robot = baseMapper.selectById(request.getRobotId());
            if (robot == null) {
                return Result.failed("机器人不存在");
            }

            // 更新机器人信息
            if (request.getRobotName() != null) {
                robot.setRobotName(request.getRobotName());
            }
            if (request.getRobotType() != null) {
                robot.setRobotType(request.getRobotType());
            }
            if (request.getDescription() != null) {
                robot.setDescription(request.getDescription());
            }
            if (request.getStatus() != null) {
                robot.setStatus(request.getStatus());
            }

            robot.setUpdateTime(LocalDateTime.now());

            // 保存更新
            boolean updated = updateById(robot);
            if (updated) {
                return Result.success("更新机器人成功");
            }
            return Result.failed(500, "更新机器人失败");
        } catch (Exception e) {
            log.error("更新机器人失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "更新机器人失败");
        }
    }

    /**
     * 获取机器人详情
     * @param robotCode 机器人ID
     * @return 机器人详情
     */
    @Override
    public Result<?> getRobotDetail(String robotCode) {
        Robot robot = baseMapper.selectOne(new LambdaQueryWrapper<Robot>()
                .eq(Robot::getRobotCode, robotCode));
        if (robot == null) {
            return Result.failed("机器人不存在");
        }
        return Result.success(robot);
    }

    /**
     * 删除机器人
     * @param robotCode 机器人编码
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteRobot(String robotCode) {
        try {
            Robot robot = baseMapper.selectOne(new LambdaQueryWrapper<Robot>()
                    .eq(Robot::getRobotCode, robotCode));
            if (robot == null) {
                return Result.failed("机器人不存在");
            }

            // 删除机器人
            boolean deleted = removeById(robot.getId());
            if (deleted) {
                return Result.success("删除机器人成功");
            } else {
                return Result.failed(500, "删除机器人失败");
            }
        } catch (Exception e) {
            log.error("删除机器人失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "删除机器人失败");
        }
    }

    /**
     * 更新机器人当前任务ID
     * @param robotCode 机器人编码
     * @param currentTaskId 当前任务ID
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateCurrentTaskId(String robotCode, String currentTaskId) {
        try {
            // 使用UpdateWrapper来明确指定要更新的字段，确保null值能够被正确更新
            UpdateWrapper<Robot> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("robot_code", robotCode)
                    .set("current_task_id", currentTaskId)
                    .set("update_time", LocalDateTime.now());

            // 执行更新
            boolean updated = update(updateWrapper);
            if (updated) {
                return Result.success("更新机器人当前任务ID成功");
            } else {
                return Result.failed(500, "更新机器人当前任务ID失败");
            }
        } catch (Exception e) {
            log.error("更新机器人当前任务ID失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "更新机器人当前任务ID失败");
        }
    }

    /**
     * 更新机器人最后心跳时间
     * @param robotCode 机器人编码
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateLastHeartbeat(String robotCode) {
        try {
            // 使用UpdateWrapper来更新最后心跳时间
            UpdateWrapper<Robot> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("robot_code", robotCode)
                    .set("last_heartbeat", LocalDateTime.now())
                    .set("update_time", LocalDateTime.now());

            // 执行更新
            boolean updated = update(updateWrapper);
            if (updated) {
                return Result.success("更新机器人最后心跳时间成功");
            } else {
                return Result.failed(500, "更新机器人最后心跳时间失败");
            }
        } catch (Exception e) {
            log.error("更新机器人最后心跳时间失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failed(500, "更新机器人最后心跳时间失败");
        }
    }
}
