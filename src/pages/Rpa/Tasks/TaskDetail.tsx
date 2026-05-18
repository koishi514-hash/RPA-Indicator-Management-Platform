import React, { useEffect, useState } from 'react';
import { Descriptions, Spin, Button, message, Tag, Card, Typography } from 'antd';
import { useNavigate, useParams } from '@umijs/max';
import { getTaskDetail } from '@/services/task';

const { Text } = Typography;

// 状态映射表
const statusMap: Record<number, { text: string; color: string }> = {
    0: { text: '待执行', color: 'default' },
    1: { text: '执行中', color: 'gold' },
    2: { text: '成功', color: 'green' },
    3: { text: '失败', color: 'red' },
};

const TaskDetail: React.FC = () => {
    const { taskCode } = useParams<{ taskCode: string }>();
    const navigate = useNavigate();

    const [taskDetail, setTaskDetail] = useState<any>(null);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        if (!taskCode) {
            message.error('任务编码无效，返回列表');
            navigate('/Rpa/Tasks');
            return;
        }

        const fetchDetail = async () => {
            setLoading(true);
            try {
                const res = await getTaskDetail(taskCode);
                setTaskDetail(res.data);
            } catch (error) {
                console.error('获取详情失败:', error);
                message.error('获取任务详情失败，请检查网络或接口');
            } finally {
                setLoading(false);
            }
        };

        fetchDetail();
    }, [taskCode, navigate]);

    const handleBack = () => {
        navigate(-1);
    };

    // 计算执行时长
    const calculateDuration = (startTime: string, endTime: string) => {
        if (!startTime || !endTime) return '-';
        const start = new Date(startTime).getTime();
        const end = new Date(endTime).getTime();
        return `${Math.round((end - start) / 1000)}秒`;
    };

    return (
        <div style={{ padding: '24px', background: '#f5f5f5', minHeight: 'calc(100vh - 64px)' }}>
            {/* 页面头部 */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <Button type="text" icon={<span style={{ fontSize: 18 }}>←</span>} onClick={handleBack} />
                    <h2 style={{ margin: 0, fontSize: 20, fontWeight: 600 }}>任务详情</h2>
                    <span style={{ color: '#666', fontSize: 14 }}>任务编码：{taskCode}</span>
                </div>
                <Button type="default" onClick={handleBack}>返回列表</Button>
            </div>

            <Spin spinning={loading} tip="数据加载中..." size="large">
                {taskDetail ? (
                    <>
                        {/* 1. 基本信息区 */}
                        <Card style={{ marginBottom: 16 }}>
                            <Descriptions
                                bordered
                                column={2}
                                labelStyle={{
                                    fontWeight: 600,
                                    width: 180,
                                    backgroundColor: '#fafafa',
                                    padding: '12px 16px'
                                }}
                                contentStyle={{ padding: '12px 16px' }}
                            >
                                <Descriptions.Item label="任务编码">
                                    <Text copyable>{taskDetail.taskCode}</Text>
                                </Descriptions.Item>
                                <Descriptions.Item label="任务名称">
                                    {taskDetail.taskName}
                                </Descriptions.Item>
                                <Descriptions.Item label="纳税人识别号">
                                    <Text copyable>{taskDetail.taxNo}</Text>
                                </Descriptions.Item>
                                <Descriptions.Item label="企业名称">
                                    {taskDetail.enterpriseName}
                                </Descriptions.Item>
                                <Descriptions.Item label="流程编码">
                                    {taskDetail.processCode}
                                </Descriptions.Item>
                                <Descriptions.Item label="机器人编码">
                                    {taskDetail.robotCode}
                                </Descriptions.Item>
                                <Descriptions.Item label="任务状态">
                                    <Tag color={statusMap[taskDetail.status]?.color || 'default'}>
                                        {statusMap[taskDetail.status]?.text || '未知状态'}
                                    </Tag>
                                </Descriptions.Item>
                                <Descriptions.Item label="创建时间">
                                    {taskDetail.createTime}
                                </Descriptions.Item>
                                <Descriptions.Item label="开始时间">
                                    {taskDetail.startTime || '-'}
                                </Descriptions.Item>
                                <Descriptions.Item label="结束时间">
                                    {taskDetail.endTime || '-'}
                                </Descriptions.Item>
                            </Descriptions>
                        </Card>

                        {/* 2. 执行记录区 */}
                        <Card title="执行记录" style={{ marginBottom: 16 }}>
                            <Descriptions
                                bordered
                                column={2}
                                labelStyle={{
                                    fontWeight: 600,
                                    width: 180,
                                    backgroundColor: '#fafafa',
                                    padding: '12px 16px'
                                }}
                                contentStyle={{ padding: '12px 16px' }}
                            >
                                <Descriptions.Item label="开始时间">
                                    {taskDetail.startTime || '-'}
                                </Descriptions.Item>
                                <Descriptions.Item label="结束时间">
                                    {taskDetail.endTime || '-'}
                                </Descriptions.Item>
                                <Descriptions.Item label="执行时长">
                                    {calculateDuration(taskDetail.startTime, taskDetail.endTime)}
                                </Descriptions.Item>
                                <Descriptions.Item label="错误信息">
                                    {taskDetail.errorMsg || '-'}
                                </Descriptions.Item>
                            </Descriptions>
                        </Card>

                        {/* 3. 备注区 */}
                        <Card title="备注">
                            <div style={{ minHeight: 60, padding: '8px 0', whiteSpace: 'pre-wrap' }}>
                                {taskDetail.remark || '无'}
                            </div>
                        </Card>
                    </>
                ) : (
                    !loading && (
                        <Card>
                            <div style={{ textAlign: 'center', padding: '40px 0', color: '#999' }}>
                                暂无任务数据
                            </div>
                        </Card>
                    )
                )}
            </Spin>
        </div>
    );
};

export default TaskDetail;