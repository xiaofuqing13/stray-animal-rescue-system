/**
 * 用户认证Token存储的键名
 */
const TOKEN_KEY = 'token';
const USER_INFO_KEY = 'userInfo';
const USER_ROLE_KEY = 'userRole';

/**
 * 获取Token
 * @returns {string} token值
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

/**
 * 设置Token
 * @param {string} token token值
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

/**
 * 移除Token
 */
export function removeToken() {
  return localStorage.removeItem(TOKEN_KEY);
}

/**
 * 获取用户信息
 * @returns {Object} 用户信息对象
 */
export function getUserInfo() {
  const userInfoStr = localStorage.getItem(USER_INFO_KEY);
  return userInfoStr ? JSON.parse(userInfoStr) : {};
}

/**
 * 设置用户信息
 * @param {Object} userInfo 用户信息对象
 */
export function setUserInfo(userInfo) {
  return localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
  return localStorage.removeItem(USER_INFO_KEY);
}

/**
 * 获取用户角色
 * @returns {number} 用户角色ID
 */
export function getUserRole() {
  return parseInt(localStorage.getItem(USER_ROLE_KEY) || '0');
}

/**
 * 设置用户角色
 * @param {number} roleId 角色ID
 */
export function setUserRole(roleId) {
  return localStorage.setItem(USER_ROLE_KEY, roleId.toString());
}

/**
 * 移除用户角色
 */
export function removeUserRole() {
  return localStorage.removeItem(USER_ROLE_KEY);
}

/**
 * 清除所有用户相关存储
 */
export function clearUserStorage() {
  removeToken();
  removeUserInfo();
  removeUserRole();
}

/**
 * 判断用户是否已登录
 * @returns {boolean} 是否已登录
 */
export function isLoggedIn() {
  return !!getToken();
}

/**
 * 判断用户是否是管理员
 * @returns {boolean} 是否是管理员
 */
export function isAdmin() {
  return getUserRole() === 1;
}

/**
 * 判断用户是否是普通用户
 * @returns {boolean} 是否是普通用户
 */
export function isUser() {
  return getUserRole() === 0;
} 