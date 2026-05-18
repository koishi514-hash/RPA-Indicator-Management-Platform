import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, message, Space, Popconfirm, Select } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { listIndicators, createIndicator, updateIndicator, deleteIndicator, getIndicatorDetail } from '@/services/indicator';
import { getQueryList } from '@/services/query';

const IndicatorCalculation = () => {
    const [data, setData] = useState([]);
    const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
    const [keyword, setKeyword] = useState('');
    const [addModalVisible, setAddModalVisible] = useState(false);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editId, setEditId] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const [addForm] = Form.useForm();
    const [editForm] = Form.useForm();
    const [queryOptions, setQueryOptions] = useState<any[]>([]);
    const [loadingQueryOptions, setLoadingQueryOptions] = useState(false);

    const loadQueryOptions = async () => {
        setLoadingQueryOptions(true);
        try {
            const res = await getQueryList({ pageNum: 1, pageSize: 100 });
            const records = res.data?.records || [];
            const options = records.map((item: any) => ({
                value: item.queryId,
                label: `任务${item.taskId} - ${item.enterpriseName || '未知企业'} (${item.taxNo || '无税号'})`,
                taskId: item.taskId,
                queryId: item.queryId,
                enterpriseName: item.enterpriseName,
                taxNo: item.taxNo,
            }));
            setQueryOptions(options);
        } catch (error) {
            console.error('加载业务数据失败:', error);
        } finally {
            setLoadingQueryOptions(false);
        }
    };

    useEffect(() => {
        loadQueryOptions();
    }, []);

    const loadData = async (pageNum = pagination.current, pageSize = pagination.pageSize) => {
        setLoading(true);
        try {
            const res = await listIndicators({ keyword, pageNum, pageSize });
            if (res.code === 200) {
                const recordsWithLabels = (res.data.records || []).map((record: any) => {
                    const matchedOption = queryOptions.find(opt => opt.taskId === record.taskId);
                    return {
                        ...record,
                        taskDisplayLabel: matchedOption?.label || `任务${record.taskId}`,
                    };
                });
                setData(recordsWithLabels);
                setPagination({ current: res.data.pageNum || 1, pageSize: res.data.pageSize || 10, total: res.data.total || 0 });
            }
        } catch (error) {
            message.error('加载失败');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    useEffect(() => {
        if (queryOptions.length > 0) {
            loadData();
        }
    }, [queryOptions]);

    const handleAddSubmit = async () => {
        try {
            const values = await addForm.validateFields();
            const selectedQuery = queryOptions.find(opt => opt.value === values.queryId);
            const submitData = {
                indicatorName: values.indicatorName,
                indicatorCode: values.indicatorCode,
                indicatorLogic: values.indicatorLogic,
                taskId: selectedQuery?.taskId || values.taskId,
                queryId: values.queryId,
            };
            await createIndicator(submitData);
            message.success('创建成功');
            setAddModalVisible(false);
            addForm.resetFields();
            loadData();
        } catch (error) {
            message.error('创建失败');
        }
    };

    const handleEditSubmit = async () => {
        try {
            const values = await editForm.validateFields();
            const selectedQuery = queryOptions.find(opt => opt.value === values.queryId);
            const submitData = {
                id: editId,
                indicatorName: values.indicatorName,
                indicatorCode: values.indicatorCode,
                indicatorLogic: values.indicatorLogic,
                taskId: selectedQuery?.taskId || values.taskId,
                queryId: values.queryId,
            };
            await updateIndicator(submitData);
            message.success('更新成功');
            setEditModalVisible(false);
            editForm.resetFields();
            setEditId(null);
            loadData();
        } catch (error) {
            message.error('更新失败');
        }
    };

    const handleEdit = async (record: any) => {
        setEditId(record.id);
        setEditModalVisible(true);
        const res = await getIndicatorDetail(record.id);
        if (res.code === 200) {
            const detail = res.data;
            const matchedQuery = queryOptions.find(opt => opt.taskId === detail.taskId);
            editForm.setFieldsValue({
                ...detail,
                queryId: matchedQuery?.value,
            });
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await deleteIndicator(id);
            message.success('删除成功');
            loadData();
        } catch (error) {
            message.error('删除失败');
        }
    };

    const handleSearch = () => {
        setPagination({ ...pagination, current: 1 });
        loadData(1, pagination.pageSize);
    };

    const columns = [
        { title: '指标名称', dataIndex: 'indicatorName', key: 'indicatorName' },
        { title: '指标编码', dataIndex: 'indicatorCode', key: 'indicatorCode' },
        { title: '计算逻辑', dataIndex: 'indicatorLogic', key: 'indicatorLogic' },
        {
            title: '关联任务',
            dataIndex: 'taskDisplayLabel',
            key: 'taskDisplayLabel',
            render: (text: string, record: any) => text || record.taskId || '-'
        },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', render: (text: string) => text || '-' },
        {
            title: '操作',
            key: 'action',
            render: (_: any, record: { id: number; }) => (
                <Space>
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
            <div style={{ marginBottom: 16, display: 'flex', gap: 16 }}>
                <div style={{ display: 'flex', gap: 8 }}>
                    <Input
                        placeholder="搜索指标名称或编码"
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                        style={{ width: 300 }}
                        onPressEnter={handleSearch}
                    />
                    <Button onClick={handleSearch} icon={<SearchOutlined />}>搜索</Button>
                </div>
                <Button type="primary" onClick={() => {
                    loadQueryOptions();
                    setAddModalVisible(true);
                }} icon={<PlusOutlined />}>新增指标</Button>
            </div>

            <Table
                columns={columns}
                dataSource={data}
                rowKey="id"
                pagination={pagination}
                loading={loading}
                onChange={(p) => loadData(p.current, p.pageSize)}
            />

            <Modal
                title="新增指标"
                open={addModalVisible}
                onCancel={() => { setAddModalVisible(false); addForm.resetFields(); }}
                onOk={handleAddSubmit}
                width={700}
            >
                <Form form={addForm} layout="vertical">
                    <Form.Item name="indicatorName" label="指标名称" rules={[{ required: true, message: '请输入指标名称' }]}>
                        <Input placeholder="如：近12个月下游销售金额" />
                    </Form.Item>
                    <Form.Item name="indicatorCode" label="指标编码" rules={[{ required: true, message: '请输入指标编码' }]}>
                        <Input placeholder="如：INV_F1_12M_DOWN_SALE_JSHJ_SUM" />
                    </Form.Item>
                    <Form.Item name="indicatorLogic" label="指标逻辑描述" rules={[{ required: true, message: '请输入指标逻辑' }]}>
                        <Input.TextArea placeholder="如：统计近12个月下游客户销售发票的含税金额总和" rows={4} />
                    </Form.Item>
                    <Form.Item name="queryId" label="关联业务数据" rules={[{ required: true, message: '请选择业务数据' }]} extra="从数据查询中选择关联的业务数据">
                        <Select
                            showSearch
                            placeholder="请选择业务数据"
                            optionFilterProp="label"
                            options={queryOptions}
                            loading={loadingQueryOptions}
                            allowClear
                        />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title="编辑指标"
                open={editModalVisible}
                onCancel={() => { setEditModalVisible(false); editForm.resetFields(); setEditId(null); }}
                onOk={handleEditSubmit}
                width={700}
            >
                <Form form={editForm} layout="vertical">
                    <Form.Item name="indicatorName" label="指标名称" rules={[{ required: true, message: '请输入指标名称' }]}>
                        <Input placeholder="如：近12个月下游销售金额" />
                    </Form.Item>
                    <Form.Item name="indicatorCode" label="指标编码" rules={[{ required: true, message: '请输入指标编码' }]}>
                        <Input placeholder="如：INV_F1_12M_DOWN_SALE_JSHJ_SUM" />
                    </Form.Item>
                    <Form.Item name="indicatorLogic" label="指标逻辑描述" rules={[{ required: true, message: '请输入指标逻辑' }]}>
                        <Input.TextArea placeholder="如：统计近12个月下游客户销售发票的含税金额总和" rows={4} />
                    </Form.Item>
                    <Form.Item name="queryId" label="关联业务数据" extra="从数据查询中选择关联的业务数据">
                        <Select
                            showSearch
                            placeholder="请选择业务数据"
                            optionFilterProp="label"
                            options={queryOptions}
                            loading={loadingQueryOptions}
                            allowClear
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default IndicatorCalculation;
