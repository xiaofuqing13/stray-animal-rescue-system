package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.AnnouncementDTO;
import com.stray.animal.rescue.entity.Announcement;

/**
 * 通知公告服务接口
 * 提供公告相关的业务操作
 */
public interface AnnouncementService extends IService<Announcement> {
    
    /**
     * 分页查询公告列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Announcement> getAnnouncementPage(Integer pageNum, Integer pageSize);
    
    /**
     * 新增公告
     * @param announcementDTO 公告信息
     */
    void addAnnouncement(AnnouncementDTO announcementDTO);
    
    /**
     * 更新公告
     * @param announcementDTO 公告信息
     */
    void updateAnnouncement(AnnouncementDTO announcementDTO);
    
    /**
     * 删除公告
     * @param id 公告ID
     */
    void deleteAnnouncement(Long id);
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    Announcement getAnnouncementById(Long id);
} 