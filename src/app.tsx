import { RequestConfig, history } from "@umijs/max";
import JSONBig from 'json-bigint';
import { message, Avatar, Dropdown, MenuProps, Space } from 'antd';
import { LogoutOutlined, UserOutlined } from '@ant-design/icons';
import { getAuthInfo } from "./services/login";

async function fetchUserAndRoutes() {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      message.info('请先登录');
      history.replace('/login');
      return { userInfo: {}, permissionRoutes: [], isLogin: false };
    }

    const res = await getAuthInfo();
    const userInfo = res.data || {};
    const permissionRoutes = userInfo.permissions || [];
    return {
      userInfo,
      permissionRoutes,
      isLogin: true,
    };
  } catch (err) {
    return {
      userInfo: {},
      permissionRoutes: [],
      isLogin: false,
    };
  }
}

export async function getInitialState() {
  return await fetchUserAndRoutes();
}

export const layout = ({ initialState }) => {
  const { isLogin, userInfo, permissionRoutes } = initialState || {};
  const handleLogout = () => {
    localStorage.clear();
    message.success('退出登录成功');
    history.push('/login');
  };
  const handleProfile = () => {
    const has = (permissionRoutes || []).some((route) => {
      return typeof route === 'string' && route.includes('/system/profile');
    });

    if (has) {
      history.push('/system/profile');
    } else {
      message.warning('没有访问权限');
    }
  };

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'info',
      icon: <UserOutlined />,
      label: '个人信息',
      onClick: handleProfile,
    },
    { type: 'divider' },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ];
  return {
    layout: 'mix',
    splitMenus: true,
    rightContentRender: () => {
      if (!isLogin) return null;

      return (
        <Space size="middle" style={{ marginRight: 24 }}>
          <Dropdown
            menu={{ items: userMenuItems }}
            placement="bottomRight"
          >
            <Space style={{ cursor: 'pointer' }}>
              <Avatar size={28} src={userInfo?.avatar} />
              <span>{userInfo?.name}</span>
            </Space>
          </Dropdown>
        </Space>
      );
    },
  };
};

export const request: RequestConfig = {
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
  transformResponse: [
    (data: any, options: any) => {
      try {
        const parsed = JSONBig.parse(data);
        parsed.__jsonBigParsed = true;
        return parsed;
      } catch {
        return { data, options };
      }
    },
  ],
  requestInterceptors: [
    (url: string, options: any) => {
      const Token = localStorage.getItem('token');
      if (Token) {
        const headers = {
          ...options.headers,
          Authorization: `Bearer ${Token}`,
        };
        return {
          url,
          options: { ...options, headers },
        };
      }
      return {
        url, options: { ...options, interceptors: true },
      };
    },
  ],
  responseInterceptors: [
    (response: any) => {
      const { data } = response;
      if (data.code !== 200) {
        message.error(data.message);
        throw data.message;
      }
      return response;
    },
  ],
  errorConfig: {
    errorHandler: (error: any) => {
      message.error(error?.message || '请求失败，请稍后重试');
    },
  },
};
