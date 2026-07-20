package com.stray.animal.rescue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 知识文章Mapper接口
 */
@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
    
    /**
     * 分页查询知识文章（包含分类信息）
     * @param page 分页参数
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @return 文章列表
     */
    @Select({
        "<script>",
        "SELECT a.*, c.name as category_name",
        "FROM knowledge_article a",
        "LEFT JOIN knowledge_category c ON a.category_id = c.id",
        "<where>",
        "<if test='keyword != null and keyword != \"\"'>",
        "AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.content LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "<if test='categoryId != null'>",
        "AND a.category_id = #{categoryId}",
        "</if>",
        "</where>",
        "ORDER BY a.create_time DESC",
        "</script>"
    })
    IPage<KnowledgeArticle> pageArticles(Page<KnowledgeArticle> page, @Param("keyword") String keyword, @Param("categoryId") Long categoryId);
} 