import React, { useState } from 'react';
import { useEffect } from 'react';
import { Button, Form, Input, Modal, Radio, Space, Table, Tag, Tooltip, Tree } from 'antd';
import { createRole, deleteRole, listAllResources, pageRoleList, updateRole } from '@/services/role';

const Role = () => {
    const [form] = Form.useForm();
    const [addForm] = Form.useForm();
    const [editForm] = Form.useForm();
    const [permForm] = Form.useForm();
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(false);
    const [addVisible, setAddVisible] = useState(false);
    const [editVisible, setEditVisible] = useState(false);
    const [permVisible, setPermVisible] = useState(false);

    const [currentRecord, setCurrentRecord] = useState<any>(null);
    const [treeData, setTreeData] = useState<any[]>([]);

    // 格式化树节点数据
    const formatNode = (node: { resourceName: any; id: any; parentId: any; children: any; }) => {
        return {
            title: node.resourceName,
            key: node.id,
            parentId: node.parentId,
            children: (node.children || []).map((child: any) => formatNode(child))
        };
    };
    // 加载资源树数据
    const loadResources = async () => {
        const res = await listAllResources();
        const data = res.data || [];
        const tree = data.map((item: any) => formatNode(item));
        setTreeData(tree);
    };
    //查询角色列表
    const getRoleList = async (params = {}) => {
        setLoading(true);
        const res = await pageRoleList({ pageNum: 1, pageSize: 10, ...params });
        setRoles(res.data?.records || []);
        setLoading(false);
    };
    //查询
    const handleSearch = () => {
        const values = form.getFieldsValue();
        getRoleList({
            roleName: values.roleName,
            roleCode: values.roleCode,
            pageNum: 1,
            pageSize: 10,
        });
    };
    //新增
    const handleAddSubmit = async () => {
        await createRole(await addForm.validateFields());
        setAddVisible(false);
        getRoleList();
    };
    //编辑
    const handleEditSubmit = async () => {
        await updateRole(currentRecord.roleId, await editForm.validateFields());
        setEditVisible(false);
        getRoleList();
    };
    //权限分配
    const handlePermSubmit = async () => {
        const { resourceIds } = await permForm.validateFields();
        await updateRole(currentRecord.roleId, { resourceIds });
        setPermVisible(false);
        getRoleList();
    };
    // 删除
    const handleDelete = async (roleId: number) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除该角色吗？',
            onOk: async () => {
                await deleteRole(roleId);
                getRoleList();
            },
        });
    };
    // 处理重置
    const handleReset = () => {
        form.resetFields();
        getRoleList();
    };

    // 状态标签
    const getStatusTag = (status: number) => {
        return status === 1 ? (
            <Tag color="green">启用</Tag>
        ) : (
            <Tag color="red">禁用</Tag>
        );
    };

    // 表格列配置
    const columns = [
        {
            title: '序号',
            key: 'index',
            render: (_: any, __: any, index: number) => index + 1,
        },
        {
            title: '角色编码',
            dataIndex: 'roleCode',
            key: 'roleCode',
        },
        {
            title: '角色名称',
            dataIndex: 'roleName',
            key: 'roleName',
        },
        {
            title: '描述',
            dataIndex: 'description',
            key: 'description',
        },
        {
            title: '权限',
            dataIndex: 'resourceNames',
            key: 'resourceNames',
            render: (text: string) => {
                // 1. 控制默认显示长度（可根据需求调整，比如20/30）
                const maxShowLength = 10;
                // 2. 文本超过长度时，截断 + 省略号；否则直接显示
                const displayText = text?.length > maxShowLength
                    ? `${text.slice(0, maxShowLength)}...`
                    : text;

                return (
                    <Tooltip title={text} placement="topLeft">
                        <span style={{
                            display: 'inline-block',
                            maxWidth: '150px', // 控制列最大宽度
                            whiteSpace: 'nowrap',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis'
                        }}>
                            {displayText}
                        </span>
                    </Tooltip>
                );
            },
        },
        {
            title: '用户数',
            dataIndex: 'userCount',
            key: 'userCount',
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (status: number) => getStatusTag(status),
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime',
        },
        {
            title: '操作',
            key: 'action',

            render: (_: any, record: any) => (
                <Space size="middle">
                    <Button size="small" onClick={() => {
                        setCurrentRecord(record);
                        editForm.setFieldsValue(record);
                        setEditVisible(true);
                    }} >编辑</Button>
                    <Button size="small" onClick={() => {
                        setCurrentRecord({
                            ...record,
                            id: record.id || [],
                        });
                        loadResources();
                        permForm.setFieldsValue({ resourceIds: record.resourceIds || [] });
                        setPermVisible(true);
                    }} >权限分配</Button>
                    <Button size="small" danger onClick={() => handleDelete(record.roleId)} >删除</Button>
                </Space>
            )
        }
    ];
    useEffect(() => {
        getRoleList();
    }, []);
    return (

        <div style={{ background: '#fff', padding: 20, borderRadius: 8 }}>
            <div style={{ marginBottom: 20 }}>
                <h2 style={{ marginBottom: 16, color: '#333' }}>角色管理</h2>
                <Button type="primary" onClick={() => [setAddVisible(true), loadResources()]} >
                    新增角色
                </Button>
            </div>

            <Form
                form={form}
                layout="inline"
                style={{ marginBottom: 20, padding: 20, background: '#f9f9f9', borderRadius: 8 }}
            >
                <Form.Item name="roleName" label="角色名称">
                    <Input placeholder="请输入" style={{ width: 180 }} />
                </Form.Item>

                <Form.Item name="roleCode" label="角色编码">
                    <Input placeholder="请输入" style={{ width: 180 }} />
                </Form.Item>

                <Form.Item>
                    <Space>
                        <Button type="primary" htmlType="submit" onClick={handleSearch}>
                            查询
                        </Button>
                        <Button onClick={handleReset}>
                            重置
                        </Button>
                    </Space>
                </Form.Item>
            </Form>

            <Table
                loading={loading}
                columns={columns}
                dataSource={roles}
                rowKey="roleId"
                pagination={{
                    pageSize: 10,
                    showSizeChanger: true,
                    showQuickJumper: true,
                    showTotal: (total) => `共 ${total} 条记录`
                }}
            />
            {/* 新增弹窗 */}
            <Modal title="新增角色" open={addVisible} onCancel={() => setAddVisible(false)} onOk={handleAddSubmit} >
                <Form form={addForm} layout="vertical">
                    <Form.Item label="角色编码" name="roleCode" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="角色名称" name="roleName" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="权限" name="resourceIds"><Tree checkable treeData={treeData} defaultExpandAll onCheck={(checkedKeys) => {
                        addForm.setFieldsValue({ resourceIds: checkedKeys });
                    }} /></Form.Item>
                    <Form.Item label="描述" name="description"><Input.TextArea rows={3} /></Form.Item>
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

            {/* 编辑弹窗 */}
            <Modal title="编辑角色" open={editVisible} onCancel={() => setEditVisible(false)} onOk={handleEditSubmit}>
                <Form form={editForm} layout="vertical">
                    <Form.Item label="角色编码" name="roleCode"><Input disabled /></Form.Item>
                    <Form.Item label="角色名称" name="roleName" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item label="描述" name="description"><Input.TextArea rows={3} /></Form.Item>
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

            {/* 分配权限弹窗 */}
            <Modal title="分配权限" open={permVisible} onCancel={() => setPermVisible(false)} onOk={handlePermSubmit} width={600}>
                <Form form={permForm} layout="vertical">
                    <Form.Item label="权限" name="resourceIds" valuePropName="checkedKeys"><Tree checkable treeData={treeData}
                        // 关键3：添加onCheck，同步表单状态，解锁勾选
                        onCheck={(checkedKeys) => {
                            permForm.setFieldsValue({ resourceIds: checkedKeys });
                        }} /></Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default Role;         