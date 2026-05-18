import React, { useEffect, useState } from 'react';
import { Button, Card, DatePicker, Form, Input, Modal, Select, Space, Table, Tag, message } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from '@umijs/max';
import { getProcessingList, getProcessingDetail, deleteProcessing } from '@/services/processing';

const { Option } = Select;
const { RangePicker } = DatePicker;

const DataProcessing: React.FC = () => {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [data, setData] = useState<any[]>([]);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [currentDetail, setCurrentDetail] = useState<any | null>(null);
    const [loading, setLoading] = useState(false);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    const [statistics, setStatistics] = useState({ totalProcessing: 0, success: 0, pending: 0, failed: 0 });

    const statusConfig: Record<string, { text: string; color: string }> = {
        'processed': { text: '已加工', color: 'default' },
        'exported': { text: '已导出', color: 'green' },
        'failed': { text: '加工失败', color: 'red' },
    };

    const loadData = async (pageNum = 1, pageSize = 10) => {
        setLoading(true);
        try {
            const values = form.getFieldsValue();
            const params: any = { pageNum, pageSize };
            if (values.taskId) params.taskId = values.taskId;
            if (values.status !== undefined && values.status !== '') params.status = values.status;
            if (values.processingTime?.length === 2) {
                params.processingTimeStart = values.processingTime[0].format('YYYY-MM-DDTHH:mm:ss');
                params.processingTimeEnd = values.processingTime[1].format('YYYY-MM-DDTHH:mm:ss');
            }
            const res = await getProcessingList(params);
            setData(res.data.records || []);
            setPagination({ current: pageNum, pageSize, total: res.data.total || 0 });
            if (pageNum === 1 && !params.taskId && params.status === undefined && !params.processingTimeStart) {
                setStatistics(res.data.statistics || { totalProcessing: 0, success: 0, pending: 0, failed: 0 });
            }
        } catch {
            message.error('加载数据失败');
        } finally {
            setLoading(false);
        }
    };

    const handleViewDetail = async (record: any) => {
        try {
            const res = await getProcessingDetail(record.processingId);
            setCurrentDetail(res.data);
            setDetailModalVisible(true);
        } catch {
            message.error('获取详情失败');
        }
    };

    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除加工记录吗？',
            onOk: async () => {
                try {
                    await deleteProcessing(record.processingId);
                    message.success('删除成功');
                    loadData(pagination.current, pagination.pageSize);
                } catch {
                    message.error('删除失败');
                }
            },
        });
    };

    useEffect(() => { loadData(); }, []);

    const columns: ColumnsType<any> = [
        { title: '序号', key: 'index', width: 80, render: (_, __, i) => (pagination.current - 1) * pagination.pageSize + i + 1 },
        { title: '任务ID', dataIndex: 'taskId', key: 'taskId', width: 100, },
        { title: '任务编码', dataIndex: 'taskCode', key: 'taskCode', width: 200, render: (v) => <a style={{ color: '#1890ff' }} onClick={() => navigate(`/rpa/tasks/detail/${v}`)}>{v}</a> },
        { title: '解析ID', dataIndex: 'parsingId', key: 'parsingId', width: 100, render: (v) => <a style={{ color: '#1890ff' }} onClick={() => navigate(`/rpa/data/analysis`)}>{v}</a> },
        { title: '状态', dataIndex: 'status', key: 'status', width: 100, render: (s) => <Tag color={statusConfig[s]?.color}>{statusConfig[s]?.text}</Tag> },
        { title: '加工时间', dataIndex: 'processingTime', key: 'processingTime', width: 180, render: (v) => v?.replace('T', ' ') },
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
            <h1 style={{ margin: 0, marginBottom: 24, fontSize: 28, fontWeight: 600 }}>数据加工</h1>

            <div style={{ marginBottom: 20, display: 'flex', gap: 16 }}>
                <Card style={{ flex: 1, textAlign: 'center' }}>
                    <div style={{ fontSize: 32, fontWeight: 600 }}>{statistics.totalProcessing}</div>
                    <div style={{ color: '#666' }}>总加工数</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#f6ffed' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#52c41a' }}>{statistics.success}</div>
                    <div style={{ color: '#666' }}>已导出</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#fffbe6' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#faad14' }}>{statistics.pending}</div>
                    <div style={{ color: '#666' }}>已加工</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#fff2f0' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#ff4d4f' }}>{statistics.failed}</div>
                    <div style={{ color: '#666' }}>失败</div>
                </Card>
            </div>

            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="taskId" label="任务ID">
                        <Input placeholder="请输入" style={{ width: 180 }} />
                    </Form.Item>
                    <Form.Item name="status" label="状态">
                        <Select placeholder="请选择" style={{ width: 150 }} allowClear>
                            <Option value="pending">已加工</Option>
                            <Option value="exported">已导出</Option>
                            <Option value="failed">加工失败</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="processingTime" label="加工时间">
                        <RangePicker style={{ width: 360 }} showTime format="YYYY-MM-DD HH:mm:ss" />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" icon={<SearchOutlined />} onClick={() => loadData(1, pagination.pageSize)}>查询</Button>
                        <Button icon={<ReloadOutlined />} onClick={() => { form.resetFields(); loadData(1, pagination.pageSize); }} style={{ marginLeft: 8 }}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>

            <Table columns={columns} dataSource={data} rowKey="processingId" pagination={pagination} onChange={(p) => loadData(p.current, p.pageSize)} loading={loading} scroll={{ x: 1100 }} />

            <Modal title="加工数据详情" open={detailModalVisible} onCancel={() => setDetailModalVisible(false)} footer={null} width={800}>
                {currentDetail && (
                    <div style={{ padding: '16px 0' }}>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px 24px', marginBottom: 16 }}>
                            <div><strong>任务ID：</strong><a style={{ color: '#1890ff' }} onClick={() => { setDetailModalVisible(false); navigate(`/rpa/tasks/detail/${currentDetail.taskId}`); }}>{currentDetail.taskId}</a></div>
                            <div><strong>解析ID：</strong><a style={{ color: '#1890ff' }} onClick={() => { setDetailModalVisible(false); navigate(`/rpa/data/analysis`); }}>{currentDetail.parsingId}</a></div>
                            <div><strong>纳税人识别号：</strong>{currentDetail.taxNo || '-'}</div>
                            <div><strong>企业名称：</strong>{currentDetail.enterpriseName || '-'}</div>
                            <div><strong>状态：</strong><Tag color={statusConfig[currentDetail.status]?.color}>{statusConfig[currentDetail.status]?.text}</Tag></div>
                            <div><strong>加工时间：</strong>{currentDetail.processingTime?.replace('T', ' ')}</div>
                            <div style={{ gridColumn: '1 / -1' }}><strong>错误信息：</strong>{currentDetail.errorMsg || '-'}</div>
                        </div>
                        <p><strong>加工数据：</strong></p>
                        <pre style={{ background: '#f5f5f5', padding: 12, borderRadius: 4, maxHeight: 200, overflow: 'auto', fontSize: 12, marginBottom: 16 }}>
                            {currentDetail.processedData ? JSON.stringify(JSON.parse(currentDetail.processedData), null, 2) : '-'}
                        </pre>
                        <p><strong>验证结果：</strong></p>
                        <pre style={{ background: '#f5f5f5', padding: 12, borderRadius: 4, maxHeight: 200, overflow: 'auto', fontSize: 12 }}>
                            {currentDetail.verifyResult ? JSON.stringify(JSON.parse(currentDetail.verifyResult), null, 2) : '-'}
                        </pre>
                    </div>
                )}
                <div style={{ textAlign: 'right', marginTop: 16 }}>
                    <Button onClick={() => setDetailModalVisible(false)}>关闭</Button>
                </div>
            </Modal>
        </>
    );
};

export default DataProcessing;
