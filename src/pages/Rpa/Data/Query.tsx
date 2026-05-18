import React, { useEffect, useState } from 'react';
import { Button, Card, DatePicker, Form, Input, InputNumber, Modal, Select, Space, Table, Tag, message } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useNavigate } from '@umijs/max';
import { getQueryList, getQueryDetail, deleteQuery } from '@/services/query';

const { Option } = Select;
const { RangePicker } = DatePicker;

const DataQuery: React.FC = () => {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [data, setData] = useState<any[]>([]);
    const [detailModalVisible, setDetailModalVisible] = useState(false);
    const [currentDetail, setCurrentDetail] = useState<any | null>(null);
    const [loading, setLoading] = useState(false);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

    const statusConfig: Record<number, { text: string; color: string }> = {
        0: { text: '不可用', color: 'default' },
        1: { text: '可用', color: 'green' },
    };

    const loadData = async (pageNum = 1, pageSize = 10) => {
        setLoading(true);
        try {
            const values = form.getFieldsValue();
            const params: any = { pageNum, pageSize };
            if (values.keyword) params.keyword = values.keyword;
            if (values.taskId) params.taskId = values.taskId;
            if (values.taxAreaId) params.taxAreaId = values.taxAreaId;
            if (values.status !== undefined && values.status !== '') params.status = values.status;
            if (values.createTime?.length === 2) {
                params.createTimeStart = values.createTime[0].format('YYYY-MM-DDTHH:mm:ss');
                params.createTimeEnd = values.createTime[1].format('YYYY-MM-DDTHH:mm:ss');
            }
            const res = await getQueryList(params);
            setData(res.data.records || []);
            setPagination({ current: pageNum, pageSize, total: res.data.total || 0 });
        } catch {
            message.error('加载数据失败');
        } finally {
            setLoading(false);
        }
    };

    const handleViewDetail = async (record: any) => {
        try {
            const res = await getQueryDetail(record.queryId);
            setCurrentDetail(res.data);
        } catch {
            message.error('获取详情失败');
        }
        setDetailModalVisible(true);
    };

    const handleDelete = (record: any) => {
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除查询记录吗？',
            onOk: async () => {
                try {
                    await deleteQuery(record.queryId);
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
        { title: '任务编码', dataIndex: 'taskCode', key: 'taskCode', width: 200, render: (v) => <a style={{ color: '#1890ff' }} onClick={() => navigate(`/Rpa/Tasks/detail/${v}`)}>{v}</a> },
        { title: '纳税人识别号', dataIndex: 'taxNo', key: 'taxNo', width: 200 },
        { title: '企业名称', dataIndex: 'enterpriseName', key: 'enterpriseName', width: 200 },
        { title: '税区ID', dataIndex: 'taxAreaId', key: 'taxAreaId', width: 120, render: (v) => v || '-' },
        { title: '数据状态', dataIndex: 'status', key: 'status', width: 100, render: (s) => <Tag color={statusConfig[s]?.color}>{statusConfig[s]?.text}</Tag> },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180, render: (v) => v?.replace('T', ' ') },
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
            <h1 style={{ margin: 0, marginBottom: 24, fontSize: 28, fontWeight: 600 }}>数据查询</h1>

            <Card style={{ marginBottom: 16 }}>
                <Form form={form} layout="inline">
                    <Form.Item name="keyword" label="关键字">
                        <Input placeholder="纳税人识别号/企业名称" style={{ width: 220 }} />
                    </Form.Item>
                    <Form.Item name="taskId" label="任务ID">
                        <InputNumber placeholder="请输入" style={{ width: 180 }} />
                    </Form.Item>
                    <Form.Item name="taxAreaId" label="税区ID">
                        <Input placeholder="请输入" style={{ width: 150 }} />
                    </Form.Item>
                    <Form.Item name="status" label="数据状态">
                        <Select placeholder="请选择" style={{ width: 150 }} allowClear>
                            <Option value={0}>不可用</Option>
                            <Option value={1}>可用</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item name="createTime" label="创建时间">
                        <RangePicker style={{ width: 360 }} showTime format="YYYY-MM-DD HH:mm:ss" />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" icon={<SearchOutlined />} onClick={() => loadData(1, pagination.pageSize)}>查询</Button>
                        <Button icon={<ReloadOutlined />} onClick={() => { form.resetFields(); loadData(1, pagination.pageSize); }} style={{ marginLeft: 8 }}>重置</Button>
                    </Form.Item>
                </Form>
            </Card>

            <Table columns={columns} dataSource={data} rowKey="queryId" pagination={pagination} onChange={(p) => loadData(p.current, p.pageSize)} loading={loading} scroll={{ x: 1500 }} />

            <Modal title="查询数据详情" open={detailModalVisible} onCancel={() => setDetailModalVisible(false)} footer={null} width={700}>
                {currentDetail && (
                    <div style={{ padding: '16px 0' }}>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px 24px', marginBottom: 16 }}>
                            <div><strong>任务ID：</strong><a style={{ color: '#1890ff' }} onClick={() => { setDetailModalVisible(false); navigate(`/Rpa/Tasks/detail/${currentDetail.taskId}`); }}>{currentDetail.taskId}</a></div>
                            <div><strong>税区ID：</strong>{currentDetail.taxAreaId || '-'}</div>
                            <div><strong>纳税人识别号：</strong>{currentDetail.taxNo || '-'}</div>
                            <div><strong>企业名称：</strong>{currentDetail.enterpriseName || '-'}</div>
                            <div><strong>数据状态：</strong><Tag color={statusConfig[currentDetail.status]?.color}>{statusConfig[currentDetail.status]?.text}</Tag></div>
                            <div><strong>创建时间：</strong>{currentDetail.createTime?.replace('T', ' ')}</div>
                        </div>
                        <p><strong>业务数据：</strong></p>
                        <pre style={{ background: '#f5f5f5', padding: 12, borderRadius: 4, maxHeight: 300, overflow: 'auto', fontSize: 12 }}>
                            {currentDetail.businessData ? JSON.stringify(JSON.parse(currentDetail.businessData), null, 2) : '-'}
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

export default DataQuery;
