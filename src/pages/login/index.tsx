import { useModel, useNavigate } from "@umijs/max";
import { Button, Form, Input, Card, message } from "antd";
import { UserOutlined, LockOutlined, LoginOutlined } from "@ant-design/icons";
import { userLogin, getAuthInfo } from "@/services/login";
import { useState } from "react";

const Login = () => {
    const navigate = useNavigate();
    const { refresh } = useModel('@@initialState');
    const [loading, setLoading] = useState(false);
    const [form] = Form.useForm();

    const login = async (value: any) => {
        setLoading(true);
        try {
            const loginRes = await userLogin(value);
            localStorage.setItem('token', loginRes.data.token);
            const authInfoRes = await getAuthInfo();
            localStorage.setItem('data', JSON.stringify(authInfoRes.data));
            await refresh();
            message.success('登录成功，欢迎回来！');
            navigate('/home');
        } catch (error) {
            message.error('登录失败，请检查用户名和密码');
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        form.resetFields();
    };

    return (
        <div style={{
            minHeight: '100vh',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
            position: 'relative',
            overflow: 'hidden',
        }}>
            {/* 装饰性背景元素 */}
            <div style={{
                position: 'absolute',
                top: '-10%',
                right: '-10%',
                width: '500px',
                height: '500px',
                borderRadius: '50%',
                background: 'rgba(255, 255, 255, 0.1)',
                animation: 'float 20s infinite ease-in-out',
            }} />
            <div style={{
                position: 'absolute',
                bottom: '-10%',
                left: '-5%',
                width: '400px',
                height: '400px',
                borderRadius: '50%',
                background: 'rgba(255, 255, 255, 0.08)',
                animation: 'float 15s infinite ease-in-out reverse',
            }} />

            <Card
                style={{
                    width: '100%',
                    maxWidth: '420px',
                    borderRadius: '20px',
                    boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
                    backdropFilter: 'blur(10px)',
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    border: 'none',
                    animation: 'fadeInUp 0.6s ease-out',
                }}
                bodyStyle={{ padding: '40px 30px' }}
            >
                {/* Logo/标题区域 */}
                <div style={{ textAlign: 'center', marginBottom: '40px' }}>
                    <div style={{
                        width: '80px',
                        height: '80px',
                        margin: '0 auto 20px',
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        borderRadius: '20px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 10px 30px rgba(102, 126, 234, 0.4)',
                    }}>
                        <LoginOutlined style={{ fontSize: '40px', color: '#fff' }} />
                    </div>
                    <h1 style={{
                        fontSize: '28px',
                        fontWeight: '600',
                        margin: '0 0 8px 0',
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent',
                        backgroundClip: 'text',
                    }}>
                        欢迎回来
                    </h1>
                    <p style={{
                        color: '#8c8c8c',
                        fontSize: '14px',
                        margin: 0,
                    }}>
                        请登录您的账号以继续
                    </p>
                </div>

                <Form
                    form={form}
                    onFinish={login}
                    size="large"
                >
                    <Form.Item
                        name="username"
                        validateFirst
                        rules={[
                            { required: true, message: '请输入用户名' },
                            { min: 1, message: '用户名最少1个字符' },
                        ]}
                    >
                        <Input
                            prefix={<UserOutlined style={{ color: '#bfbfbf' }} />}
                            placeholder="请输入用户名"
                            maxLength={20}
                            minLength={1}
                            style={{
                                borderRadius: '12px',
                                height: '48px',
                                fontSize: '15px',
                            }}
                        />
                    </Form.Item>

                    <Form.Item
                        name="password"
                        validateFirst
                        rules={[
                            { required: true, message: '请输入密码' },
                            { min: 6, message: '密码最少6个字符' },
                        ]}
                    >
                        <Input.Password
                            prefix={<LockOutlined style={{ color: '#bfbfbf' }} />}
                            placeholder="请输入密码"
                            maxLength={20}
                            minLength={6}
                            style={{
                                borderRadius: '12px',
                                height: '48px',
                                fontSize: '15px',
                            }}
                        />
                    </Form.Item>

                    <Form.Item style={{ marginTop: '30px', marginBottom: '16px' }}>
                        <Button
                            type="primary"
                            htmlType="submit"
                            loading={loading}
                            block
                            style={{
                                height: '48px',
                                borderRadius: '12px',
                                fontSize: '16px',
                                fontWeight: '500',
                                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                border: 'none',
                                boxShadow: '0 8px 20px rgba(102, 126, 234, 0.3)',
                                transition: 'all 0.3s ease',
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.transform = 'translateY(-2px)';
                                e.currentTarget.style.boxShadow = '0 12px 25px rgba(102, 126, 234, 0.4)';
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.transform = 'translateY(0)';
                                e.currentTarget.style.boxShadow = '0 8px 20px rgba(102, 126, 234, 0.3)';
                            }}
                        >
                            登 录
                        </Button>
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 0 }}>
                        <Button
                            type="text"
                            htmlType="reset"
                            onClick={handleReset}
                            block
                            style={{
                                height: '40px',
                                borderRadius: '12px',
                                fontSize: '14px',
                                color: '#8c8c8c',
                                transition: 'all 0.3s ease',
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.color = '#667eea';
                                e.currentTarget.style.backgroundColor = '#f5f5f5';
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.color = '#8c8c8c';
                                e.currentTarget.style.backgroundColor = 'transparent';
                            }}
                        >
                            重 置
                        </Button>
                    </Form.Item>
                </Form>

                {/* 底部提示 */}
                <div style={{
                    textAlign: 'center',
                    marginTop: '30px',
                    paddingTop: '20px',
                    borderTop: '1px solid #f0f0f0',
                }}>
                    <p style={{
                        color: '#bfbfbf',
                        fontSize: '12px',
                        margin: 0,
                    }}>
                        © 2024 RPA数据采集平台. All rights reserved.
                    </p>
                </div>
            </Card>
        </div>
    );
};

export default Login;