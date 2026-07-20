package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.common.exception.BusinessException;
import com.stray.animal.rescue.dto.AdoptionApplicationDTO;
import com.stray.animal.rescue.dto.AdoptionAuditDTO;
import com.stray.animal.rescue.entity.AdoptionApplication;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.mapper.AdoptionApplicationMapper;
import com.stray.animal.rescue.mapper.AnimalMapper;
import com.stray.animal.rescue.mapper.UserMapper;
import com.stray.animal.rescue.service.AdoptionApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 领养申请服务实现类
 */
@Service
public class AdoptionApplicationServiceImpl 
    extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication> 
    implements AdoptionApplicationService {

    private final AnimalMapper animalMapper;
    private final UserMapper userMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AdoptionApplicationServiceImpl(AnimalMapper animalMapper, UserMapper userMapper) {
        this.animalMapper = animalMapper;
        this.userMapper = userMapper;
    }

    @Override
    public IPage<AdoptionApplicationDTO> pageApplications(Page<AdoptionApplicationDTO> page, Integer status) {
        return baseMapper.pageApplications(page, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditApplication(AdoptionAuditDTO auditDTO) {
        // 获取申请信息
        AdoptionApplication application = getById(auditDTO.getId());
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        
        // 检查申请状态
        if (application.getStatus() != 0) {
            throw new BusinessException("该申请已审核过，不能重复审核");
        }
        
        // 更新申请状态
        application.setStatus(auditDTO.getStatus());
        application.setRemark(auditDTO.getRemark());
        application.setUpdateTime(LocalDateTime.now());
        boolean updated = updateById(application);
        
        // 如果审核通过，更新动物状态为已领养
        if (updated && auditDTO.getStatus() == 1) {
            Animal animal = animalMapper.selectById(application.getAnimalId());
            if (animal != null) {
                animal.setStatus(1); // 设置为已领养
                animalMapper.updateById(animal);
                
                // 将同一动物的其他待审核申请自动设为拒绝
                LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AdoptionApplication::getAnimalId, animal.getId())
                       .eq(AdoptionApplication::getStatus, 0)
                       .ne(AdoptionApplication::getId, application.getId());
                
                AdoptionApplication updateApp = new AdoptionApplication();
                updateApp.setStatus(2); // 设置为拒绝
                updateApp.setRemark("该动物已被其他申请者领养");
                update(updateApp, wrapper);
            }
        }
        
        return updated;
    }

    @Override
    public AdoptionApplicationDTO getApplicationById(Long id) {
        // 查询申请信息
        AdoptionApplication application = getById(id);
        if (application == null) {
            return null;
        }
        
        // 使用Mapper查询带用户和动物信息的完整数据
        Page<AdoptionApplicationDTO> page = new Page<>(1, 1);
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getId, id);
        
        IPage<AdoptionApplicationDTO> result = baseMapper.pageApplications(page, null);
        
        // 检查查询结果是否为空
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            AdoptionApplicationDTO dto = result.getRecords().get(0);
            
            // 如果用户联系信息为空，则从User表中查询并补充
            if (dto.getUserPhone() == null || dto.getUserEmail() == null || dto.getUserAddress() == null) {
                User user = userMapper.selectById(application.getUserId());
                if (user != null) {
                    dto.setUserPhone(user.getPhone());
                    dto.setUserEmail(user.getEmail());
                    dto.setUserAddress(user.getAddress());
                }
            }
            
            return dto;
        } else {
            // 如果未查到完整信息，则返回基本信息
            AdoptionApplicationDTO dto = new AdoptionApplicationDTO();
            BeanUtils.copyProperties(application, dto);
            
            // 获取用户联系信息
            User user = userMapper.selectById(application.getUserId());
            if (user != null) {
                dto.setUserName(user.getName());
                dto.setUserPhone(user.getPhone());
                dto.setUserEmail(user.getEmail());
                dto.setUserAddress(user.getAddress());
            }
            
            if (application.getCreateTime() != null) {
                dto.setCreateTime(application.getCreateTime().format(formatter));
            }
            return dto;
        }
    }
    
    @Override
    public List<AdoptionApplicationDTO> getUserApplications(Long userId) {
        // 查询用户的所有申请
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getUserId, userId)
               .orderByDesc(AdoptionApplication::getCreateTime);
        
        List<AdoptionApplication> applications = list(wrapper);
        List<AdoptionApplicationDTO> dtoList = new ArrayList<>();
        
        applications.forEach(application -> {
            AdoptionApplicationDTO dto = new AdoptionApplicationDTO();
            BeanUtils.copyProperties(application, dto);
            
            // 格式化时间
            if (application.getCreateTime() != null) {
                dto.setCreateTime(application.getCreateTime().format(formatter));
            }
            
            dtoList.add(dto);
        });
        
        return dtoList;
    }
    
    @Override
    public boolean submitApplication(AdoptionApplication application) {
        application.setStatus(0); // 设置为待审核
        application.setCreateTime(LocalDateTime.now());
        return save(application);
    }

    @Override
    public List<Long> getAnimalIdsWithPendingApplications() {
        // 查询所有状态为待审核(0)的申请
        LambdaQueryWrapper<AdoptionApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplication::getStatus, 0)
               .select(AdoptionApplication::getAnimalId);
        
        // 获取动物ID列表
        List<AdoptionApplication> applications = list(wrapper);
        List<Long> animalIds = new ArrayList<>();
        
        applications.forEach(application -> {
            if (application.getAnimalId() != null && !animalIds.contains(application.getAnimalId())) {
                animalIds.add(application.getAnimalId());
            }
        });
        
        return animalIds;
    }

    @Override
    public List<Map<String, Object>> getAdoptionApplicationStats() {
        // 查询各状态领养申请数量
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 状态：0-待审核 1-通过 2-拒绝
        String[] statusNames = {"待审核", "已通过", "已拒绝"};
        for (int i = 0; i < statusNames.length; i++) {
            long count = count(new LambdaQueryWrapper<AdoptionApplication>().eq(AdoptionApplication::getStatus, i));
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("name", statusNames[i]);
            statusMap.put("value", count);
            result.add(statusMap);
        }
        
        return result;
    }
    
    @Override
    public Page<AdoptionApplicationDTO> getAdoptionPage(Integer pageNum, Integer pageSize, Integer status) {
        Page<AdoptionApplicationDTO> page = new Page<>(pageNum, pageSize);
        IPage<AdoptionApplicationDTO> result = baseMapper.pageApplications(page, status);
        return (Page<AdoptionApplicationDTO>) result;
    }
    
    @Override
    @Transactional
    public void auditAdoption(AdoptionAuditDTO auditDTO) {
        AdoptionApplication application = getById(auditDTO.getId());
        if (application == null) {
            throw new BusinessException("领养申请不存在");
        }
        
        application.setStatus(auditDTO.getStatus());
        application.setRemark(auditDTO.getRemark());
        application.setUpdateTime(LocalDateTime.now());
        
        updateById(application);
        
        // 如果审核通过，更新动物状态为已领养
        if (auditDTO.getStatus() == 1) {
            Animal animal = animalMapper.selectById(application.getAnimalId());
            if (animal != null) {
                animal.setStatus(1); // 1-已领养
                animal.setUpdateTime(LocalDateTime.now());
                animalMapper.updateById(animal);
            }
        }
    }
    
    @Override
    @Transactional
    public void addRemark(Long id, String remark) {
        AdoptionApplication application = getById(id);
        if (application == null) {
            throw new BusinessException("领养申请不存在");
        }
        
        // 如果已有备注，追加新备注
        String currentRemark = application.getRemark();
        String newRemark;
        if (currentRemark != null && !currentRemark.isEmpty()) {
            newRemark = currentRemark + "\n[" + LocalDateTime.now().format(formatter) + "] " + remark;
        } else {
            newRemark = "[" + LocalDateTime.now().format(formatter) + "] " + remark;
        }
        
        application.setRemark(newRemark);
        application.setUpdateTime(LocalDateTime.now());
        
        updateById(application);
    }
}
