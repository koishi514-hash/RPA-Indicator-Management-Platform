import { Button, Form, Input, Select, Space, Table, Tag, Modal, Radio } from "antd";
import { useEffect, useState } from "react";
import { pageUserList, createUser, changeUserStatus, deleteUser, updateUser, resetUserPassword } from "@/services/user"; // 引入你之前写的service
import { pageRoleList } from "@/services/role";

function User() {
    const [form] = Form.useForm();
    const [addForm] = Form.useForm(); // 新增用户表单
    const [addModalVisible, setAddModalVisible] = useState(false); // 弹窗控制
    const [userList, setUserList] = useState([]); // 用户列表数据
    const [loading, setLoading] = useState(false);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editForm] = Form.useForm();
    const [currentRecord, setCurrentRecord] = useState<any>(null);
    // 角色选项（可根据实际角色数据动态获取）
    const [roleOptions, setRoleOptions] = useState([]);
    const [roleOption, setRoleOption] = useState([]);

    // 状态映射
    const statusMap: Record<number, JSX.Element> = {
        1: <Tag color="green">启用</Tag>,
        0: <Tag color="red">禁用</Tag>,
    };

    const columns = [
        {
            title: "序号",
            key: "index",
            width: 80,
            render: (_: any, __: any, index: number) => index + 1,
        },
        {
            title: "用户名",
            dataIndex: "username",
            key: "username",
        },
        {
            title: "姓名",
            dataIndex: "nickname",
            key: "nickname",
        },
        {
            title: "邮箱",
            dataIndex: "email",
            key: "email",
        },
        {
            title: "手机号",
            dataIndex: "phone",
            key: "phone",
        },
        {
            title: "角色",
            dataIndex: "roleNames",
            key: "roleNames",
        },
        {
            title: "状态",
            dataIndex: "status",
            key: "status",
            render: (status: number) => statusMap[status] || "-",
        },
        {
            title: "创建时间",
            dataIndex: "createTime",
            key: "createTime",
        },
        {
            title: "操作",
            key: "action",
            width: 220,
            render: (_: any, record: any) => (
                <Space size="middle">
                    <Button size="small" type="link" onClick={() => handleEdit(record)}>
                        编辑
                    </Button>
                    <Button size="small" type="link" onClick={() => handleResetToDefaultPwd(record.userId)}>
                        重置密码
                    </Button>
                    {record.status === 1 && (
                        <Button size="small" type="link" onClick={() => handleChangeStatus(record.userId, 0)}>
                            禁用
                        </Button>
                    )}
                    <Button size="small" type="link" danger onClick={() => handleDelete(record.userId)}>
                        删除
                    </Button>
                </Space>
            ),
        },
    ];

    // 初始化加载用户列表
    const fetchUserList = async (params = {}) => {
        setLoading(true);
        try {
            const res = await pageUserList(params);
            setUserList(res.data.records);
        } finally {
            setLoading(false);
        }
    };
    //加载角色列表
    const loadRoles = async () => {
        const res = await pageRoleList({});
        const roles = res.data.records || [];
        setRoleOptions(
            roles.map((item: { roleName: any; roleId: any; roleCode: any; }) => ({
                label: item.roleName,
                value: item.roleCode,
            }))
        );
        setRoleOption(
            roles.map((item: { roleName: any; roleId: any; roleCode: any; }) => ({
                label: item.roleName,
                value: item.roleId,
            }))
        );
    };
    // 查询按钮
    const handleSearch = () => {
        const values = form.getFieldsValue();
        fetchUserList({
            username: values.username,
            nickname: values.nickname,
            roleCode: values.roleCode,
            pageNum: 1,
            pageSize: 10,
        });
    };

    // 重置按钮
    const handleReset = () => {
        form.resetFields();
        fetchUserList();
    };

    // 打开新增弹窗
    const handleAdd = () => {
        addForm.resetFields();
        addForm.setFieldValue("status", 1); // 默认启用
        setAddModalVisible(true);
    };

    // 提交新增用户
    const handleAddSubmit = async () => {
        const values = await addForm.validateFields();
        // 转换角色code为roleIds（根据后端要求，这里示例传对应ID）
        const params = {
            username: values.username,
            nickname: values.nickname,
            email: values.email,
            phone: values.phone,
            roleIds: values.roleIds,
            password: values.password,
            status: values.status,
        };
        await createUser(params);
        setAddModalVisible(false);
        fetchUserList(); // 刷新列表
    };

    // 页面初始化加载数据
    useEffect(() => {
        fetchUserList();
        loadRoles();
    }, []);

    // 删除用户
    const handleDelete = async (userId: number) => {
        Modal.confirm({
            title: "确认删除",
            content: "确定要删除该用户吗？",
            onOk: async () => {
                await deleteUser(userId);
                fetchUserList();
            },
        });
    };
    // 切换用户状态（禁用/启用）
    const handleChangeStatus = async (userId: number, targetStatus: number) => {
        const tipText = targetStatus === 0 ? "确认禁用该用户吗？" : "确认启用该用户吗？";
        Modal.confirm({
            title: "提示",
            content: tipText,
            onOk: async () => {
                await changeUserStatus(userId, { status: targetStatus });
                fetchUserList(); // 刷新列表
            },
        });
    };
    // 打开编辑弹窗（完整回显，用户名不可编辑）
    const handleEdit = (record: any) => {
        setCurrentRecord(record);
        // 完整回显所有字段，和截图完全一致
        editForm.setFieldsValue({
            username: record.username,
            nickname: record.nickname,
            email: record.email,
            phone: record.phone,
            roleIds: record.roleIds ? [record.roleIds] : [], // 角色回显（根据接口roleIds字段）
            status: record.status,
        });
        setEditModalVisible(true);
    };
    // 提交编辑（严格匹配你的PUT接口）
    const handleEditSubmit = async () => {
        const values = await editForm.validateFields();
        if (!currentRecord) return;

        const params = {
            nickname: values.nickname,
            email: values.email,
            phone: values.phone,
            roleIds: values.roleIds,
            status: values.status,
        };

        await updateUser(currentRecord.userId, params);
        setEditModalVisible(false);
        fetchUserList();
    };
    const handleResetToDefaultPwd = (userId: any) => {
        Modal.confirm({
            title: "确认重置",
            content: "确定要将该用户密码重置为初始密码:123456 吗？",
            onOk: async () => {
                // 直接调用接口，传固定密码
                await resetUserPassword(userId, {
                    newPassword: "123456",
                });
            },
        });
    };
    return (
        <div style={{ padding: 20 }}>
            <div style={{ fontSize: 18, fontWeight: "bold", marginBottom: 20 }}>
                用户管理
            </div>

            {/* 新增用户按钮 */}
            <div style={{ marginBottom: 20 }}>
                <Button type="primary" onClick={handleAdd}>
                    新增用户
                </Button>
            </div>

            {/* 搜索表单 */}
            <Form
                form={form}
                layout="inline"
                style={{ marginBottom: 20, padding: 20, background: "#f9f9f9", borderRadius: 8 }}
            >
                <Form.Item name="username" label="用户名">
                    <Input placeholder="请输入" style={{ width: 180 }} />
                </Form.Item>

                <Form.Item name="nickname" label="姓名">
                    <Input placeholder="请输入" style={{ width: 180 }} />
                </Form.Item>

                <Form.Item name="roleCode" label="角色">
                    <Select placeholder="请选择" style={{ width: 180 }} options={roleOptions} />
                </Form.Item>

                <Space>
                    <Button type="primary" onClick={handleSearch}>
                        查询
                    </Button>
                    <Button onClick={handleReset}>重置</Button>
                </Space>
            </Form>

            {/* 用户列表表格 */}
            <Table
                columns={columns}
                dataSource={userList}
                rowKey="userId"
                loading={loading}
                pagination={{ pageSize: 10 }}
            />

            {/* 新增用户弹窗 */}
            <Modal
                title="新增用户"
                open={addModalVisible}
                onOk={handleAddSubmit}
                onCancel={() => setAddModalVisible(false)}
                okText="确定"
                cancelText="取消"
                width={600}
            >
                <Form
                    form={addForm}
                    layout="vertical"
                    initialValues={{ status: 1 }}
                >
                    <Form.Item
                        label="用户名"
                        name="username"
                        rules={[{ required: true, message: "请输入用户名" }]}
                    >
                        <Input placeholder="请输入用户名" />
                    </Form.Item>

                    <Form.Item
                        label="姓名"
                        name="nickname"
                        rules={[{ required: true, message: "请输入姓名" }]}
                    >
                        <Input placeholder="请输入姓名" />
                    </Form.Item>

                    <Form.Item
                        label="邮箱"
                        name="email"
                        rules={[
                            { required: true, message: "请输入邮箱" },
                            { type: "email", message: "请输入正确的邮箱格式" },
                        ]}
                    >
                        <Input placeholder="请输入邮箱" />
                    </Form.Item>

                    <Form.Item
                        label="手机号"
                        name="phone"
                        rules={[{ required: true, message: "请输入手机号" }]}
                    >
                        <Input placeholder="请输入手机号" />
                    </Form.Item>

                    <Form.Item
                        label="角色"
                        name="roleIds"
                        rules={[{ required: true, message: "请选择角色" }]}
                    >
                        <Select placeholder="请选择角色" options={roleOption} mode="multiple" />
                    </Form.Item>

                    <Form.Item
                        label="密码"
                        name="password"
                        rules={[{ required: true, message: "请输入密码" }]}
                    >
                        <Input.Password placeholder="请输入密码" />
                    </Form.Item>

                    <Form.Item label="状态" name="status">
                        <Radio.Group>
                            <Radio value={1}>启用</Radio>
                            <Radio value={0}>禁用</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
            {/* 编辑用户弹窗（完全匹配截图） */}
            <Modal
                title="编辑用户"
                open={editModalVisible}
                onOk={handleEditSubmit}
                onCancel={() => setEditModalVisible(false)}
                okText="确定"
                cancelText="取消"
                width={600}
            >
                <Form
                    form={editForm}
                    layout="vertical"
                    initialValues={{ status: 1 }}
                >
                    {/* 用户名：不可编辑，仅展示 */}
                    <Form.Item
                        label="用户名"
                        name="username"
                        rules={[{ required: true, message: "用户名不可为空" }]}
                    >
                        <Input disabled placeholder="用户名不可修改" />
                    </Form.Item>

                    <Form.Item
                        label="姓名"
                        name="nickname"
                        rules={[{ required: true, message: "请输入姓名" }]}
                    >
                        <Input placeholder="请输入姓名" />
                    </Form.Item>

                    <Form.Item
                        label="邮箱"
                        name="email"
                        rules={[
                            { required: true, message: "请输入邮箱" },
                            { type: "email", message: "请输入正确的邮箱格式" },
                        ]}
                    >
                        <Input placeholder="请输入邮箱" />
                    </Form.Item>

                    <Form.Item
                        label="手机号"
                        name="phone"
                        rules={[{ required: true, message: "请输入手机号" }]}
                    >
                        <Input placeholder="请输入手机号" />
                    </Form.Item>

                    <Form.Item
                        label="角色"
                        name="roleIds"
                        rules={[{ required: true, message: "请选择角色" }]}
                    >
                        <Select placeholder="请选择角色" options={roleOption} mode="multiple" />
                    </Form.Item>

                    <Form.Item label="状态" name="status">
                        <Radio.Group>
                            <Radio value={1}>启用</Radio>
                            <Radio value={0}>禁用</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
}

export default User;