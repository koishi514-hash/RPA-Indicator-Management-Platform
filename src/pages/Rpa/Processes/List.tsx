import CodeEditor from '@/pages/components';
import { createProcess, deleteProcess, listProcesses, updateProcess, listProcessSteps, saveProcessSteps } from '@/services/processes';
import { Button, Card, Form, Input, Modal, Radio, Select, Space, Table, Tag } from 'antd';
import React, { useEffect, useState } from 'react';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';

const ProcessList = () => {

    const [form] = Form.useForm();
    const [data, setData] = useState([]);
    const [addForm] = Form.useForm();
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editForm] = Form.useForm();
    const [currentProcess, setCurrentProcess] = useState<any>(null);
    const [steps, setSteps] = useState<any[]>([]);
    const [designVisible, setDesignVisible] = useState(false);
    const [currentStep, setCurrentStep] = useState(0);
    // 添加步骤
    const handleAddStep = () => {
        const newStep = {
            stepOrder: steps.length + 1,
            stepName: `步骤${steps.length + 1}`,
            stepType: 'Java爬虫代码',
            codeContent: '',
        };
        setSteps([...steps, newStep]);
    };
    const handleDesign = async (record: any) => {
        setCurrentProcess(record);
        try {
            // 调用 listProcessSteps 获取该流程的步骤数据
            const res = await listProcessSteps(record.processCode);

            if (res.data && res.data.length > 0) {
                // 有步骤数据，直接使用
                setSteps(res.data);
            } else {
                // 没有步骤数据，初始化一个默认步骤
                setSteps([{
                    stepOrder: 1,
                    stepName: '步骤1',
                    stepType: 'Java爬虫代码',
                    codeContent: '',
                }]);
            }
            setCurrentStep(0); // 默认选中第一个步骤
            setDesignVisible(true);
        } catch (error) {
            console.error('加载步骤失败:', error);
            // 失败时也打开设计器，但使用空步骤
            setSteps([{
                stepOrder: 1,
                stepName: '步骤1',
                stepType: 'Java爬虫代码',
                codeContent: '',
            }]);
            setCurrentStep(0);
            setDesignVisible(true);
        }
    };
    // 删除步骤
    const handleDeleteStep = (index: number) => {
        const newSteps = steps.filter((_, i) => i !== index);
        // 重新排序
        newSteps.forEach((step, i) => {
            step.stepOrder = i + 1;
            step.stepName = `步骤${i + 1}`;
        });
        setSteps(newSteps);
        if (currentStep >= newSteps.length) {
            setCurrentStep(newSteps.length - 1);
        }
    };

    //步骤类型切换函数
    const handleStepTypeChange = (value: string) => {
        const newSteps = [...steps];
        newSteps[currentStep].stepType = value;
        setSteps(newSteps);
    };

    const handleSaveDesign = async () => {
        await saveProcessSteps({
            processCode: currentProcess?.processCode,
            steps: steps.map((step, index) => ({
                stepOrder: step.stepOrder || index + 1,
                stepName: step.stepName,
                stepType: step.stepType,
                codeContent: step.codeContent,
            })),
        });
        setDesignVisible(false);
        loadData(); // 刷新列表数据，更新步骤数
    };

    const handleCodeChange = (value: string) => {
        const newSteps = [...steps];
        newSteps[currentStep].codeContent = value;
        setSteps(newSteps);
    };

    const handleEdit = (record: any) => {
        setCurrentProcess(record);
        editForm.setFieldsValue({
            processId: record.id,
            processCode: record.processCode,
            processName: record.processName,
            description: record.description,
            status: record.status,
        });
        setEditModalVisible(true);
    };

    const loadData = async () => {
        const values = form.getFieldsValue();
        const res = await listProcesses(values);
        setData(res.data.records);
    };

    const handleEditSubmit = async () => {
        const values = await editForm.validateFields();
        await updateProcess(values);
        setEditModalVisible(false);
        editForm.resetFields();
        loadData();
    };

    useEffect(() => {
        loadData();
    }, []);

    const handleSearch = () => {
        loadData();
    };

    const handleReset = () => {
        form.resetFields();
        loadData();
    };

    const addData = async () => {
        const values = await addForm.validateFields();
        await createProcess(values);
        setAddModalVisible(false);
        addForm.resetFields();
        loadData();
    };

    const handleAdd = () => {
        setAddModalVisible(true);
    };

    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: `确定要删除流程 "${record.processCode}" 吗？`,
            okText: '确定',
            cancelText: '取消',
            onOk: async () => {
                await deleteProcess(record.processCode);
                loadData();
            },
        });
    };
    const columns = [
        {
            title: "序号",
            key: "index",
            width: 80,
            render: (_: any, __: any, index: number) => index + 1,
        },
        {
            title: "流程编码",
            dataIndex: "processCode",
            key: "processCode",
        },
        {
            title: "流程名称",
            dataIndex: "processName",
            key: "processName",
        },
        {
            title: "描述",
            dataIndex: "description",
            key: "description",
        },
        {
            title: "步骤数",
            dataIndex: "stepCount",
            key: "stepCount",
        },
        {
            title: "状态",
            dataIndex: "status",
            key: "status",
            render: (status: number) => status === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">禁用</Tag>,
        },
        {
            title: "创建时间",
            dataIndex: "createTime",
            key: "createTime",
        },
        {
            title: "操作",
            dataIndex: "operation",
            render: (_: any, record: any) => (
                <Space size={16}>
                    <a onClick={() => handleEdit(record)}>编辑</a>
                    <a onClick={() => handleDesign(record)}>设计</a>
                    <a style={{ color: '#f56c6c' }} onClick={() => handleDelete(record)}>删除</a>
                </Space>
            ),
        },
    ];
    // 1. 添加步骤类型对应的编辑器配置
    const getEditorConfig = (stepType: string) => {
        const configs: Record<string, { language: string; placeholder: string }> = {
            'Java爬虫代码': { language: 'java', placeholder: '// 编写 Java 爬虫代码...' },
            'JavaScript代码': { language: 'javascript', placeholder: '// 编写 JavaScript 代码...' },
            'Python代码': { language: 'python', placeholder: '# 编写 Python 代码...' },
            '数据清洗': { language: 'sql', placeholder: '-- 编写数据清洗 SQL...' },
            '数据解析': { language: 'json', placeholder: '// 配置解析规则...' },
        };
        return configs[stepType] || { language: 'text', placeholder: '编写代码...' };
    };
    return (
        <>
            <h1 style={{ margin: '0 0 24px 0', fontSize: 28, fontWeight: 600 }}>流程列表</h1>
            <div style={{ marginBottom: 20 }}>
                <Button type="primary" onClick={handleAdd}>新增流程</Button>
            </div>

            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="processName" label="流程名称">
                        <Input placeholder="请输入" style={{ width: 220 }} />
                    </Form.Item>
                    <Form.Item name="processCode" label="流程编码">
                        <Input placeholder="请输入" style={{ width: 220 }} />
                    </Form.Item>
                    <Form.Item name="status" label="状态">
                        <Select placeholder="请选择" style={{ width: 220 }} allowClear>
                            <Select.Option value={1}>启用</Select.Option>
                            <Select.Option value={0}>禁用</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" onClick={handleSearch}>查询</Button>
                        <Button onClick={handleReset} style={{ marginLeft: 8 }}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>
            <Modal
                title="新增流程"
                open={addModalVisible}
                onOk={addData}
                onCancel={() => setAddModalVisible(false)}
                okText="确定"
                cancelText="取消"
            >
                <Form form={addForm} layout="vertical">
                    <Form.Item
                        name="processCode"
                        label="流程编码"
                        rules={[{ required: true, message: '请输入流程编码' }]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item
                        name="processName"
                        label="流程名称"
                        rules={[{ required: true, message: '请输入流程名称' }]}
                    >
                        <Input />
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
                            <Radio value={1}>启用</Radio>
                            <Radio value={0}>禁用</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal
                title="编辑流程"
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
                    <Form.Item name="processId" hidden>
                        <Input />
                    </Form.Item>
                    <Form.Item
                        name="processCode"
                        label="流程编码"
                        rules={[{ required: true, message: '请输入流程编码' }]}
                    >
                        <Input disabled />
                    </Form.Item>
                    <Form.Item
                        name="processName"
                        label="流程名称"
                        rules={[{ required: true, message: '请输入流程名称' }]}
                    >
                        <Input />
                    </Form.Item>
                    <Form.Item name="description" label="描述">
                        <Input.TextArea rows={4} />
                    </Form.Item>
                    <Form.Item name="status" label="状态" rules={[{ required: true }]}>
                        <Radio.Group>
                            <Radio value={1}>启用</Radio>
                            <Radio value={0}>禁用</Radio>
                        </Radio.Group>
                    </Form.Item>
                </Form>
            </Modal>
            <Modal
                title="流程设计"
                open={designVisible}
                onOk={handleSaveDesign}
                onCancel={() => setDesignVisible(false)}
                width="90%"
                style={{ top: 20 }}
                okText="保存设计"
                cancelText="取消"
            >
                <div style={{ display: 'flex', gap: 24, minHeight: 500 }}>
                    {/* 左侧步骤列表 */}
                    <div style={{ width: 280, borderRight: '1px solid #f0f0f0', paddingRight: 16 }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                            <h4 style={{ margin: 0 }}>步骤列表</h4>
                            <Button
                                type="primary"
                                size="small"
                                icon={<PlusOutlined />}
                                onClick={handleAddStep}
                            >
                                添加步骤
                            </Button>
                        </div>
                        <div>
                            {steps.map((step, index) => (
                                <div
                                    key={index}
                                    style={{
                                        padding: '12px 16px',
                                        margin: '4px 0',
                                        cursor: 'pointer',
                                        background: currentStep === index ? '#e6f7ff' : 'transparent',
                                        borderLeft: currentStep === index ? '3px solid #1890ff' : '3px solid transparent',
                                        borderRadius: 4,
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center',
                                    }}
                                >
                                    <div
                                        style={{ flex: 1 }}
                                        onClick={() => setCurrentStep(index)}
                                    >
                                        <div style={{ fontWeight: currentStep === index ? 600 : 400 }}>
                                            {step.stepName || `步骤${step.stepOrder || index + 1}`}
                                        </div>
                                        <div style={{ fontSize: 12, color: '#999', marginTop: 4 }}>
                                            {step.stepType || 'Java爬虫代码'}
                                        </div>
                                        {/* 添加代码缩略显示 */}
                                        {step.codeContent && (
                                            <div style={{
                                                fontSize: 11,
                                                color: '#666',
                                                marginTop: 6,
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                whiteSpace: 'nowrap',
                                                background: '#f9f9f9',
                                                padding: '2px 6px',
                                                borderRadius: 4,
                                                maxWidth: '100%'
                                            }}>
                                                {step.codeContent.substring(0, 40)}{step.codeContent.length > 40 ? '...' : ''}
                                            </div>
                                        )}

                                    </div>
                                    {steps.length > 1 && (
                                        <Button
                                            type="text"
                                            danger
                                            size="small"
                                            icon={<DeleteOutlined />}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleDeleteStep(index);
                                            }}
                                        />
                                    )}
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* 右侧代码编辑器 */}
                    <div style={{ flex: 1 }}>
                        {steps[currentStep] && (
                            <>
                                <div style={{ marginBottom: 16 }}>
                                    <Input
                                        value={steps[currentStep].stepName}
                                        onChange={(e) => {
                                            const newSteps = [...steps];
                                            newSteps[currentStep].stepName = e.target.value;
                                            setSteps(newSteps);
                                        }}
                                        placeholder="步骤名称"
                                        style={{ marginBottom: 12 }}
                                    />
                                    <Select
                                        value={steps[currentStep].stepType}
                                        onChange={handleStepTypeChange}
                                        style={{ width: 200 }}
                                    >
                                        <Select.Option value="Java爬虫代码">Java爬虫代码</Select.Option>
                                        <Select.Option value="JavaScript代码">JavaScript代码</Select.Option>
                                        <Select.Option value="Python代码">Python代码</Select.Option>
                                        <Select.Option value="数据清洗">数据清洗</Select.Option>
                                        <Select.Option value="数据解析">数据解析</Select.Option>
                                    </Select>

                                    <div style={{
                                        background: '#f5f5f5',
                                        padding: '8px 16px',
                                        borderRadius: 4,
                                    }}>
                                        <span style={{ color: '#666' }}>当前编辑器：</span>
                                        <span style={{ fontWeight: 500 }}>
                                            {steps[currentStep].stepType || 'Java爬虫代码'}
                                            {steps[currentStep].codeContent &&
                                                ` (${steps[currentStep].codeContent.split('\n').length} 行)`}
                                        </span>
                                    </div>
                                </div>
                                <CodeEditor
                                    language={getEditorConfig(steps[currentStep].stepType).language}
                                    value={steps[currentStep].codeContent || ''}
                                    onChange={handleCodeChange}

                                />
                            </>
                        )}
                    </div>
                </div>
            </Modal>
            <Table
                dataSource={data}
                columns={columns}
                rowKey="processId"
                pagination={false}
            />
        </>

    );
};

export default ProcessList;

