package com.stray.animal.rescue.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stray.animal.rescue.dto.CarouselDTO;
import com.stray.animal.rescue.entity.Carousel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 轮播图服务接口
 */
public interface CarouselService extends IService<Carousel> {
    
    /**
     * 获取轮播图列表（分页）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 轮播图分页数据
     */
    Page<Carousel> getCarouselList(Integer pageNum, Integer pageSize, String keyword);
    
    /**
     * 获取所有启用的轮播图（按排序）
     * @return 启用的轮播图列表
     */
    List<Carousel> getActiveCarousels();
    
    /**
     * 添加轮播图
     * @param carouselDTO 轮播图信息
     * @param image 图片文件
     * @return 轮播图ID
     * @throws IOException IO异常
     */
    Long addCarousel(CarouselDTO carouselDTO, MultipartFile image) throws IOException;
    
    /**
     * 更新轮播图
     * @param carouselDTO 轮播图信息
     * @param image 图片文件（可为空）
     * @return 是否成功
     * @throws IOException IO异常
     */
    void updateCarousel(CarouselDTO carouselDTO, MultipartFile image) throws IOException;
    
    /**
     * 删除轮播图
     * @param id 轮播图ID
     * @return 是否成功
     */
    void deleteCarousel(Long id);
    
    /**
     * 更新轮播图状态
     * @param id 轮播图ID
     * @param status 状态 0-禁用 1-正常
     * @return 是否成功
     */
    void updateStatus(Long id, Integer status);
} 