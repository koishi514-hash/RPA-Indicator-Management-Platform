import React, { useState, useEffect } from 'react';
import { Button, Modal, Form, Input, Tabs, Avatar, message } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { getProfile, updateProfile, updatePassword } from '@/services/profile';

const { TabPane } = Tabs;

// 完全匹配接口返回的类型
type UserInfo = {
    username?: string;
    nickname?: string;
    email?: string;
    phone?: string;
    roleNames?: string;
    avatar?: string | null;
    status?: number;
    createTime?: string;
};

function Profile() {
    const [userInfo, setUserInfo] = useState<UserInfo>({});
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [activeTab, setActiveTab] = useState('basic');
    const [form] = Form.useForm();
    const [passwordForm] = Form.useForm();

    useEffect(() => {
        loadUserInfo();
    }, []);

    const loadUserInfo = async () => {
        const res = await getProfile();
        setUserInfo(res.data);
    };

    const handleOpenModal = () => {
        form.setFieldsValue({
            nickname: userInfo.nickname,
            email: userInfo.email,
            phone: userInfo.phone,
        });
        passwordForm.resetFields();
        setIsModalVisible(true);
    };

    const handleCloseModal = () => setIsModalVisible(false);

    const handleSaveBasicInfo = async (values: any) => {
        const res = await updateProfile(values);
        setUserInfo(res.data);
        setIsModalVisible(false);
        loadUserInfo();
    };

    const handleUpdatePassword = async (values: any) => {
        const res = await updatePassword(values);
        if (res.code === 200) {
            passwordForm.resetFields();
            setIsModalVisible(false);
            message.success(res.msg);
        } else {
            message.error(res.msg);
        }
        loadUserInfo();
    };



    return (
        <div style={{ background: '#fff', padding: 20, borderRadius: 8 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h2>基本信息</h2>
                <Button type="primary" onClick={handleOpenModal}>修改信息</Button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: 30 }}>
                <Avatar size={128} icon={<UserOutlined />} />
                <label style={{ color: '#1890ff', cursor: 'pointer', marginTop: 10 }}>
                    更换头像
                    <input type="file" accept="image/*" style={{ display: 'none' }} />
                </label>
            </div>

            <div style={{ width: '100%', maxWidth: 600, margin: '0 auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <tbody>
                        <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                            <td style={{ padding: 12, width: 120, fontWeight: 'bold' }}>用户名</td>
                            <td style={{ padding: 12 }}>{userInfo.username}</td>
                        </tr>
                        <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                            <td style={{ padding: 12, width: 120, fontWeight: 'bold' }}>姓名</td>
                            <td style={{ padding: 12 }}>{userInfo.nickname}</td>
                        </tr>
                        <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                            <td style={{ padding: 12, width: 120, fontWeight: 'bold' }}>邮箱</td>
                            <td style={{ padding: 12 }}>{userInfo.email}</td>
                        </tr>
                        <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                            <td style={{ padding: 12, width: 120, fontWeight: 'bold' }}>手机号</td>
                            <td style={{ padding: 12 }}>{userInfo.phone}</td>
                        </tr>
                        <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                            <td style={{ padding: 12, width: 120, fontWeight: 'bold' }}>角色</td>
                            <td style={{ padding: 12 }}>
                                <span style={{ background: '#e6f7ff', color: '#1890ff', padding: '4px 12px', borderRadius: 12, fontSize: 12 }}>
                                    {userInfo.roleNames}
                                </span>
                            </td>
                        </tr>
                        <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                            <td style={{ padding: 12, width: 120, fontWeight: 'bold' }}>创建时间</td>
                            <td style={{ padding: 12 }}>{userInfo.createTime}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <Modal title="修改信息" open={isModalVisible} onCancel={handleCloseModal} footer={null} width={600}>
                <Tabs activeKey={activeTab} onChange={setActiveTab}>
                    <TabPane tab="基本信息" key="basic">
                        <Form form={form} onFinish={handleSaveBasicInfo} layout="vertical" style={{ marginTop: 20 }}>
                            <Form.Item name="nickname" label="姓名" rules={[{ required: true }]}><Input /></Form.Item>
                            <Form.Item name="email" label="邮箱" rules={[{ required: true, type: 'email' }]}><Input /></Form.Item>
                            <Form.Item name="phone" label="手机号" rules={[{ required: true }]}><Input /></Form.Item>
                            <Form.Item style={{ textAlign: 'right' }}>
                                <Button onClick={handleCloseModal}>取消</Button>
                                <Button type="primary" htmlType="submit" style={{ marginLeft: 8 }}>保存</Button>
                            </Form.Item>
                        </Form>
                    </TabPane>
                    <TabPane tab="修改密码" key="password">
                        <Form form={passwordForm} onFinish={handleUpdatePassword} layout="vertical" style={{ marginTop: 20 }}>
                            <Form.Item name="oldPassword" label="原密码" rules={[{ required: true }]}><Input.Password /></Form.Item>
                            <Form.Item name="newPassword" label="新密码" rules={[{ required: true, min: 6 }]}><Input.Password /></Form.Item>
                            <Form.Item name="confirmPassword" label="确认密码" rules={[{ required: true }, ({ getFieldValue }) => ({ validator(_, v) { return !v || getFieldValue('newPassword') === v ? Promise.resolve() : Promise.reject('两次密码不一致') } })]}><Input.Password /></Form.Item>
                            <Form.Item style={{ textAlign: 'right' }}>
                                <Button onClick={handleCloseModal}>取消</Button>
                                <Button type="primary" htmlType="submit" style={{ marginLeft: 8 }}>修改密码</Button>
                            </Form.Item>
                        </Form>
                    </TabPane>
                </Tabs>
            </Modal>
        </div>
    );
}

export default Profile;