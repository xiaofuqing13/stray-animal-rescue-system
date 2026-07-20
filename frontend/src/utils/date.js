/**
 * 日期格式化
 * @param {Date|string} date 日期对象或日期字符串
 * @param {string} fmt 格式化模板，如 'yyyy-MM-dd HH:mm:ss'
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, fmt) {
  if (!date) return '';
  
  // 如果是时间戳（数字），则转换为日期对象
  if (typeof date === 'number') {
    date = new Date(date);
  }
  
  // 如果是日期字符串，则转换为日期对象
  if (typeof date === 'string') {
    // 处理ISO格式的日期字符串（如2021-01-01T00:00:00.000Z）
    if (date.indexOf('T') > -1) {
      date = new Date(date);
    } else {
      // 处理其他格式的日期字符串（如2021-01-01 00:00:00）
      // 兼容 yyyy-MM-dd 或 yyyy/MM/dd 格式
      date = date.replace(/-/g, '/');
      date = new Date(date);
    }
  }
  
  // 如果date不是一个有效的日期对象，则返回空字符串
  if (isNaN(date.getTime())) {
    return '';
  }
  
  if (!fmt) fmt = 'yyyy-MM-dd';
  
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 小时（12小时制）
    'H+': date.getHours(), // 小时（24小时制）
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    S: date.getMilliseconds(), // 毫秒
  };
  
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
      );
    }
  }
  
  return fmt;
}

/**
 * 获取相对时间，如"1小时前"、"昨天"等
 * @param {Date|string} date 日期对象或日期字符串
 * @returns {string} 相对时间
 */
export function relativeTime(date) {
  if (!date) return '';
  
  // 如果是时间戳（数字），则转换为日期对象
  if (typeof date === 'number') {
    date = new Date(date);
  }
  
  // 如果是日期字符串，则转换为日期对象
  if (typeof date === 'string') {
    date = date.replace(/-/g, '/');
    date = new Date(date);
  }
  
  // 如果date不是一个有效的日期对象，则返回空字符串
  if (isNaN(date.getTime())) {
    return '';
  }
  
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const diffMinutes = Math.floor(diff / (1000 * 60));
  const diffHours = Math.floor(diff / (1000 * 60 * 60));
  const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (diffMinutes < 1) {
    return '刚刚';
  } else if (diffMinutes < 60) {
    return diffMinutes + '分钟前';
  } else if (diffHours < 24) {
    return diffHours + '小时前';
  } else if (diffDays < 2) {
    return '昨天';
  } else if (diffDays < 7) {
    return diffDays + '天前';
  } else {
    return formatDate(date, 'yyyy-MM-dd');
  }
} 