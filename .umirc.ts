import access from '@/access';
import { defineConfig } from '@umijs/max';

export default defineConfig({
  antd: {},
  access: {},
  model: {},
  initialState: {},
  request: {},
  layout: {
    title: '重庆工程学院',
    layout: 'mix',
  },
  mock: false,
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      name: '首页',
      path: '/home',
      component: './Home',
    },
    {
      name: 'RPA运营管理',
      path: '/rpa',
      routes: [
        {
          name: '任务管理',
          path: '/rpa/tasks',
          routes: [
            { path: '/rpa/tasks/list', name: '任务列表', component: './Rpa/Tasks/List', access: 'canAccess' },
            { path: '/rpa/tasks/records', name: '任务记录', component: './Rpa/Tasks/Records', access: 'canAccess' },
            {
              path: '/rpa/tasks/detail/:taskCode', // 子路由（详情页）
              name: '任务详情',
              component: './Rpa/Tasks/TaskDetail',
              hideInMenu: true,
            },
          ]
        },
        {
          name: '机器人管理',
          path: '/rpa/robots',
          routes: [
            { path: '/rpa/robots/list', name: '机器人列表', component: './Rpa/Robots/List', access: 'canAccess' },
          ]
        },
        {
          path: '/rpa/processes',
          name: '流程管理',
          routes: [
            { path: '/rpa/processes/list', name: '流程列表', component: './Rpa/Processes/List', access: 'canAccess' },
          ]
        },
        {
          path: '/rpa/data',
          name: '数据管理',
          routes: [
            { path: '/rpa/data/collection', name: '数据采集', component: './Rpa/Data/Collection', access: 'canAccess' },
            { path: '/rpa/data/analysis', name: '数据解析', component: './Rpa/Data/Analysis', access: 'canAccess' },
            { path: '/rpa/data/processing', name: '数据处理', component: './Rpa/Data/Processing', access: 'canAccess' },
            { path: '/rpa/data/query', name: '数据查询', component: './Rpa/Data/Query', access: 'canAccess' },
          ]
        },
      ],
    },
    {
      name: '系统管理',
      path: '/system',
      access: 'canAccess',
      routes: [
        { path: '/system', name: '系统管理', redirect: '/system/profile' },
        { path: '/system/profile', name: '个人信息', component: './System/Profile', access: 'canAccess' },
        { path: '/system/user', name: '用户管理', component: './System/User', access: 'canAccess' },
        { path: '/system/role', name: '角色管理', component: './System/Role', access: 'canAccess' },
        { path: '/system/resource', name: '资源管理', component: './System/Resource', access: 'canAccess' },
      ],
    },
    {
      name: '指标管理',
      path: '/indicator',
      access: 'canAccess',
      routes: [
        { path: '/indicator', name: '指标管理', redirect: '/indicator/calculation' },
        { path: '/indicator/calculation', name: '指标计算', component: './indicator/Calculation', access: 'canAccess' },
        { path: '/indicator/audit', name: '指标审核', component: './indicator/Audit', access: 'canAccess' },
      ],
    },
    {
      name: '登录',
      path: '/login',
      component: './login',
      layout: false,
    },
  ],
  proxy: {
    '/api': {
      target: 'http://10.159.42.150:8080',
      changeOrigin: true,
    },
  },
  npmClient: 'pnpm',
});