import { Button, Card, DatePicker, Descriptions, Form, Input, InputNumber, Modal, Select, Space, Table, Tag, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { createTask, deleteTask, executeTask, getTaskDetail, listTasks, updateTask } from '@/services/task';
import { listProcesses } from '@/services/processes';
import { listRobots } from '@/services/robots';
import { useNavigate } from '@umijs/max';

const TaskList = () => {
    const [form] = Form.useForm();
    const [data, setData] = useState([]);
    const [processes, setProcesses] = useState([]);
    const [robots, setRobots] = useState([]);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editForm] = Form.useForm();
    const [currentTask, setCurrentTask] = useState<any>(null);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [taskDetail, setTaskDetail] = useState<any>(null);
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [addForm] = Form.useForm();
    const navigate = useNavigate();

    const [pagination, setPagination] = useState({
        current: 1,
        pageSize: 10,
        total: 0,
    });

    const statusMap: Record<number, { text: string; color: string }> = {
        0: { text: '待执行', color: 'default' },
        1: { text: '执行中', color: 'gold' },
        2: { text: '成功', color: 'green' },
        3: { text: '失败', color: 'red' },
    }
    // 执行任务
    const handleExecute = (record: any) => {
        Modal.confirm({
            title: '确认执行',
            content: `确定要执行任务 "${record.taskName}" 吗？`,
            okText: '确定',
            cancelText: '取消',
            onOk: async () => {
                const res = await executeTask(record.taskCode);
                message.success(`任务已开始执行,执行ID: ${res.data.executionId}`);
                loadData();
            },
        });
    };
    // 查看任务详情
    const handleViewDetail = async (record: any) => {
        // const res = await getTaskDetail(record.taskCode);
        // setTaskDetail(res.data);
        // setDetailModalVisible(true);
        navigate(`/Rpa/Tasks/detail/${record.taskCode}`);
    };
    // 加载任务列表
    const loadData = async (pageNum = 1, pageSize = 10) => {
        const values = form.getFieldsValue();
        const params: any = {
            pageNum,
            pageSize,
        };
        if (values.taskCode) params.taskCode = values.taskCode;
        if (values.status !== undefined && values.status !== '') params.status = values.status;
        if (values.createTime && values.createTime.length === 2) {
            params.startTime = values.createTime[0].format('YYYY-MM-DDTHH:mm:ss');
            params.endTime = values.createTime[1].format('YYYY-MM-DDTHH:mm:ss');
        }
        const res = await listTasks(params);
        setData(res.data.data.records || []);
        setPagination({
            current: pageNum,
            pageSize: pageSize,
            total: res.data.data.records.total,
        });
    };
    // 加载流程列表
    const loadProcesses = async () => {
        const res = await listProcesses({ pageNum: 1, pageSize: 100 });
        setProcesses(res.data.records || []);
    };

    // 加载机器人列表
    const loadRobots = async () => {
        const res = await listRobots({ pageNum: 1, pageSize: 100 });
        setRobots(res.data.records || []);
    };
    // 新增任务
    const handleAdd = () => {
        setAddModalVisible(true);
        loadProcesses();
        loadRobots();
    };

    const handleAddSubmit = async () => {
        const values = await addForm.validateFields();
        await createTask(values);
        setAddModalVisible(false);
        addForm.resetFields();
        loadData();
    };
    // 编辑任务
    const handleEdit = async (record: any) => {
        const res = await getTaskDetail(record.taskCode);
        const detail = res.data;
        setCurrentTask(detail);
        setEditModalVisible(true);
        editForm.setFieldsValue({
            id: detail.id,
            taskName: detail.taskName,
            processId: detail.processId,
            robotId: detail.robotId,
            taxNo: detail.taxNo,
            enterpriseName: detail.enterpriseName,
            priority: detail.priority,
            remark: detail.remark,
        });
        loadProcesses();
        loadRobots();
    };

    const handleEditSubmit = async () => {
        const values = await editForm.validateFields();
        await updateTask({ ...values, id: currentTask.taskId });
        setEditModalVisible(false);
        editForm.resetFields();
        loadData();
    };
    // 删除任务
    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除任务 "${record.taskName}" 吗？`,
            okText: '确定',
            cancelText: '取消',
            onOk: async () => {
                await deleteTask(record.taskCode);
                loadData();
            },
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
        { title: '任务编码', dataIndex: 'taskCode', key: 'taskCode' },
        { title: '任务名称', dataIndex: 'taskName', key: 'taskName' },
        { title: '纳税人识别号', dataIndex: 'taxNo', key: 'taxNo' },
        { title: '企业名称', dataIndex: 'enterpriseName', key: 'enterpriseName' },
        {
            title: '任务状态',
            dataIndex: 'status',
            key: 'status',
            render: (status: number) => {
                const item = statusMap[status] || { text: '未知', color: 'default' };
                return <Tag color={item.color}>{item.text}</Tag>;
            },
        },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
        {
            title: '操作',
            key: 'operation',
            render: (_: any, record: any) => (
                <Space size={16}>
                    <a onClick={() => handleViewDetail(record)}>查看详情</a>
                    <a onClick={() => handleEdit(record)}>编辑</a>
                    <a onClick={() => handleExecute(record)} style={{ color: '#52c41a' }}>执行</a>
                    <a onClick={() => handleDelete(record)} style={{ color: '#f56c6c' }}>删除</a>
                </Space>
            ),
        },
    ];

    return (
        <>
            <h1 style={{ margin: 0, marginBottom: 24, fontSize: 28, fontWeight: 600 }}>任务列表</h1>

            <div style={{ marginBottom: 20 }}>
                <Button type="primary" onClick={handleAdd}>新增任务</Button>
            </div>

            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="taskCode" label="任务编码">
                        <Input placeholder="请输入" style={{ width: 220 }} />
                    </Form.Item>
                    <Form.Item name="status" label="任务状态">
                        <Select placeholder="请选择" style={{ width: 220 }} allowClear>
                            <Select.Option value={0}>待执行</Select.Option>
                            <Select.Option value={1}>执行中</Select.Option>
                            <Select.Option value={2}>成功</Select.Option>
                            <Select.Option value={3}>失败</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="createTime" label="开始时间">
                        <DatePicker.RangePicker
                            style={{ width: 360 }}
                            showTime
                            placeholder={['开始', '结束']}
                            format="YYYY-MM-DD HH:mm:ss"
                        />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" onClick={handleSearch}>查询</Button>
                        <Button onClick={handleReset} style={{ marginLeft: 8 }}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>
            {/* 新增弹窗 */}
            <Modal
                title="新建任务"
                open={addModalVisible}
                onOk={handleAddSubmit}
                onCancel={() => {
                    setAddModalVisible(false);
                    addForm.resetFields();
                }}
                okText="确定"
                cancelText="取消"
                width={600}
            >
                <Form form={addForm} layout="vertical">
                    <Form.Item
                        name="taskName"
                        label="任务名称"
                    >
                        <Input placeholder="请输入任务名称" />
                    </Form.Item>
                    <Form.Item
                        name="processId"
                        label="绑定流程"
                        rules={[{ required: true, message: '请选择流程' }]}
                    >
                        <Select placeholder="请选择流程">
                            {processes.map((item: any) => (
                                <Select.Option key={item.id} value={item.id}>
                                    {item.processName}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="robotId"
                        label="绑定机器人"
                        rules={[{ required: true, message: '请选择机器人' }]}
                    >
                        <Select placeholder="请选择机器人">
                            {robots.map((item: any) => (
                                <Select.Option key={item.robotId} value={item.robotId}>
                                    {item.robotName}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="taxNo"
                        label="纳税人识别号"
                    >
                        <Input placeholder="请输入纳税人识别号" />
                    </Form.Item>
                    <Form.Item
                        name="enterpriseName"
                        label="企业名称"
                    >
                        <Input placeholder="请输入企业名称" />
                    </Form.Item>
                    <Form.Item
                        name="priority"
                        label="优先级"
                        initialValue={5}
                    >
                        <InputNumber min={1} max={10} style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item name="remark" label="备注">
                        <Input.TextArea rows={4} placeholder="请输入备注" />
                    </Form.Item>
                </Form>
            </Modal>
            {/* 编辑弹窗 */}
            <Modal
                title="编辑任务"
                open={editModalVisible}
                onOk={handleEditSubmit}
                onCancel={() => {
                    setEditModalVisible(false);
                    editForm.resetFields();
                }}
                okText="确定"
                cancelText="取消"
                width={600}
            >
                <Form form={editForm} layout="vertical">
                    <Form.Item name="id" hidden>
                        <Input />
                    </Form.Item>
                    <Form.Item
                        name="taskName"
                        label="任务名称"

                    >
                        <Input disabled />
                    </Form.Item>
                    <Form.Item
                        name="processId"
                        label="绑定流程"
                        rules={[{ required: true, message: '请选择流程' }]}
                    >
                        <Select placeholder="请选择流程">
                            {processes.map((item: any) => (
                                <Select.Option key={item.id} value={item.id}>
                                    {item.processName}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="robotId"
                        label="绑定机器人"
                        rules={[{ required: true, message: '请选择机器人' }]}
                    >
                        <Select placeholder="请选择机器人">
                            {robots.map((item: any) => (
                                <Select.Option key={item.robotId} value={item.robotId}>
                                    {item.robotName}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="taxNo"
                        label="纳税人识别号"
                    >
                        <Input placeholder="请输入纳税人识别号" />
                    </Form.Item>
                    <Form.Item
                        name="enterpriseName"
                        label="企业名称"
                    >
                        <Input placeholder="请输入企业名称" />
                    </Form.Item>
                    <Form.Item
                        name="priority"
                        label="优先级"
                        initialValue={5}
                    >
                        <InputNumber min={1} max={10} style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item name="remark" label="备注">
                        <Input.TextArea rows={4} placeholder="请输入备注" />
                    </Form.Item>
                </Form>
            </Modal>
            <Table
                columns={columns}
                dataSource={data}
                rowKey="taskId"
                pagination={pagination}
                onChange={handleTableChange}
            />
        </>
    );
};

export default TaskList;