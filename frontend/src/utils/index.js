/**
 * 静态资源访问前缀
 * 浏览器请求 /uploads/xxx 由前端 dev server 或生产 nginx 代理到后端 /api/uploads/xxx
 */
const UPLOAD_PREFIX = '/uploads'

/**
 * 默认头像路径
 */
export const DEFAULT_AVATAR = `${UPLOAD_PREFIX}/avatar/default.png`

/**
 * 列表卡片占位图（内嵌 SVG，零外部依赖，不会 404）
 * 用于文章封面、动物图片等场景的 imageUrl 为空时兜底，避免破图。
 * 历史代码里 /images/default-article.jpg 和 /images/default-animal.jpg 实际不存在，
 * 老数据 imageUrl 为空时会请求 404，看起来"不显示"。改用 data URI 后该问题消失。
 */
export const DEFAULT_IMAGE_PLACEHOLDER =
  'data:image/svg+xml;charset=utf-8,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="400" height="260" viewBox="0 0 400 260">' +
      '<rect width="400" height="260" fill="#f5f7fa"/>' +
      '<g fill="#c0c4cc" transform="translate(160,90)">' +
      '<rect x="0" y="0" width="80" height="60" rx="6" fill="none" stroke="#c0c4cc" stroke-width="3"/>' +
      '<circle cx="22" cy="22" r="7"/>' +
      '<path d="M8 50 L30 30 L48 44 L72 22 L72 50 Z"/>' +
      '</g>' +
      '<text x="200" y="190" text-anchor="middle" fill="#909399" font-family="Microsoft YaHei,Arial" font-size="14">暂无图片</text>' +
      '</svg>'
  )

/**
 * 标准化图片 URL，确保前端可正确显示
 * @param {string} url - 原始 URL，可能是完整 URL、相对路径、含或不含 /uploads 前缀
 * @returns {string} - 规范化的图片 URL（统一前缀 /uploads/...）
 */
export function getImageUrl(url) {
  if (!url) return ''

  // 完整 URL（http/https）直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  // 确保以 / 开头
  if (!url.startsWith('/')) {
    url = '/' + url
  }

  // 如果已含 /uploads 前缀，剥掉再走统一处理（兼容 /uploads//x 这种历史脏数据）
  let tail = url
  if (tail.startsWith(`${UPLOAD_PREFIX}/`) || tail === UPLOAD_PREFIX) {
    tail = tail.substring(UPLOAD_PREFIX.length)
  }

  // 去掉所有前导斜杠后再补一个，确保只有单斜杠
  while (tail.startsWith('/')) {
    tail = tail.substring(1)
  }

  return `${UPLOAD_PREFIX}/${tail}`
}

/**
 * 获取头像 URL
 * @param {string} avatar - 头像相对路径或完整 URL
 * @param {boolean} fallbackToDefault - 为空时是否返回默认头像（默认 true）
 * @returns {string|null} - 头像 URL 或 null（fallbackToDefault=false 且 avatar 为空时）
 */
export function getAvatarUrl(avatar, fallbackToDefault = true) {
  if (!avatar) {
    return fallbackToDefault ? DEFAULT_AVATAR : null
  }
  return getImageUrl(avatar)
}