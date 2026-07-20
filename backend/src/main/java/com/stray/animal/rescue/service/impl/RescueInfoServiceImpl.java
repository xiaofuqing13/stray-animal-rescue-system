package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.RescueInfoDTO;
import com.stray.animal.rescue.entity.RescueInfo;
import com.stray.animal.rescue.mapper.RescueInfoMapper;
import com.stray.animal.rescue.service.RescueInfoService;
import com.stray.animal.rescue.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 救助信息服务实现类
 */
@Service
@Slf4j
public class RescueInfoServiceImpl extends ServiceImpl<RescueInfoMapper, RescueInfo> implements RescueInfoService {

    @Resource
    private FileUploadUtil fileUploadUtil;

    @Override
    public Page<RescueInfo> getRescueInfoPage(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<RescueInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(RescueInfo::getTitle, keyword)
                        .or()
                        .like(RescueInfo::getContent, keyword)
                        .or()
                        .like(RescueInfo::getLocation, keyword)
                        .or()
                        .like(RescueInfo::getContactName, keyword);
        }
        
        if (status != null) {
            queryWrapper.eq(RescueInfo::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(RescueInfo::getCreateTime);
        
        // 分页查询
        Page<RescueInfo> page = new Page<>(pageNum, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRescueInfo(RescueInfoDTO rescueInfoDTO) {
        // 保存救助信息
        RescueInfo rescueInfo = new RescueInfo();
        BeanUtils.copyProperties(rescueInfoDTO, rescueInfo);
        this.save(rescueInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRescueInfo(RescueInfoDTO rescueInfoDTO) {
        // 验证救助信息是否存在
        RescueInfo rescueInfo = this.getById(rescueInfoDTO.getId());
        if (rescueInfo == null) {
            throw new RuntimeException("救助信息不存在");
        }
        
        // 更新救助信息
        BeanUtils.copyProperties(rescueInfoDTO, rescueInfo);
        this.updateById(rescueInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 验证救助信息是否存在
        RescueInfo rescueInfo = this.getById(id);
        if (rescueInfo == null) {
            throw new RuntimeException("救助信息不存在");
        }
        
        // 更新救助信息状态
        rescueInfo.setStatus(status);
        this.updateById(rescueInfo);
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        // 上传图片
        String imageUrl = fileUploadUtil.uploadFile(file, "rescue");
        
        // 返回图片URL
        return fileUploadUtil.getFileUrl(imageUrl);
    }

    @Override
    public List<Map<String, Object>> getRescueInfoStats() {
        // 查询各状态救助信息数量
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 状态：0-待处理 1-已派出 2-处理中 3-已完成
        String[] statusNames = {"待处理", "已派出", "处理中", "已完成"};
        for (int i = 0; i < statusNames.length; i++) {
            long count = count(new LambdaQueryWrapper<RescueInfo>().eq(RescueInfo::getStatus, i));
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("name", statusNames[i]);
            statusMap.put("value", count);
            result.add(statusMap);
        }
        
        return result;
    }
} 