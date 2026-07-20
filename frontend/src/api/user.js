import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - 登录信息 {username, password}
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {FormData} data - 注册信息表单数据
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 更新用户信息
 * @param {FormData} data - 用户信息表单数据
 * @returns {Promise}
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 修改密码
 * @param {Object} data - 密码信息 {oldPassword, newPassword}
 * @returns {Promise}
 */
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

// 获取用户列表 (管理员)
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}

// 删除用户 (管理员)
export function deleteUser(userId) {
  return request({
    url: `/admin/user/${userId}`,
    method: 'delete'
  })
}

// 更新用户 (管理员)
export function updateUser(userId, data) {
  return request({
    url: `/admin/user/${userId}`,
    method: 'put',
    data
  })
} 