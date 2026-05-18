import { Button, Card, Form, Input, Select, Space, Table, Tag, Statistic, Row, Col, message, Radio, Modal, Descriptions } from 'antd';
import React, { useEffect, useState } from 'react';
import { createRobot, deleteRobot, getRobotDetail, listRobots, updateRobot } from '@/services/robots';

const RobotList = () => {
    const [form] = Form.useForm();
    const [data, setData] = useState([]);
    const [statistics, setStatistics] = useState({
        total: 0,
        online: 0,
        offline: 0,
    });
    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
        total: 0,
    });
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [addForm] = Form.useForm();
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editForm] = Form.useForm();
    const [currentRobot, setCurrentRobot] = useState<any>(null);

    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [robotDetail, setRobotDetail] = useState<any>(null);
    const [totalRobotCount, setTotalRobotCount] = useState(0);

    // 查看机器人详情
    const handleView = async (record: any) => {
        const res = await getRobotDetail(record.robotCode);
        setRobotDetail(res.data);
        setDetailModalVisible(true);
    };

    // 编辑机器人
    const handleEdit = async (record: any) => {
        const res = await getRobotDetail(record.robotCode);
        const detail = res.data;
        setCurrentRobot(detail);
        editForm.setFieldsValue({
            robotId: detail.id,
            robotCode: detail.robotCode,
            robotName: detail.robotName,
            robotType: detail.robotType,
            description: detail.description,
            status: detail.status,
        });
        setEditModalVisible(true);
    };

    const handleEditSubmit = async () => {
        const values = await editForm.validateFields();
        await updateRobot({ ...values, robotId: currentRobot.id });
        message.success('编辑成功');
        setEditModalVisible(false);
        editForm.resetFields();
        loadData();
    };
    // 添加机器人
    const handleAdd = () => {
        setAddModalVisible(true);
    };

    const handleAddSubmit = async () => {
        const values = await addForm.validateFields();
        await createRobot(values);
        message.success('新增成功');
        setAddModalVisible(false);
        addForm.resetFields();
        loadData();
    };


    // 加载列表数据
    const loadData = async (pageNum = 1, pageSize = 10) => {
        const values = form.getFieldsValue();
        const res = await listRobots({
            ...values,
            pageNum,
            pageSize,
        });
        const { statistics: stats, records, total } = res.data;
        setStatistics(stats);
        if (pageNum === 1 && JSON.stringify(values) === '{}') {
            setTotalRobotCount(stats.total);
        }
        setData(records);
        setPagination({
            current: pageNum,
            pageSize: pageSize,
            total: total,
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
    // 删除机器人
    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除机器人 "${record.robotCode}" 吗？`,
            okText: '确定',
            cancelText: '取消',
            onOk: async () => {
                await deleteRobot(record.robotCode);
                message.success('删除成功');
                loadData();
            },
        });
    };

    const columns = [
        {
            title: "序号",
            key: "index",
            width: 80,
            render: (_: any, __: any, index: number) => (pagination.current - 1) * pagination.pageSize + index + 1,
        },
        {
            title: "机器人编码",
            dataIndex: "robotCode",
            key: "robotCode",
        },
        {
            title: "机器人名称",
            dataIndex: "robotName",
            key: "robotName",
        },
        {
            title: "类型",
            dataIndex: "robotType",
            key: "robotType",
        },
        {
            title: "状态",
            dataIndex: "status",
            key: "status",
            render: (status: number) => {
                const statusMap: Record<number, { text: string; color: string }> = {
                    1: { text: '在线', color: 'green' },
                    0: { text: '离线', color: 'red' },
                };
                const { text, color } = statusMap[status] || { text: '未知', color: 'default' };
                return <Tag color={color}>{text}</Tag>;
            },
        },
        {
            title: "当前任务",
            dataIndex: "currentTaskId",
            key: "currentTaskId",
            render: (task: string) => task || '空闲',
        },
        {
            title: "最后心跳",
            dataIndex: "lastHeartbeat",
            key: "lastHeartbeat",
        },
        {
            title: "更新时间",
            dataIndex: "updateTime",
            key: "updateTime",
        },
        {
            title: "操作",
            key: "action",
            render: (_: any, record: any) => (
                <Space size={16}>
                    <a onClick={() => handleView(record)}>查看</a>
                    <a onClick={() => handleEdit(record)}>编辑</a>
                    <a style={{ color: '#f56c6c' }} onClick={() => handleDelete(record)}>删除</a>
                </Space>
            ),
        },
    ];

    // 统计卡片配置
    const statsCards = [
        { title: '总机器人数', value: totalRobotCount, color: '#1890ff' },
        { title: '在线', value: statistics.online, color: '#52c41a' },
        { title: '工作中', value: 0, color: '#faad14' },
        { title: '离线', value: statistics.offline, color: '#ff4d4f' },
    ];

    return (
        <>
            <h1 style={{ margin: '0 0 24px 0', fontSize: 28, fontWeight: 600 }}>机器人列表</h1>

            {/* 统计卡片 */}
            <Row gutter={16} style={{ marginBottom: 24 }}>
                {statsCards.map((stat, index) => (
                    <Col span={6} key={index}>
                        <Card>
                            <Statistic
                                title={stat.title}
                                value={stat.value}
                                valueStyle={{ color: stat.color, fontSize: 32 }}
                            />
                        </Card>
                    </Col>
                ))}
            </Row>

            {/* 新增按钮 */}
            <div style={{ marginBottom: 20 }}>
                <Button type="primary" onClick={handleAdd}>新增机器人</Button>
            </div>

            {/* 查询表单 */}
            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="robotName" label="机器人名称">
                        <Input placeholder="请输入" style={{ width: 180 }} />
                    </Form.Item>
                    <Form.Item name="robotCode" label="机器人编码">
                        <Input placeholder="请输入" style={{ width: 180 }} />
                    </Form.Item>
                    <Form.Item name="status" label="状态">
                        <Select placeholder="请选择" style={{ width: 150 }} allowClear>
                            <Select.Option value={1}>在线</Select.Option>
                            <Select.Option value={0}>离线</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" onClick={handleSearch}>查询</Button>
                        <Button style={{ marginLeft: 8 }} onClick={handleReset}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>
            {/* 新增机器人弹窗 */}
            <Modal
                title="新增机器人"
                open={addModalVisible}
                onOk={handleAddSubmit}
                onCancel={() => {
                    setAddModalVisible(false);
                    addForm.resetFields();
                }}
                okText="确定"
                cancelText="取消"
            >
                <Form form={addForm} layout="vertical">
                    <Form.Item
                        name="robotCode"
                        label="机器人编码"
                        rules={[{ required: true, message: '请输入机器人编码' }]}
                    >
                        <Input placeholder="如:机器人A002" />
                    </Form.Item>
                    <Form.Item
                        name="robotName"
                        label="机器人名称"
                        rules={[{ required: true, message: '请输入机器人名称' }]}
                    >
                        <Input placeholder="如:机器人A002" />
                    </Form.Item>
                    <Form.Item
                        name="robotType"
                        label="类型"
                        rules={[{ required: true, message: '请输入类型' }]}
                    >
                        <Input placeholder="如:thread" />
                    </Form.Item>
                    <Form.Item name="description" label="描述">
                        <Input.TextArea rows={4} placeholder="请输入描述" />
                    </Form.Item>
                    <Form.Item
                        name="status"
                        label="状态"
                        initialValue={1}
                        rules={[{ required: true, message: '请选择状态' }]}
                    >
                        <Radio.Group>
                            <Radio value={1}>在线</Radio>
                            <Radio value={0}>离线</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
            {/* 编辑机器人弹窗 */}
            <Modal
                title="编辑机器人"
                open={editModalVisible}
                onOk={handleEditSubmit}
                onCancel={() => {
                    setEditModalVisible(false);
                    editForm.resetFields();
                }}
                okText="确定"
                cancelText="取消"
            >
                <Form form={editForm} layout="vertical">
                    <Form.Item name="robotId" hidden>
                        <Input />
                    </Form.Item>
                    <Form.Item
                        name="robotCode"
                        label="机器人编码"
                        rules={[{ required: true, message: '请输入机器人编码' }]}
                    >
                        <Input disabled />
                    </Form.Item>
                    <Form.Item
                        name="robotName"
                        label="机器人名称"
                        rules={[{ required: true, message: '请输入机器人名称' }]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item
                        name="robotType"
                        label="类型"
                        rules={[{ required: true, message: '请输入类型' }]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item name="description" label="描述">
                        <Input.TextArea rows={4} />
                    </Form.Item>
                    <Form.Item name="status" label="状态" rules={[{ required: true }]}>
                        <Radio.Group>
                            <Radio value={1}>在线</Radio>
                            <Radio value={0}>离线</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
            {/* 查看详情机器人弹窗 */}
            <Modal
                title="机器人详情"
                open={detailModalVisible}
                onCancel={() => {
                    setDetailModalVisible(false);
                    setRobotDetail(null);
                }}
                footer={[
                    <Button key="close" onClick={() => {
                        setDetailModalVisible(false);
                        setRobotDetail(null);
                    }}>
                        关闭
                    </Button>
                ]}
                width={600}
            >
                {robotDetail && (
                    <Descriptions column={1} bordered>
                        <Descriptions.Item label="机器人ID">{robotDetail.id}</Descriptions.Item>
                        <Descriptions.Item label="机器人编码">{robotDetail.robotCode}</Descriptions.Item>
                        <Descriptions.Item label="机器人名称">{robotDetail.robotName}</Descriptions.Item>
                        <Descriptions.Item label="类型">{robotDetail.robotType}</Descriptions.Item>
                        <Descriptions.Item label="描述">{robotDetail.description || '-'}</Descriptions.Item>
                        <Descriptions.Item label="状态">
                            <Tag color={robotDetail.status === 1 ? 'green' : 'red'}>
                                {robotDetail.status === 1 ? '在线' : '离线'}
                            </Tag>
                        </Descriptions.Item>
                        <Descriptions.Item label="当前任务">{robotDetail.currentTaskId || '空闲'}</Descriptions.Item>
                        <Descriptions.Item label="最后心跳">{robotDetail.lastHeartbeat}</Descriptions.Item>
                        <Descriptions.Item label="创建时间">{robotDetail.createTime}</Descriptions.Item>
                        <Descriptions.Item label="更新时间">{robotDetail.updateTime}</Descriptions.Item>
                    </Descriptions>
                )}
            </Modal>
            {/* 数据表格 */}
            <Table
                columns={columns}
                dataSource={data}
                rowKey="robotId"
                pagination={pagination}
                onChange={handleTableChange}
            />
        </>
    );
};

export default RobotList;