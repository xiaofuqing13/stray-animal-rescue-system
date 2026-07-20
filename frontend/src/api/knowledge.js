import request from '@/utils/request'

/**
 * 获取所有知识分类
 * @returns {Promise}
 */
export function getAllKnowledgeCategories() {
  return request({
    url: '/user/knowledge/category/all',
    method: 'get'
  })
}

/**
 * 分页查询知识文章
 * @param {Number} pageNum 页码
 * @param {Number} pageSize 每页大小
 * @param {String} keyword 关键字
 * @param {Number} categoryId 分类ID
 * @returns {Promise}
 */
export function getKnowledgeArticlePage(pageNum, pageSize, keyword, categoryId) {
  return request({
    url: '/user/knowledge/article/page',
    method: 'get',
    params: {
      pageNum,
      pageSize,
      keyword,
      categoryId
    }
  })
}

/**
 * 获取文章详情
 * @param {Number} id 文章ID
 * @returns {Promise}
 */
export function getKnowledgeArticleDetail(id) {
  return request({
    url: `/user/knowledge/article/${id}`,
    method: 'get'
  })
}

/**
 * 获取相关文章
 * @param {Number} categoryId 分类ID
 * @param {Number} articleId 当前文章ID
 * @param {Number} limit 限制数量
 * @returns {Promise}
 */
export function getRelatedArticles(categoryId, articleId, limit = 4) {
  return request({
    url: '/user/knowledge/article/related',
    method: 'get',
    params: {
      categoryId,
      articleId,
      limit
    }
  })
} 