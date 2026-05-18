import React, { useEffect, useState } from 'react';
import { Button, Card, DatePicker, Form, Input, Modal, Select, Space, Table, Tag, message } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from '@umijs/max';
import { getParsingList, getParsingDetail, deleteParsing } from '@/services/parsing';

const { Option } = Select;
const { RangePicker } = DatePicker;

const DataAnalysis = () => {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [data, setData] = useState<any[]>([]);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [currentDetail, setCurrentDetail] = useState<any | null>(null);
    const [loading, setLoading] = useState(false);

    const [current, setCurrent] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [total, setTotal] = useState(0);

    const [statistics, setStatistics] = useState({ totalParsing: 0, success: 0, parsing: 0, failed: 0 });

    const statusConfig: Record<string, { text: string; color: string }> = {
        'parsed': { text: '待解析', color: 'default' },
        'processed': { text: '已解析', color: 'green' },
        'failed': { text: '解析失败', color: 'red' },
    };

    const loadData = async (pageNum = 1, pageSizeVal = 10) => {
        setLoading(true);
        try {
            const values = form.getFieldsValue();
            const params: any = { pageNum, pageSize: pageSizeVal };

            if (values.taskId) params.taskId = values.taskId;
            if (values.status !== undefined && values.status !== '') params.status = values.status;

            if (values.parsingTime?.length === 2) {
                const start = values.parsingTime[0];
                const end = values.parsingTime[1];
                if (start && end && typeof start.format === 'function' && typeof end.format === 'function') {
                    params.parsingTimeStart = start.format('YYYY-MM-DDTHH:mm:ss');
                    params.parsingTimeEnd = end.format('YYYY-MM-DDTHH:mm:ss');
                }
            }

            const res = await getParsingList(params);
            const processedRecords = (res.data.records || []).map((record: any) => ({
                ...record,
                collectionId: record.collectionId?.toString?.() ?? record.collectionId,
            }));
            setData(processedRecords);
            setCurrent(pageNum);
            setPageSize(pageSizeVal);
            setTotal(res.data.total || 0);

            if (pageNum === 1 && !params.taskId && params.status === undefined && !params.parsingTimeStart) {
                setStatistics(res.data.statistics || { totalParsing: 0, success: 0, parsing: 0, failed: 0 });
            }
        } catch {
            message.error('加载数据失败');
        } finally {
            setLoading(false);
        }
    };

    const handleViewDetail = async (record: any) => {
        try {
            const res = await getParsingDetail(record.parsingId);
            const processedDetail = {
                ...res.data,
                collectionId: res.data.collectionId?.toString?.() ?? res.data.collectionId,
            };
            setCurrentDetail(processedDetail);
            setDetailModalVisible(true);
        } catch {
            message.error('获取详情失败');
        }
    };

    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除解析记录吗？',
            onOk: async () => {
                try {
                    await deleteParsing(record.parsingId);
                    message.success('删除成功');
                    loadData(current, pageSize);
                } catch {
                    message.error('删除失败');
                }
            },
        });
    };

    useEffect(() => {
        loadData();
    }, []);

    const columns: ColumnsType<any> = [
        {
            title: '序号',
            key: 'index',
            width: 80,
            render: (_, __, i) => (current - 1) * pageSize + i + 1
        },
        {
            title: '任务ID',
            dataIndex: 'taskId',
            key: 'taskId',
            width: 100,
        },
        {
            title: '任务编码',
            dataIndex: 'taskCode',
            key: 'taskCode',
            width: 200,
            render: (v) => <a style={{ color: '#1890ff' }} onClick={() => navigate(`/Rpa/Tasks/detail/${v}`)}>{v}</a>
        },
        {
            title: '采集ID',
            dataIndex: 'collectionId',
            key: 'collectionId',
            width: 200,
            render: (v) => <a style={{ color: '#1890ff' }} onClick={() => navigate(`/Rpa/Data/collection`)}>{v}</a>
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            width: 100,
            render: (s) => <Tag color={statusConfig[s]?.color}>{statusConfig[s]?.text}</Tag>
        },
        {
            title: '提取字段数',
            dataIndex: 'extractedFields',
            key: 'extractedFields',
            width: 120
        },
        {
            title: '解析时间',
            dataIndex: 'parsingTime',
            key: 'parsingTime',
            width: 180,
            render: (v) => v?.replace('T', ' ')
        },
        {
            title: '操作',
            key: 'action',
            width: 150,
            fixed: 'right',
            render: (_, r) => (
                <Space size={16}>
                    <a onClick={() => handleViewDetail(r)}>查看</a>
                    <a style={{ color: '#f56c6c' }} onClick={() => handleDelete(r)}>删除</a>
                </Space>
            ),
        },
    ];

    return (
        <>
            <h1 style={{ margin: 0, marginBottom: 24, fontSize: 28, fontWeight: 600 }}>数据解析</h1>

            <div style={{ marginBottom: 20, display: 'flex', gap: 16 }}>
                <Card style={{ flex: 1, textAlign: 'center' }}>
                    <div style={{ fontSize: 32, fontWeight: 600 }}>{statistics.totalParsing}</div>
                    <div style={{ color: '#666' }}>总解析数</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#f6ffed' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#52c41a' }}>{statistics.success}</div>
                    <div style={{ color: '#666' }}>成功</div>
                </Card>
                <Card style={{ flex: 1, textAlign: 'center', background: '#fffbe6' }}>
                    <div style={{ fontSize: 32, fontWeight: 600, color: '#faad14' }}>{statistics.parsing}</div>
                    <div style={{ color: '#666' }}>解析中</div>
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
                            <Option value={'parsing'}>待解析</Option>
                            <Option value={'processed'}>已解析</Option>
                            <Option value={'failed'}>解析失败</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="parsingTime" label="解析时间">
                        <RangePicker style={{ width: 360 }} showTime format="YYYY-MM-DD HH:mm:ss" />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" icon={<SearchOutlined />} onClick={() => loadData(1, pageSize)}>查询</Button>
                        <Button icon={<ReloadOutlined />} onClick={() => {
                            form.resetFields();
                            loadData(1, pageSize);
                        }} style={{ marginLeft: 8 }}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>

            <Table
                columns={columns}
                dataSource={data}
                rowKey="parsingId"
                pagination={{
                    current: current,
                    pageSize: pageSize,
                    total: total,
                    showSizeChanger: true,
                    showQuickJumper: true,
                    showTotal: (total) => `共 ${total} 条`,
                    onChange: (page, size) => loadData(page, size),
                }}
                loading={loading}
                scroll={{ x: 1400 }}
            />

            <Modal title="解析数据详情" open={detailModalVisible} onCancel={() => setDetailModalVisible(false)} footer={null} width={800}>
                {currentDetail && (
                    <div style={{ padding: '16px 0' }}>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px 24px', marginBottom: 16 }}>
                            <div><strong>任务ID：</strong><a style={{ color: '#1890ff' }} onClick={() => { setDetailModalVisible(false); navigate(`/Rpa/Tasks/detail/${currentDetail.taskId}`); }}>{currentDetail.taskId}</a></div>
                            <div><strong>采集ID：</strong>{String(currentDetail.collectionId)}</div>
                            <div><strong>纳税人识别号：</strong>{currentDetail.taxNo || '-'}</div>
                            <div><strong>企业名称：</strong>{currentDetail.enterpriseName || '-'}</div>
                            <div><strong>状态：</strong><Tag color={statusConfig[currentDetail.status]?.color}>{statusConfig[currentDetail.status]?.text}</Tag></div>
                            <div><strong>解析时间：</strong>{currentDetail.parsingTime?.replace('T', ' ')}</div>
                            {currentDetail.errorMsg && <div style={{ gridColumn: '1 / -1' }}><strong>错误信息：</strong><span style={{ color: '#f56c6c' }}>{currentDetail.errorMsg}</span></div>}
                        </div>
                        <p><strong>解析数据：</strong></p>
                        <pre style={{ background: '#f5f5f5', padding: 12, borderRadius: 4, maxHeight: 400, overflow: 'auto', fontSize: 12 }}>
                            {currentDetail.parsedData ? (() => {
                                try {
                                    const parsed = JSON.parse(currentDetail.parsedData);
                                    return typeof parsed === 'object' ? JSON.stringify(parsed, null, 2) : currentDetail.parsedData;
                                } catch {
                                    return currentDetail.parsedData;
                                }
                            })() : '-'}
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

export default DataAnalysis;
