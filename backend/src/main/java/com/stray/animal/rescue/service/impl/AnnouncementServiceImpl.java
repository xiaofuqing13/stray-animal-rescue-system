package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.AnnouncementDTO;
import com.stray.animal.rescue.entity.Announcement;
import com.stray.animal.rescue.mapper.AnnouncementMapper;
import com.stray.animal.rescue.service.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知公告服务实现类
 * 实现公告相关的业务操作
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {
    
    @Override
    public Page<Announcement> getAnnouncementPage(Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        // 创建查询条件
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        // 按创建时间降序排序
        wrapper.orderByDesc(Announcement::getCreateTime);
        // 执行分页查询
        return page(page, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAnnouncement(AnnouncementDTO announcementDTO) {
        // 创建公告实体
        Announcement announcement = new Announcement();
        // 复制属性
        BeanUtils.copyProperties(announcementDTO, announcement);
        // 保存公告
        save(announcement);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(AnnouncementDTO announcementDTO) {
        // 创建公告实体
        Announcement announcement = new Announcement();
        // 复制属性
        BeanUtils.copyProperties(announcementDTO, announcement);
        // 更新公告
        updateById(announcement);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long id) {
        // 根据ID删除公告
        removeById(id);
    }
    
    @Override
    public Announcement getAnnouncementById(Long id) {
        // 根据ID查询公告
        return getById(id);
    }
} 