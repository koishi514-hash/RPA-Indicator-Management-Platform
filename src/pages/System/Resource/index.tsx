import React, { useEffect, useState } from 'react';
import { Button, Form, Input, InputNumber, Modal, Radio, Select, Space, Table, Tag, TreeSelect } from 'antd';
import { createResource, deleteResource, listAllResources, updateResource } from '@/services/resource';

const { Option } = Select;

const Resource = () => {
    const [form] = Form.useForm();
    const [resources, setResources] = useState([]);
    const [addVisible, setAddVisible] = useState(false);
    const [addForm] = Form.useForm();
    const [editVisible, setEditVisible] = useState(false);
    const [editForm] = Form.useForm();
    const [editRecord, setEditRecord] = useState<any>(null);
    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    // 加载列表
    useEffect(() => {
        const load = async () => {
            const res = await listAllResources({ tree: true });
            setResources(res.data.data.records);
            setExpandedRowKeys([]);
        };
        load();
    }, []);
    const handleExpand = (expanded: boolean, record: any) => {
        if (expanded) {
            // 打开 → 只展开当前这一个
            setExpandedRowKeys([record.id]);
        } else {
            // 关闭 → 清空
            setExpandedRowKeys([]);
        }
    };
    // 资源类型映射
    const resourceType = [
        { value: 1, label: '菜单' },
        { value: 2, label: '按钮' },
        { value: 3, label: 'API' },
    ];
    // 状态标签
    const getStatusTag = (status: number) => {
        if (status === 1) return <Tag color="green">启用</Tag>;
        return <Tag color="red">禁用</Tag>;
    };

    // 类型标签
    const getTypeTag = (type: number) => {
        const map: any = {
            1: { color: 'blue', text: '菜单' },
            2: { color: 'green', text: '按钮' },
            3: { color: 'orange', text: 'API' },
        };
        const item = map[type];
        return item ? <Tag color={item.color}>{item.text}</Tag> : null;
    };

    // 表格列
    const columns = [
        { title: '资源名称', dataIndex: 'resourceName', key: 'resourceName' },
        { title: '资源编码', dataIndex: 'resourceCode', key: 'resourceCode' },
        {
            title: '资源类型',
            dataIndex: 'resourceType',
            key: 'resourceType',
            render: (type: number) => getTypeTag(type),
        },
        { title: '路径/URL', dataIndex: 'path', key: 'path' },
        { title: '图标', dataIndex: 'icon', key: 'icon' },
        { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder' },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status: number) => getStatusTag(status),
        },
        {
            title: '操作',
            key: 'action',
            render: (_: any, record: any) => (
                <Space size="middle">
                    <Button size="small" onClick={() => openEditModal(record)}>编辑</Button>
                    <Button size="small" danger onClick={() => handleDelete(record.id)}>
                        删除
                    </Button>
                </Space>
            ),
        },
    ];

    // 查询
    const handleSearch = async () => {
        const params = form.getFieldsValue(); // 获取查询条件
        const res = await listAllResources({ ...params, tree: true }); // 传给接口
        setResources(res.data.data.records);
    };
    // 重置
    const handleReset = () => {
        form.resetFields();
        handleSearch();
    };

    // 提交新增
    const submitAdd = async () => {
        const values = await addForm.validateFields();
        const params = {
            resourceCode: values.resourceCode,
            resourceName: values.resourceName,
            resourceType: values.resourceType, // 数字转字符串
            parentId: values.parentId, // 无父级传 0
            path: values.path,
            icon: values.icon,
            sortOrder: values.sortOrder,
            status: values.status,
        };
        await createResource(params);
        setAddVisible(false);
        handleSearch();
    };
    //表单回显
    const openEditModal = (record: any) => {
        editForm.setFieldsValue({
            ...record,
            resourceType: record.resourceType,
        });
        setEditRecord(record);
        setEditVisible(true);
    };

    //编辑
    const submitEdit = async () => {
        const values = await editForm.validateFields();
        await updateResource(editRecord.id, values);
        setEditVisible(false);
        handleSearch();
    };

    // 删除资源
    const handleDelete = async (resourceId: any) => {
        await deleteResource(resourceId);
        handleSearch(); // 刷新列表
    };
    useEffect(() => {
        handleSearch();
    }, []);

    return (
        <div style={{ background: '#fff', padding: 20, borderRadius: 8 }}>
            <div style={{ marginBottom: 20 }}>
                <h2 style={{ marginBottom: 16, color: '#333' }}>资源管理</h2>
                <Button type="primary" onClick={() => setAddVisible(true)}>新增资源</Button>
            </div>

            <Form
                form={form}
                layout="inline"
                onFinish={handleSearch}
                style={{ marginBottom: 20, padding: 20, background: '#f9f9f9', borderRadius: 8 }}
            >
                <Form.Item name="resourceName" label="资源名称">
                    <Input placeholder="请输入" style={{ width: 180 }} />
                </Form.Item>

                <Form.Item name="resourceType" label="资源类型">
                    <Select placeholder="请选择" style={{ width: 180 }}>
                        {resourceType.map((opt) => (
                            <Option key={opt.value} value={opt.value}>
                                {opt.label}
                            </Option>
                        ))}
                    </Select>
                </Form.Item>

                <Form.Item>
                    <Space>
                        <Button type="primary" htmlType="submit" onClick={handleSearch}>查询</Button>
                        <Button onClick={handleReset}>重置</Button>
                    </Space>
                </Form.Item>
            </Form>
            <Modal
                title="新增资源"
                open={addVisible}
                onCancel={() => setAddVisible(false)}
                onOk={() => submitAdd()}
            >
                <Form form={addForm} layout="vertical">
                    <Form.Item label="父级资源" name="parentId">
                        <TreeSelect style={{ width: '100%' }} treeData={resources} fieldNames={{
                            label: 'resourceName',
                            value: 'id',
                            children: 'children',
                        }} placeholder="请选择父级资源"
                            allowClear
                            treeDefaultExpandAll></TreeSelect>
                    </Form.Item>
                    <Form.Item label="资源名称" name="resourceName" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="资源编码" name="resourceCode" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="资源类型" name="resourceType" rules={[{ required: true }]}>
                        <Select placeholder="请选择">
                            {resourceType.map((opt) => (
                                <Option key={opt.value} value={opt.value}>
                                    {opt.label}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item label="路径/URL" name="path"><Input /></Form.Item>
                    <Form.Item label="图标" name="icon"><Input /></Form.Item>
                    <Form.Item label="排序" name="sortOrder"><InputNumber type="number" style={{ width: '100%' }} /></Form.Item>
                    <Form.Item label="状态" name="status" initialValue={1} rules={[{ required: true }]}>
                        <Radio.Group>
                            <Radio value={1}>
                                <Tag color="green">启用</Tag>
                            </Radio>
                            <Radio value={0}>
                                <Tag color="red">禁用</Tag>
                            </Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal title="编辑资源"
                open={editVisible}
                onCancel={() => setEditVisible(false)}
                onOk={() => submitEdit()}
                width={600}
            >
                <Form form={editForm} layout="vertical">
                    <Form.Item label="父级资源" name="parentId">
                        <TreeSelect style={{ width: '100%' }} treeData={resources} fieldNames={{
                            label: 'resourceName',
                            value: 'id',
                            children: 'children',
                        }} placeholder="请选择父级资源"
                            allowClear
                            treeDefaultExpandAll></TreeSelect>
                    </Form.Item>
                    <Form.Item label="资源名称" name="resourceName" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="资源编码" name="resourceCode" ><Input disabled /></Form.Item>
                    <Form.Item label="资源类型" name="resourceType" rules={[{ required: true }]}>
                        <Select placeholder="请选择">
                            {resourceType.map((opt) => (
                                <Option key={opt.value} value={opt.value}>
                                    {opt.label}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item label="路径/URL" name="path"><Input /></Form.Item>
                    <Form.Item label="图标" name="icon"><Input /></Form.Item>
                    <Form.Item label="排序" name="sortOrder"><InputNumber type="number" style={{ width: '100%' }} /></Form.Item>
                    <Form.Item label="状态" name="status" initialValue={1} rules={[{ required: true }]}>
                        <Radio.Group>
                            <Radio value={1}>
                                <Tag color="green">启用</Tag>
                            </Radio>
                            <Radio value={0}>
                                <Tag color="red">禁用</Tag>
                            </Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>

            {/* 树形表格 */}
            <Table
                columns={columns}
                dataSource={resources}
                rowKey="id"
                pagination={false}
                expandable={{
                    defaultExpandAllRows: true, // 默认展开所有层级
                    expandedRowKeys: expandedRowKeys,
                    onExpand: handleExpand,

                }}

            />
        </div>
    );
};

export default Resource;