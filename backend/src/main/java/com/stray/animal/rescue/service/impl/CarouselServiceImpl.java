package com.stray.animal.rescue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stray.animal.rescue.dto.CarouselDTO;
import com.stray.animal.rescue.entity.Carousel;
import com.stray.animal.rescue.exception.BusinessException;
import com.stray.animal.rescue.mapper.CarouselMapper;
import com.stray.animal.rescue.service.CarouselService;
import com.stray.animal.rescue.util.FileUploadUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * 轮播图Service实现类
 */
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Resource
    private FileUploadUtil fileUploadUtil;

    

    /**
     * 获取所有启用的轮播图（按排序）
     */
    @Override
    public List<Carousel> getActiveCarousels() {
        LambdaQueryWrapper<Carousel> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询启用状态的轮播图
        queryWrapper.eq(Carousel::getStatus, 1);
        
        // 按排序字段排序
        queryWrapper.orderByAsc(Carousel::getSort);
        
        List<Carousel> carousels = this.list(queryWrapper);
        
        // 处理图片URL
        carousels.forEach(this::processImageUrl);
        
        return carousels;
    }

    /**
     * 获取轮播图列表（分页）
     */
    @Override
    public Page<Carousel> getCarouselList(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<Carousel> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果有关键字，则进行模糊查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Carousel::getTitle, keyword);
        }
        
        // 按排序字段升序排序
        queryWrapper.orderByAsc(Carousel::getSort);
        
        Page<Carousel> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 处理图片URL
        page.getRecords().forEach(this::processImageUrl);
        
        return page;
    }

    /**
     * 添加轮播图
     */
    @Override
    public Long addCarousel(CarouselDTO carouselDTO, MultipartFile image) throws IOException {
        // 创建轮播图实体
        Carousel carousel = new Carousel();
        BeanUtils.copyProperties(carouselDTO, carousel);
        
        // 如果有新图片上传，则处理图片
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileUploadUtil.uploadFile(image, "carousel");
            carousel.setImageUrl(imageUrl);
        } else if (StringUtils.hasText(carouselDTO.getImageUrl())) {
            // 如果没有上传新图片，但DTO中有URL，则使用DTO中的URL
            String imageUrl = carouselDTO.getImageUrl();
            
            // 如果URL以/uploads开头，则移除该前缀
            if (imageUrl.startsWith("/uploads")) {
                imageUrl = imageUrl.substring("/uploads".length());
            }
            
            carousel.setImageUrl(imageUrl);
        } else {
            throw new BusinessException("图片不能为空");
        }
        
        // 保存轮播图
        this.save(carousel);
        
        return carousel.getId();
    }

    /**
     * 更新轮播图
     */
    @Override
    public void updateCarousel(CarouselDTO carouselDTO, MultipartFile image) throws IOException {
        // 查询轮播图是否存在
        Carousel carousel = this.getById(carouselDTO.getId());
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }
        
        // 如果上传了新图片，则更新图片
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileUploadUtil.uploadFile(image, "carousel");
            carousel.setImageUrl(imageUrl);
        } else if (StringUtils.hasText(carouselDTO.getImageUrl()) && 
                  !carouselDTO.getImageUrl().equals(carousel.getImageUrl()) &&
                  !carouselDTO.getImageUrl().equals(fileUploadUtil.getFileUrl(carousel.getImageUrl()))) {
            // 如果没有上传新图片，但DTO中的URL与数据库不同，处理URL
            String imageUrl = carouselDTO.getImageUrl();
            
            // 如果URL以/uploads开头，则移除该前缀
            if (imageUrl.startsWith("/uploads")) {
                imageUrl = imageUrl.substring("/uploads".length());
            }
            
            carousel.setImageUrl(imageUrl);
        }
        
        // 更新其他信息
        carousel.setTitle(carouselDTO.getTitle());
        carousel.setSort(carouselDTO.getSort());
        carousel.setStatus(carouselDTO.getStatus());
        
        // 保存更新
        this.updateById(carousel);
    }

    /**
     * 删除轮播图
     */
    @Override
    public void deleteCarousel(Long id) {
        // 查询轮播图是否存在
        Carousel carousel = this.getById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }
        
        // 删除轮播图
        this.removeById(id);
    }

    /**
     * 更新轮播图状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        // 查询轮播图是否存在
        Carousel carousel = this.getById(id);
        if (carousel == null) {
            throw new BusinessException("轮播图不存在");
        }
        
        // 更新状态
        carousel.setStatus(status);
        this.updateById(carousel);
    }
    
    /**
     * 处理图片URL
     */
    private void processImageUrl(Carousel carousel) {
        if (StringUtils.hasText(carousel.getImageUrl())) {
            String url = fileUploadUtil.getFileUrl(carousel.getImageUrl());
            // 打印处理前后的URL，便于调试
            System.out.println("处理前URL: " + carousel.getImageUrl() + ", 处理后URL: " + url);
            carousel.setImageUrl(url);
        }
    }
} 