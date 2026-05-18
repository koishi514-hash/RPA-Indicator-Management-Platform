export default function (initialState) {
  // 从全局数据中拿到：权限路由数组
  const { permissionRoutes = [] } = initialState || {};
  return {
    // 核心：判断用户是否有权限访问当前路由
    // route 是你在路由配置里写的每一条路由信息
    canAccess: (route) => {
      // 如果路由没有配置 path → 直接放行（比如布局页）
      if (!route.path) return true;
      // 判断：后端返回的权限数组里 是否包含 当前路由path
      return permissionRoutes.includes(route.path);
    },
  };
}