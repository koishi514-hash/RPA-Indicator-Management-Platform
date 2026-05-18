import { Button, Card, DatePicker, Descriptions, Form, Input, Modal, Select, Space, Table, Tag } from 'antd';
import React, { useEffect, useState } from 'react';
import { getExecutionDetail, listExecutions } from '@/services/execution';
import { useNavigate } from '@umijs/max';

const ExecutionRecord = () => {
    const [form] = Form.useForm();
    const [data, setData] = useState([]);
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
        total: 0,
    });
    // 添加状态
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [executionDetail, setExecutionDetail] = useState<any>(null);
    const navigate = useNavigate();

    // 添加查看详情函数
    const handleViewDetail = async (record: any) => {
        const res = await getExecutionDetail(record.executionId);
        setExecutionDetail(res.data);
        setDetailModalVisible(true);
    };

    // 点击任务编码跳转到任务详情页
    const goToTaskDetail = (taskCode: string) => {
        navigate(`/Rpa/Tasks/detail/${taskCode}`); // 路径和你之前的路由完全一致
    };

    const statusMap: Record<number, { text: string; color: string }> = {
        0: { text: '执行中', color: 'gold' },
        1: { text: '成功', color: 'green' },
        2: { text: '失败', color: 'red' },
    };

    const loadData = async (pageNum = 1, pageSize = 10) => {
        const values = form.getFieldsValue();
        const params: any = {
            pageNum,
            pageSize,
        };
        if (values.taskCode) params.taskCode = values.taskCode;
        if (values.status !== undefined && values.status !== '') params.status = values.status;
        if (values.executionTime && values.executionTime.length === 2) {
            params.startTime = values.executionTime[0].format('YYYY-MM-DDTHH:mm:ss');
            params.endTime = values.executionTime[1].format('YYYY-MM-DDTHH:mm:ss');
        }
        const res = await listExecutions(params);
        setData(res.data.records || []);
        setPagination({
            current: pageNum,
            pageSize: pageSize,
            total: res.data.records.total,
        });
    };

    useEffect(() => {
        loadData();
    }, []);

    const handleSearch = () => {
        loadData(1, pagination.pageSize);
    };

    const handleReset = () => {
        form.resetFields();
        loadData(1, pagination.pageSize);
    };

    const handleTableChange = (pagination: any) => {
        loadData(pagination.current, pagination.pageSize);
    };

    const columns = [
        { title: '序号', key: 'index', width: 80, render: (_: any, __: any, index: number) => (pagination.current - 1) * pagination.pageSize + index + 1 },
        { title: '执行ID', dataIndex: 'executionId', key: 'executionId' },
        {
            title: '任务编码',
            dataIndex: 'taskCode',
            key: 'taskCode',
            render: (text: string) => (
                <a onClick={() => goToTaskDetail(text)} style={{ color: '#1890ff' }}>
                    {text}
                </a>
            ),
        },
        { title: '流程编码', dataIndex: 'processCode', key: 'processCode' },
        { title: '机器人编码', dataIndex: 'robotCode', key: 'robotCode' },
        {
            title: '执行状态',
            dataIndex: 'status',
            key: 'status',
            render: (status: number) => {
                const item = statusMap[status] || { text: '未知', color: 'default' };
                return <Tag color={item.color}>{item.text}</Tag>;
            },
        },
        { title: '开始时间', dataIndex: 'startTime', key: 'startTime' },
        { title: '结束时间', dataIndex: 'endTime', key: 'endTime', render: (text: string) => text || '-' },
        {
            title: '操作',
            key: 'operation',
            render: (_: any, record: any) => (
                <Space size={16}>
                    <a onClick={() => handleViewDetail(record)}>查看详情</a>
                </Space>
            ),
        },
    ];

    return (
        <>
            <h1 style={{ margin: 0, marginBottom: 24, fontSize: 28, fontWeight: 600 }}>执行记录</h1>

            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="taskCode" label="任务编码">
                        <Input placeholder="请输入" style={{ width: 200 }} />
                    </Form.Item>
                    <Form.Item name="status" label="执行状态">
                        <Select placeholder="请选择" style={{ width: 180 }} allowClear>
                            <Select.Option value={0}>执行中</Select.Option>
                            <Select.Option value={1}>成功</Select.Option>
                            <Select.Option value={2}>失败</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="executionTime" label="执行时间">
                        <DatePicker.RangePicker
                            style={{ width: 360 }}
                            showTime
                            placeholder={['开始时间', '结束时间']}
                            format="YYYY-MM-DD HH:mm:ss"
                        />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" onClick={handleSearch}>查询</Button>
                        <Button onClick={handleReset} style={{ marginLeft: 8 }}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>
            {/* 添加详情弹窗 */}
            <Modal
                title="执行记录详情"
                open={detailModalVisible}
                onCancel={() => {
                    setDetailModalVisible(false);
                    setExecutionDetail(null);
                }}
                footer={[
                    <Button key="close" onClick={() => {
                        setDetailModalVisible(false);
                        setExecutionDetail(null);
                    }}>
                        关闭
                    </Button>
                ]}
                width={800}
            >
                {executionDetail && (
                    <>
                        <Descriptions column={2} bordered style={{ marginBottom: 24 }}>
                            <Descriptions.Item label="执行ID">{executionDetail.executionId}</Descriptions.Item>
                            <Descriptions.Item label="任务编码">{executionDetail.taskCode}</Descriptions.Item>
                            <Descriptions.Item label="流程编码">{executionDetail.processCode}</Descriptions.Item>
                            <Descriptions.Item label="机器人编码">{executionDetail.robotCode}</Descriptions.Item>
                            <Descriptions.Item label="执行状态">
                                <Tag color={
                                    executionDetail.status === 0 ? 'gold' :
                                        executionDetail.status === 1 ? 'green' : 'red'
                                }>
                                    {executionDetail.status === 0 ? '执行中' :
                                        executionDetail.status === 1 ? '成功' : '失败'}
                                </Tag>
                            </Descriptions.Item>
                            <Descriptions.Item label="执行时长">{executionDetail.duration}秒</Descriptions.Item>
                            <Descriptions.Item label="开始时间">{executionDetail.startTime}</Descriptions.Item>
                            <Descriptions.Item label="结束时间">{executionDetail.endTime || '-'}</Descriptions.Item>
                            {executionDetail.errorMsg && (
                                <Descriptions.Item label="错误信息" span={2}>
                                    <span style={{ color: 'red' }}>{executionDetail.errorMsg}</span>
                                </Descriptions.Item>
                            )}
                        </Descriptions>

                        <h4>执行步骤</h4>
                        <Table
                            dataSource={executionDetail.stepLogs}
                            rowKey="stepName"
                            pagination={false}
                            columns={[
                                { title: '步骤名称', dataIndex: 'stepName', key: 'stepName' },
                                { title: '步骤类型', dataIndex: 'stepType', key: 'stepType' },
                                { title: '执行时间', dataIndex: 'executeTime', key: 'executeTime' },
                                {
                                    title: '输出',
                                    dataIndex: 'output',
                                    key: 'output',
                                    render: (text: string) => (
                                        <div style={{ maxWidth: 300, wordBreak: 'break-all' }}>
                                            {text || '-'}
                                        </div>
                                    )
                                },
                            ]}
                        />
                    </>
                )}
            </Modal>

            <Table
                columns={columns}
                dataSource={data}
                rowKey="executionId"
                pagination={pagination}
                onChange={handleTableChange}
            />
        </>
    );
};

export default ExecutionRecord;