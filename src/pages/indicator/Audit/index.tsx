// 指标审核页面 - 管理审核规则，支持动态添加判断条件和计算

import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, message, Space, Popconfirm, Select } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, MinusCircleOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { listQuotaRules, createQuotaRule, updateQuotaRule, deleteQuotaRule, getQuotaRuleDetail, getAllIndicators, calculateQuotaRule } from '@/services/indicator';

const { Option } = Select;

const AuditRule = () => {
    const [data, setData] = useState([]);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    const [keyword, setKeyword] = useState('');
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [calculateModalVisible, setCalculateModalVisible] = useState(false);
    const [calculateResult, setCalculateResult] = useState<any>(null);
    const [calculateLoading, setCalculateLoading] = useState(false);
    const [editId, setEditId] = useState<number | null>(null);
    const [calculateId, setCalculateId] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    interface Indicator {
        indicatorCode: string;
        indicatorName: string;
    }
    const [indicators, setIndicators] = useState<Indicator[]>([]);
    const [addForm] = Form.useForm();
    const [editForm] = Form.useForm();
    const [calculateForm] = Form.useForm();

    // 加载审核规则列表
    const loadData = async (pageNum = pagination.current, pageSize = pagination.pageSize) => {
        setLoading(true);
        try {
            const res = await listQuotaRules({ keyword, pageNum, pageSize });
            if (res.code === 200) {
                setData(res.data.records || []);
                setPagination({ current: res.data.pageNum || 1, pageSize: res.data.pageSize || 10, total: res.data.total || 0 });
            }
        } catch (error) {
            message.error('加载失败');
        } finally {
            setLoading(false);
        }
    };

    // 加载所有指标（供下拉选择）
    useEffect(() => {
        const loadIndicators = async () => {
            const res = await getAllIndicators();
            setIndicators(res.data || []);
        };
        loadIndicators();
    }, []);

    // 加载审核规则列表
    useEffect(() => {
        loadData();
    }, []);

    // 新增审核规则提交
    const handleAddSubmit = async () => {
        try {
            const values = await addForm.validateFields();
            const data = {
                ...values,
                conditions: values.conditions?.join('\n') || '',
                indicatorCodes: values.indicatorCodes?.join(',') || '',
            };
            await createQuotaRule(data);
            message.success('创建成功');
            setAddModalVisible(false);
            addForm.resetFields();
            loadData();
        } catch (error) {
            message.error('创建失败');
        }
    };

    // 编辑审核规则提交
    const handleEditSubmit = async () => {
        try {
            const values = await editForm.validateFields();
            const data = {
                ...values,
                id: editId,
                conditions: values.conditions?.join('\n') || '',
                indicatorCodes: values.indicatorCodes?.join(',') || '',
            };
            await updateQuotaRule(data);
            message.success('更新成功');
            setEditModalVisible(false);
            editForm.resetFields();
            setEditId(null);
            loadData();
        } catch (error) {
            message.error('更新失败');
        }
    };

    // 打开编辑弹窗并加载数据
    const handleEdit = async (record: any) => {
        setEditId(record.id);
        setEditModalVisible(true);
        const res = await getQuotaRuleDetail(record.id);
        if (res.code === 200) {
            const data = {
                ...res.data,
                conditions: res.data.conditions?.split('\n') || [],
                indicatorCodes: res.data.indicatorCodes?.split(',') || [],
            };
            editForm.setFieldsValue(data);
        }
    };

    // 删除审核规则
    const handleDelete = async (id: number) => {
        try {
            await deleteQuotaRule(id);
            message.success('删除成功');
            loadData();
        } catch (error) {
            message.error('删除失败');
        }
    };

    // 打开计算弹窗
    const handleCalculate = async (record: any) => {
        setCalculateId(record.id);
        setCalculateResult(null);
        setCalculateModalVisible(true);
        calculateForm.resetFields();
    };

    // 执行计算
    const handleExecuteCalculate = async () => {
        setCalculateLoading(true);
        try {
            const values = await calculateForm.validateFields();
            const res = await calculateQuotaRule({
                quotaRuleId: calculateId!,
                data: values.data ? JSON.parse(values.data) : undefined,
            });
            if (res.code === 200) {
                setCalculateResult(res.data);
                message.success('计算成功');
            }
        } catch (error) {
            message.error('计算失败');
        } finally {
            setCalculateLoading(false);
        }
    };

    // 搜索
    const handleSearch = () => {
        setPagination({ ...pagination, current: 1 });
        loadData(1, pagination.pageSize);
    };

    // 表格列配置
    const columns = [
        { title: '额度名称', dataIndex: 'quotaName', key: 'quotaName' },
        { title: '关联指标', dataIndex: 'indicatorCodes', key: 'indicatorCodes' },
        { title: '判断条件', dataIndex: 'conditions', key: 'conditions', render: (text: string) => text?.substring(0, 50) + (text?.length > 50 ? '...' : '') },
        { title: '额度计算', dataIndex: 'quotaCalculation', key: 'quotaCalculation' },
        { title: '结果变量', dataIndex: 'resultVarName', key: 'resultVarName' },
        { title: '计算结果', dataIndex: 'calculatedResult', key: 'calculatedResult' },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', render: (text: string) => text || '-' },
        {
            title: '操作',
            key: 'action',
            render: (_: any, record: { id: number; }) => (
                <Space>
                    <Button type="primary" onClick={() => handleCalculate(record)} icon={<ThunderboltOutlined />}>计算</Button>
                    <Button onClick={() => handleEdit(record)} icon={<EditOutlined />}>编辑</Button>
                    <Popconfirm title="确定删除？" onConfirm={() => handleDelete(record.id)}>
                        <Button danger icon={<DeleteOutlined />}>删除</Button>
                    </Popconfirm>
                </Space>
            )
        },
    ];

    return (
        <div>
            {/* 搜索区域 */}
            <div style={{ marginBottom: 16, display: 'flex', gap: 16 }}>
                <div style={{ display: 'flex', gap: 8 }}>
                    <Input
                        placeholder="搜索额度名称"
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                        style={{ width: 300 }}
                        onPressEnter={handleSearch}
                    />
                    <Button onClick={handleSearch} icon={<SearchOutlined />}>搜索</Button>
                </div>
                <Button type="primary" onClick={() => setAddModalVisible(true)} icon={<PlusOutlined />}>新增审核规则</Button>
            </div>

            {/* 数据表格 */}
            <Table
                columns={columns}
                dataSource={data}
                rowKey="id"
                pagination={pagination}
                loading={loading}
                onChange={(p) => loadData(p.current, p.pageSize)}
            />

            {/* 新增弹窗 */}
            <Modal
                title="新增审核规则"
                visible={addModalVisible}
                onCancel={() => { setAddModalVisible(false); addForm.resetFields(); }}
                onOk={handleAddSubmit}
                width={800}
            >
                <Form form={addForm} layout="vertical">
                    <Form.Item name="quotaName" label="额度名称" rules={[{ required: true, message: '请输入额度名称' }]}>
                        <Input placeholder="如：企业综合信用额度" />
                    </Form.Item>
                    <Form.Item name="indicatorCodes" label="关联指标" rules={[{ required: true, message: '请选择指标' }]}>
                        <Select mode="multiple" placeholder="请选择关联的指标">
                            {indicators.map(item => (
                                <Option key={item.indicatorCode} value={item.indicatorCode}>
                                    {item.indicatorName} ({item.indicatorCode})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item name="conditions" label="判断逻辑" rules={[{ required: true, message: '至少添加一个判断条件' }]}>
                        <Form.List name="conditions">
                            {(fields, { add, remove }) => (
                                <>
                                    {fields.map(({ key, name, ...restField }) => (
                                        <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                            <Form.Item
                                                {...restField}
                                                name={name}
                                                rules={[{ required: true, message: '请输入判断条件' }]}
                                            >
                                                <Input placeholder="输入判断条件，如：INV_F1_12M_DOWN_SALE_JSHJ_SUM.value > 100000" />
                                            </Form.Item>
                                            {fields.length > 1 && (
                                                <MinusCircleOutlined onClick={() => remove(name)} />
                                            )}
                                        </Space>
                                    ))}
                                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                                        添加判断条件
                                    </Button>
                                </>
                            )}
                        </Form.List>
                    </Form.Item>
                    <Form.Item name="quotaCalculation" label="额度计算公式" rules={[{ required: true, message: '请输入计算公式' }]}>
                        <Input placeholder="如：INV_F1_12M_DOWN_SALE_JSHJ_SUM.value * (1 - TAX_RATE_6M.value / 100) * 0.5" />
                    </Form.Item>
                    <Form.Item name="resultVarName" label="结果变量名" rules={[{ required: true, message: '请输入变量名' }]}>
                        <Input placeholder="如：creditLimit" />
                    </Form.Item>
                    <Form.Item name="outputTemplate" label="输出模板" rules={[{ required: true, message: '请输入输出模板' }]}>
                        <Input.TextArea placeholder='如：{"status": "${status}", "creditLimit": ${creditLimit}, "remark": "综合信用额度计算完成"}' rows={3} />
                    </Form.Item>
                </Form>
            </Modal>

            {/* 编辑弹窗 */}
            <Modal
                title="编辑审核规则"
                visible={editModalVisible}
                onCancel={() => { setEditModalVisible(false); editForm.resetFields(); setEditId(null); }}
                onOk={handleEditSubmit}
                width={800}
            >
                <Form form={editForm} layout="vertical">
                    <Form.Item name="quotaName" label="额度名称" rules={[{ required: true, message: '请输入额度名称' }]}>
                        <Input placeholder="如：企业综合信用额度" />
                    </Form.Item>
                    <Form.Item name="indicatorCodes" label="关联指标" rules={[{ required: true, message: '请选择指标' }]}>
                        <Select mode="multiple" placeholder="请选择关联的指标">
                            {indicators.map(item => (
                                <Option key={item.indicatorCode} value={item.indicatorCode}>
                                    {item.indicatorName} ({item.indicatorCode})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item name="conditions" label="判断逻辑" rules={[{ required: true, message: '至少添加一个判断条件' }]}>
                        <Form.List name="conditions">
                            {(fields, { add, remove }) => (
                                <>
                                    {fields.map(({ key, name, ...restField }) => (
                                        <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                                            <Form.Item
                                                {...restField}
                                                name={name}
                                                rules={[{ required: true, message: '请输入判断条件' }]}
                                            >
                                                <Input placeholder="输入判断条件，如：INV_F1_12M_DOWN_SALE_JSHJ_SUM.value > 100000" />
                                            </Form.Item>
                                            {fields.length > 1 && (
                                                <MinusCircleOutlined onClick={() => remove(name)} />
                                            )}
                                        </Space>
                                    ))}
                                    <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                                        添加判断条件
                                    </Button>
                                </>
                            )}
                        </Form.List>
                    </Form.Item>
                    <Form.Item name="quotaCalculation" label="额度计算公式" rules={[{ required: true, message: '请输入计算公式' }]}>
                        <Input placeholder="如：INV_F1_12M_DOWN_SALE_JSHJ_SUM.value * (1 - TAX_RATE_6M.value / 100) * 0.5" />
                    </Form.Item>
                    <Form.Item name="resultVarName" label="结果变量名" rules={[{ required: true, message: '请输入变量名' }]}>
                        <Input placeholder="如：creditLimit" />
                    </Form.Item>
                    <Form.Item name="outputTemplate" label="输出模板" rules={[{ required: true, message: '请输入输出模板' }]}>
                        <Input.TextArea placeholder='如：{"status": "${status}", "creditLimit": ${creditLimit}, "remark": "综合信用额度计算完成"}' rows={3} />
                    </Form.Item>
                </Form>
            </Modal>

            {/* 计算弹窗 */}
            <Modal
                title="计算额度"
                visible={calculateModalVisible}
                onCancel={() => { setCalculateModalVisible(false); setCalculateResult(null); }}
                onOk={handleExecuteCalculate}
                confirmLoading={calculateLoading}
                width={700}
            >
                <Form form={calculateForm} layout="vertical">
                    <Form.Item name="data" label="业务数据（可选）">
                        <Input.TextArea rows={4} />
                    </Form.Item>
                </Form>

                {calculateResult && (
                    <div style={{ marginTop: 16 }}>
                        <h4>计算结果：</h4>
                        <div style={{ background: '#f5f5f5', padding: 16, borderRadius: 4, wordBreak: 'break-all' }}>
                            <p><strong>状态：</strong> {calculateResult.status}</p>
                            <p><strong>规则名称：</strong> {calculateResult.quotaRuleName}</p>
                            <p><strong>输出：</strong></p>
                            <pre>{JSON.stringify(calculateResult.output, null, 2)}</pre>
                            <p><strong>计算时间：</strong> {calculateResult.calculatedAt}</p>
                        </div>
                    </div>
                )}
            </Modal>
        </div>
    );
};

export default AuditRule;
