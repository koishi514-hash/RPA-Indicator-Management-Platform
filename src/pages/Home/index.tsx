import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Progress, Table, Tag, Button, message } from 'antd';
import { ArrowUpOutlined, PlusOutlined, RightOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate, useModel } from '@umijs/max';
import { listTasks } from '@/services/task';
import { listRobots } from '@/services/robots';
import { listProcesses } from '@/services/processes';
import { getCollectionList } from '@/services/collection';

const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { initialState } = useModel('@@initialState');
  const [loading, setLoading] = useState(false);


  // ====================== 权限逻辑（已按你真实数据修复）======================
  const { permissionRoutes, userInfo, isLogin } = initialState || {};

  // 1. 权限判断函数
  const hasPermission = (path: string): boolean => {
    if (!isLogin || !permissionRoutes) return false;

    // 过滤掉无效值
    const validRoutes = permissionRoutes.filter(Boolean);

    // 严格精确匹配（不使用前缀匹配）
    return validRoutes.some(permPath => {
      // 只进行精确匹配
      return path === permPath;
    });
  };

  // 2. 安全跳转函数（统一拦截）
  const safeNavigate = (path: string) => {
    if (hasPermission(path)) {
      navigate(path);
    } else {
      message.warning('暂无访问权限，请联系管理员');
    }
  };
  // ==========================================================================

  const [stats, setStats] = useState({
    totalTasks: 0,
    taskGrowth: 0,
    totalRobots: 0,
    onlineRobots: 0,
    totalProcesses: 0,
    enabledProcesses: 0,
    totalData: 0,
    todayData: 0,
  });
  const [taskStatusStats, setTaskStatusStats] = useState({ running: 0, pending: 0, completed: 0, failed: 0 });
  const [recentTasks, setRecentTasks] = useState([]);
  const [systemInfo, setSystemInfo] = useState({
    version: 'v1.0.0',
    uptime: '15天8小时',
    dataSource: '东方财富网',
    lastUpdate: new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-'),
  });

  const statusConfig: Record<number, { text: string; color: string }> = {
    0: { text: '待执行', color: 'default' },
    1: { text: '运行中', color: 'processing' },
    2: { text: '成功', color: 'success' },
    3: { text: '失败', color: 'error' },
  };

  // 通用数据解析函数
  const parseResponse = (res: any) => {
    if (res?.data?.data) return res.data.data;
    if (res?.data) return res.data;
    return res || {};
  };


  const loadData = async () => {
    setLoading(true);
    try {
      const [tasksRes, robotsRes, processesRes, collectionRes] = await Promise.all([
        listTasks({ pageNum: 1, pageSize: 100 }),
        listRobots({ pageNum: 1, pageSize: 100 }),
        listProcesses({ pageNum: 1, pageSize: 100 }),
        getCollectionList({ pageNum: 1, pageSize: 1000 }),
      ]);

      const tasksData = parseResponse(tasksRes);
      const tasks = tasksData.records || [];
      const totalTasks = tasksData.total ?? tasks.length;

      const robotsData = parseResponse(robotsRes);
      const robots = robotsData.records || [];
      const totalRobots = robotsData.total ?? robots.length;

      const processesData = parseResponse(processesRes);
      const processes = processesData.records || [];
      const totalProcesses = processesData.total ?? processes.length;

      const collectionData = parseResponse(collectionRes);
      const collectionList = collectionData.records || [];
      const totalData = collectionData.total ?? collectionData.statistics?.totalCollection ?? 0;

      // 任务状态统计
      const running = tasks.filter((t: any) => t.status === 1).length;
      const pending = tasks.filter((t: any) => t.status === 0).length;
      const completed = tasks.filter((t: any) => t.status === 2).length;
      const failed = tasks.filter((t: any) => t.status === 3).length;

      // 机器人/流程统计
      const onlineRobots = robots.filter((r: any) =>
        r.status === 1 || r.status === 'online' || r.status === '1'
      ).length;
      const enabledProcesses = processes.filter((p: any) =>
        p.status === 1 || p.status === 'enabled' || p.status === '1'
      ).length;

      // 获取今日日期 yyyy-MM-dd
      const today = new Date().toISOString().split('T')[0];

      // 今日新增任务
      const taskGrowth = tasks.filter((t: any) => {
        if (!t.createTime) return false;
        return t.createTime.startsWith(today);
      }).length;

      // 今日采集数据
      const todayData = collectionList.filter((item: any) => {
        if (!item.collectionTime) return false;
        return item.collectionTime.startsWith(today);
      }).length;

      // 最后更新时间
      let lastUpdate = systemInfo.lastUpdate;
      if (tasks.length > 0) {
        const latestTask = tasks.reduce((latest: any, current: any) => {
          const latestTime = latest.createTime ? new Date(latest.createTime).getTime() : 0;
          const currentTime = current.createTime ? new Date(current.createTime).getTime() : 0;
          return currentTime > latestTime ? current : latest;
        });
        if (latestTask.createTime) {
          lastUpdate = latestTask.createTime.replace('T', ' ');
        }
      }

      setStats({
        totalTasks,
        taskGrowth,
        totalRobots,
        onlineRobots,
        totalProcesses,
        enabledProcesses,
        totalData,
        todayData,
      });

      setSystemInfo(prev => ({ ...prev, lastUpdate }));
      setTaskStatusStats({ running, pending, completed, failed });
      setRecentTasks(tasks.slice(0, 5));

    } catch (error) {
      console.error('加载数据失败', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 6) return '夜深了';
    if (hour < 12) return '早上好';
    if (hour < 18) return '下午好';
    return '晚上好';
  };

  const getDateString = () => {
    const d = new Date();
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    return `${year}年${month}月${day}日 ${weekdays[d.getDay()]}`;
  };

  const quickActions = [
    { title: '创建任务', description: '快速创建新的RPA任务', icon: '📋', color: '#1890ff', path: '/rpa/tasks/list' },
    { title: '流程定义', description: '定义和管理RPA流程', icon: '⚙️', color: '#52c41a', path: '/rpa/processes/list' },
    { title: '机器人列表', description: '查看和管理机器人', icon: '🤖', color: '#722ed1', path: '/rpa/robots/list' },
    { title: '数据查询', description: '查询已处理的数据', icon: '🔍', color: '#fa8c16', path: '/rpa/data/query' },
  ];

  const recentTaskColumns: ColumnsType<any> = [
    { title: '任务编码', dataIndex: 'taskCode', key: 'taskCode', width: 160 },
    { title: '任务名称', dataIndex: 'taskName', key: 'taskName', width: 140 },
    { title: '企业名称', dataIndex: 'enterpriseName', key: 'enterpriseName', width: 140 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 100,
      render: (s) => {
        const config = statusConfig[s] || { text: s, color: 'default' };
        return <Tag color={config.color}>{config.text}</Tag>;
      }
    },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 170, render: (v) => v?.replace('T', ' ') || '-' },
  ];

  const totalTasksForProgress = taskStatusStats.running + taskStatusStats.pending + taskStatusStats.completed + taskStatusStats.failed;
  const completionRate = totalTasksForProgress > 0 ? Math.round((taskStatusStats.completed / totalTasksForProgress) * 100) : 0;

  // 导航链接（带权限判断）
  const renderNavLink = (path: string, text: React.ReactNode, showIcon: boolean = true) => {
    return (
      <a
        onClick={() => safeNavigate(path)}
        style={{
          color: hasPermission(path) ? '#1890ff' : '#999',
          cursor: hasPermission(path) ? 'pointer' : 'not-allowed',
        }}
      >
        {text}{showIcon && <RightOutlined />}
      </a>
    );
  };

  return (
    <div style={{ padding: 24, background: '#f0f2f5', minHeight: '100vh' }}>
      <div style={{ marginBottom: 24 }}>
        <h1 style={{ fontSize: 24, fontWeight: 600, marginBottom: 8 }}>
          {getGreeting()}，{userInfo?.realName || '系统管理员'}！
        </h1>
        <p style={{ color: '#666', fontSize: 14 }}>今天是{getDateString()}，系统运行正常</p>
      </div>

      <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} style={{ borderRadius: 8 }} loading={loading}>
            <Statistic title="总任务数" value={stats.totalTasks} valueStyle={{ fontSize: 32, fontWeight: 600 }} />
            <div style={{ marginTop: 8, color: '#52c41a', fontSize: 13 }}>
              <ArrowUpOutlined /> 今日新增 {stats.taskGrowth}
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} style={{ borderRadius: 8 }} loading={loading}>
            <Statistic title="机器人总数" value={stats.totalRobots} valueStyle={{ fontSize: 32, fontWeight: 600 }} />
            <div style={{ marginTop: 8, color: '#1890ff', fontSize: 13 }}>在线 {stats.onlineRobots} 台</div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} style={{ borderRadius: 8 }} loading={loading}>
            <Statistic title="流程总数" value={stats.totalProcesses} valueStyle={{ fontSize: 32, fontWeight: 600 }} />
            <div style={{ marginTop: 8, color: '#52c41a', fontSize: 13 }}>启用 {stats.enabledProcesses} 个</div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card bordered={false} style={{ borderRadius: 8 }} loading={loading}>
            <Statistic title="数据总量" value={stats.totalData} valueStyle={{ fontSize: 32, fontWeight: 600 }} />
            <div style={{ marginTop: 8, color: '#1890ff', fontSize: 13 }}>
              <ArrowUpOutlined /> 今日采集 {stats.todayData}
            </div>
          </Card>
        </Col>
      </Row>

      <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
        <Col xs={24} lg={12}>
          <Card
            title="任务状态概览"
            bordered={false}
            style={{ borderRadius: 8 }}
            extra={renderNavLink('/rpa/tasks/list', '查看详情 ')}
            loading={loading}
          >
            <Row gutter={[16, 24]}>
              <Col span={6} style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 36, fontWeight: 600, color: '#1890ff' }}>{taskStatusStats.running}</div>
                <div style={{ color: '#666', marginTop: 4 }}>运行中</div>
              </Col>
              <Col span={6} style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 36, fontWeight: 600, color: '#faad14' }}>{taskStatusStats.pending}</div>
                <div style={{ color: '#666', marginTop: 4 }}>待执行</div>
              </Col>
              <Col span={6} style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 36, fontWeight: 600, color: '#52c41a' }}>{taskStatusStats.completed}</div>
                <div style={{ color: '#666', marginTop: 4 }}>已完成</div>
              </Col>
              <Col span={6} style={{ textAlign: 'center' }}>
                <div style={{ fontSize: 36, fontWeight: 600, color: '#ff4d4f' }}>{taskStatusStats.failed}</div>
                <div style={{ color: '#666', marginTop: 4 }}>失败</div>
              </Col>
            </Row>
            <div style={{ marginTop: 24 }}>
              <Progress percent={completionRate} strokeColor="#52c41a" format={(p) => `完成率 ${p}%`} />
            </div>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="快捷入口" bordered={false} style={{ borderRadius: 8 }}>
            <Row gutter={[16, 16]}>
              {quickActions.map((item, index) => (
                <Col span={12} key={index}>
                  <Card
                    hoverable={hasPermission(item.path)}
                    style={{
                      background: '#fafafa',
                      borderRadius: 8,
                      cursor: hasPermission(item.path) ? 'pointer' : 'not-allowed',
                      opacity: hasPermission(item.path) ? 1 : 0.6,
                    }}
                    bodyStyle={{ padding: 16 }}
                    onClick={() => safeNavigate(item.path)}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                      <div style={{
                        width: 48, height: 48, borderRadius: 8,
                        background: item.color, display: 'flex',
                        alignItems: 'center', justifyContent: 'center',
                        fontSize: 24
                      }}>
                        {item.icon}
                      </div>
                      <div>
                        <div style={{ fontWeight: 600, marginBottom: 4 }}>{item.title}</div>
                        <div style={{ color: '#999', fontSize: 12 }}>{item.description}</div>
                      </div>
                    </div>
                  </Card>
                </Col>
              ))}
            </Row>
          </Card>
        </Col>
      </Row>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={16}>
          <Card
            title="最近任务"
            bordered={false}
            style={{ borderRadius: 8 }}
            extra={renderNavLink('/rpa/tasks/list', '查看全部 ')}
            loading={loading}
          >
            <Table
              columns={recentTaskColumns}
              dataSource={recentTasks}
              rowKey="taskId"
              pagination={false}
              size="small"
            />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="系统信息" bordered={false} style={{ borderRadius: 8 }}>
            <div style={{ padding: '8px 0' }}>
              <Row style={{ marginBottom: 16 }}>
                <Col span={12} style={{ color: '#666' }}>系统版本</Col>
                <Col span={12} style={{ textAlign: 'right' }}>{systemInfo.version}</Col>
              </Row>
              <Row style={{ marginBottom: 16 }}>
                <Col span={12} style={{ color: '#666' }}>运行时间</Col>
                <Col span={12} style={{ textAlign: 'right' }}>{systemInfo.uptime}</Col>
              </Row>
              <Row style={{ marginBottom: 16 }}>
                <Col span={12} style={{ color: '#666' }}>数据源</Col>
                <Col span={12} style={{ textAlign: 'right' }}>{systemInfo.dataSource}</Col>
              </Row>
              <Row>
                <Col span={12} style={{ color: '#666' }}>最后更新</Col>
                <Col span={12} style={{ textAlign: 'right' }}>{systemInfo.lastUpdate}</Col>
              </Row>
            </div>
            <div style={{ marginTop: 16 }}>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                block
                onClick={() => safeNavigate('/rpa/tasks/list')}
                disabled={!hasPermission('/rpa/tasks/list')}
              >
                创建任务
              </Button>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardPage;