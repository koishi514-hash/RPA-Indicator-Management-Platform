import React, { useEffect, useState } from 'react';
import { Button, Card, DatePicker, Form, Input, InputNumber, Modal, Select, Space, Table, Tag, message } from 'antd';
import { PlusOutlined, MinusOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { getCollectionList, getCollectionDetail, deleteCollection, addCollection } from '@/services/collection';
import { useNavigate } from '@umijs/max';

const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

const DataCollectionPage = () => {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [addForm] = Form.useForm();
    const [data, setData] = useState<any[]>([]);
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [currentDetail, setCurrentDetail] = useState<any | null>(null);
    const [taskIdCount, setTaskIdCount] = useState(1);
    const [loading, setLoading] = useState(false);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    const [statistics, setStatistics] = useState({ totalCollection: 0, success: 0, pending: 0, failed: 0 });

    const statusConfig: Record<string, { text: string; color: string }> = {
        'success': { text: '成功', color: 'green' },
        'parsed': { text: '采集中', color: 'gold' },
        'collected': { text: '失败', color: 'red' },
    };

    const loadData = async (pageNum = 1, pageSize = 10) => {
        setLoading(true);
        try {
            const values = form.getFieldsValue();
            const params: any = { pageNum, pageSize };
            if (values.taskCode) params.taskCode = values.taskCode;
            if (values.keyword) params.keyword = values.keyword;
            if (values.status) params.status = values.status;
            if (values.collectionTime?.length === 2) {
                params.collectionTimeStart = values.collectionTime[0].format('YYYY-MM-DDTHH:mm:ss');
                params.collectionTimeEnd = values.collectionTime[1].format('YYYY-MM-DDTHH:mm:ss');
            }
            const res = await getCollectionList(params);
            setData(res.data.records || []);
            setPagination({ current: pageNum, pageSize, total: res.data.total || 0 });
            if (pageNum === 1 && !params.taskId && !params.keyword && !params.status && !params.collectionTimeStart) {
                setStatistics(res.data.statistics || { totalCollection: 0, success: 0, parsed: 0, failed: 0 });
            }
        } catch {
            message.error('加载数据失败');
        } finally {
            setLoading(false);
        }
    };

    const handleViewDetail = async (record: any) => {
        try {
            const res = await getCollectionDetail(record.collectionId);
            setCurrentDetail(res.data);
            setDetailModalVisible(true);
        } catch {
            message.error('获取详情失败');
        }
    };

    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除采集记录吗？',
            onOk: async () => {
                try {
                    await deleteCollection(record.collectionId);
                    message.success('删除成功');
                    loadData(pagination.current, pagination.pageSize);
                } catch {
                    message.error('删除失败');
                }
            },
        });
    };

    const handleAdd = () => {
        setAddModalVisible(true);
        addForm.resetFields();
        setTaskIdCount(1);
    };

    const handleAddSubmit = async () => {
        try {
            const values = await addForm.validateFields();
            await addCollection({ ...values, taskId: taskIdCount, collectionTime: new Date().toISOString() });
            message.success('新增成功');
            setAddModalVisible(false);
            loadData(pagination.current, pagination.pageSize);
        } catch {
            message.error('新增失败');
        }
    };
    const fixInvalidJSON = (str: string): string => {
        if (!str) return str;

        let fixed = str;

        // 1. 替换中文标点
        fixed = fixed.replace(/：/g, ':');
        fixed = fixed.replace(/，/g, ',');
        fixed = fixed.replace(/“/g, '"');
        fixed = fixed.replace(/”/g, '"');

        // 2. 为没有引号的键名添加双引号
        fixed = fixed.replace(/(\s*)([a-zA-Z_][a-zA-Z0-9_]*)(\s*):/g, '$1"$2"$3:');

        // 3. 处理单引号
        fixed = fixed.replace(/'/g, '"');

        // 4. 移除末尾多余的逗号
        fixed = fixed.replace(/,\s*}/g, '}');
        fixed = fixed.replace(/,\s*\]/g, ']');

        return fixed;
    };
    useEffect(() => { loadData(); }, []);

    const columns: ColumnsType<any> = [
        { title: '序号', key: 'index', width: 80, render: (_, __, i) => (pagination.current - 1) * pagination.pageSize + i + 1 },
        { title: '任务编码', dataIndex: 'taskCode', key: 'taskCode', width: 200, render: (v) => <a style={{ color: '#1890ff' }} onClick={() => navigate(`/Rpa/Tasks/detail/${v}`)}>{v}</a> },
        { title: '状态', dataIndex: 'status', key: 'status', width: 100, render: (s) => <Tag color={statusConfig[s]?.color}>{statusConfig[s]?.text}</Tag> },
        { title: '纳税人识别号', dataIndex: 'taxNo', key: 'taxNo', width: 200 },
        { title: '企业名称', dataIndex: 'enterpriseName', key: 'enterpriseName', width: 200 },
        { title: '数据来源', dataIndex: 'dataSource', key: 'dataSource', width: 180 },
        { title: '采集时间', dataIndex: 'collectionTime', key: 'collectionTime', width: 180, render: (v) => v?.replace('T', ' ') },
        {
            title: '操作', key: 'action', width: 150, fixed: 'right', render: (_, r) => (
                <Space size={16}>
                    <a onClick={() => handleViewDetail(r)}>查看</a>
                    <a style={{ color: '#f56c6c' }} onClick={() => handleDelete(r)}>删除</a>
                </Space>
            ),
        },
    ];

    return (
        <>
            <h1 style={{ margin: 0, marginBottom: 24, fontSize: 28, fontWeight: 600 }}>数据采集</h1>
            <div style={{ marginBottom: 20, display: 'flex', gap: 16 }}>
                <Card style={{ flex: 1, textAlign: 'center' }}>
                    <div style={{ fontSize: 32, fontWeight: 600 }}>{statistics.totalCollection}</div>
                    <div style={{ color: '#666' }}>总采集数</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#f6ffed' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#52c41a' }}>{statistics.success}</div>
                    <div style={{ color: '#666' }}>成功</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#fffbe6' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#faad14' }}>{statistics.pending}</div>
                    <div style={{ color: '#666' }}>采集中</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#fff2f0' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#ff4d4f' }}>{statistics.failed}</div>
                    <div style={{ color: '#666' }}>失败</div>
                </Card>
            </div>


            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="taskCode" label="任务编码">
                        <Input placeholder="请输入" style={{ width: 180 }} />
                    </Form.Item>
                    <Form.Item name="keyword" label="关键字">
                        <Input placeholder="纳税人识别号/企业名称" style={{ width: 220 }} />
                    </Form.Item>
                    <Form.Item name="status" label="状态">
                        <Select placeholder="请选择" style={{ width: 150 }} allowClear>
                            <Option value="success">成功</Option>
                            <Option value="parsed">采集中</Option>
                            <Option value="collected">失败</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="collectionTime" label="采集时间">
                        <RangePicker style={{ width: 360 }} showTime format="YYYY-MM-DD HH:mm:ss" />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" icon={<SearchOutlined />} onClick={() => loadData(1, pagination.pageSize)}>查询</Button>
                        <Button icon={<ReloadOutlined />} onClick={() => { form.resetFields(); loadData(1, pagination.pageSize); }} style={{ marginLeft: 8 }}>重置</Button>
                        <Button type="primary" onClick={handleAdd} style={{ marginLeft: 8 }}>新增采集</Button>
                    </Form.Item>
                </Form>
            </Card>

            <Table columns={columns} dataSource={data} rowKey="collectionId" pagination={pagination} onChange={(p) => loadData(p.current, p.pageSize)} loading={loading} scroll={{ x: 1500 }} />

            <Modal title="新增采集记录" open={addModalVisible} onOk={handleAddSubmit} onCancel={() => setAddModalVisible(false)} width={600}>
                <Form form={addForm} layout="vertical">
                    <Form.Item label="任务ID" required>
                        <Space>
                            <InputNumber value={taskIdCount} onChange={(v) => setTaskIdCount(v || 1)} />
                        </Space>
                    </Form.Item>
                    <Form.Item name="taxNo" label="纳税人识别号">
                        <Input placeholder="请输入纳税人识别号" />
                    </Form.Item>
                    <Form.Item name="enterpriseName" label="企业名称">
                        <Input placeholder="请输入企业名称" />
                    </Form.Item>
                    <Form.Item name="dataSource" label="数据来源">
                        <Input placeholder="请输入数据来源" />
                    </Form.Item>
                    <Form.Item name="status" label="状态" initialValue="success">
                        <Select>
                            <Option value="success">成功</Option>
                            <Option value="pending">采集中</Option>
                            <Option value="failed">失败</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="rawData" label="原始数据">
                        <TextArea rows={4} placeholder="JSON格式" />
                    </Form.Item>
                    <Form.Item name="errorMsg" label="错误信息">
                        <TextArea rows={2} />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal title="采集数据详情" open={detailModalVisible} onCancel={() => setDetailModalVisible(false)} footer={null} width={700}>
                {currentDetail && (
                    <div style={{ padding: '16px 0' }}>
                        <p><strong>任务ID:</strong>{currentDetail.taskId}</p>
                        <p><strong>纳税人识别号：</strong>{currentDetail.taxNo || '-'}</p>
                        <p><strong>企业名称：</strong>{currentDetail.enterpriseName || '-'}</p>
                        <p><strong>状态：</strong><Tag color={statusConfig[currentDetail.status]?.color}>{statusConfig[currentDetail.status]?.text}</Tag></p>
                        <p><strong>数据来源：</strong>{currentDetail.dataSource || '-'}</p>
                        <p><strong>采集时间：</strong>{currentDetail.collectionTime?.replace('T', ' ')}</p>
                        {currentDetail.errorMsg && <p><strong>错误信息：</strong><span style={{ color: '#f56c6c' }}>{currentDetail.errorMsg}</span></p>}
                        <p><strong>原始数据：</strong></p>
                        <pre style={{ background: '#f5f5f5', padding: 12, borderRadius: 4, maxHeight: 300, overflow: 'auto', fontSize: 12 }}>
                            {(() => {
                                const rawData = currentDetail.rawData;

                                if (!rawData) return '-';

                                if (typeof rawData === 'string') {
                                    try {
                                        const parsed = JSON.parse(rawData);
                                        return JSON.stringify(parsed, null, 2);
                                    } catch (e) {
                                        // 尝试修复
                                        try {
                                            const fixed = fixInvalidJSON(rawData);
                                            const parsed = JSON.parse(fixed);
                                            return JSON.stringify(parsed, null, 2);
                                        } catch (e2) {
                                            // 修复失败，显示原始内容并标记
                                            return `// JSON 格式错误，原始数据：\n${rawData}`;
                                        }
                                    }
                                }

                                if (typeof rawData === 'object') {
                                    try {
                                        return JSON.stringify(rawData, null, 2);
                                    } catch {
                                        return String(rawData);
                                    }
                                }

                                return String(rawData);
                            })()}
                        </pre>
                    </div>
                )}
            </Modal>
        </>
    );
};

export default DataCollectionPage;