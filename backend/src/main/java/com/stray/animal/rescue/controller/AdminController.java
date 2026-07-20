package com.stray.animal.rescue.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stray.animal.rescue.common.Result;
import com.stray.animal.rescue.dto.UserInfoDTO;
import com.stray.animal.rescue.dto.UserUpdateDTO;
import com.stray.animal.rescue.entity.User;
import com.stray.animal.rescue.entity.Carousel;
import com.stray.animal.rescue.entity.Announcement;
import com.stray.animal.rescue.entity.VideoCategory;
import com.stray.animal.rescue.entity.Video;
import com.stray.animal.rescue.entity.AnimalCategory;
import com.stray.animal.rescue.entity.Animal;
import com.stray.animal.rescue.entity.RescueInfo;
import com.stray.animal.rescue.entity.KnowledgeCategory;
import com.stray.animal.rescue.entity.KnowledgeArticle;
import com.stray.animal.rescue.mapper.UserMapper;
import com.stray.animal.rescue.service.UserService;
import com.stray.animal.rescue.service.CarouselService;
import com.stray.animal.rescue.service.AnnouncementService;
import com.stray.animal.rescue.service.VideoCategoryService;
import com.stray.animal.rescue.service.VideoService;
import com.stray.animal.rescue.service.AnimalCategoryService;
import com.stray.animal.rescue.service.AnimalService;
import com.stray.animal.rescue.service.AdoptionApplicationService;
import com.stray.animal.rescue.service.KnowledgeCategoryService;
import com.stray.animal.rescue.service.KnowledgeArticleService;
import com.stray.animal.rescue.service.RescueInfoService;
import com.stray.animal.rescue.util.FileUploadUtil;
import com.stray.animal.rescue.dto.CarouselDTO;
import com.stray.animal.rescue.dto.AnnouncementDTO;
import com.stray.animal.rescue.dto.VideoCategoryDTO;
import com.stray.animal.rescue.dto.VideoDTO;
import com.stray.animal.rescue.dto.AnimalCategoryDTO;
import com.stray.animal.rescue.dto.AnimalDTO;
import com.stray.animal.rescue.dto.AdoptionApplicationDTO;
import com.stray.animal.rescue.dto.AdoptionAuditDTO;
import com.stray.animal.rescue.dto.KnowledgeCategoryDTO;
import com.stray.animal.rescue.dto.KnowledgeArticleDTO;
import com.stray.animal.rescue.dto.RescueInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 管理员控制器
 */
@Slf4j
@Tag(name = "管理员接口", description = "管理员用户管理接口")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private UserService userService;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private FileUploadUtil fileUploadUtil;
    
    @Resource
    private CarouselService carouselService;

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private VideoCategoryService videoCategoryService;
    
    @Resource
    private VideoService videoService;

    @Resource
    private AnimalCategoryService animalCategoryService;
    
    @Resource
    private AnimalService animalService;
    
    @Resource
    private AdoptionApplicationService adoptionApplicationService;
    
    @Resource
    private KnowledgeCategoryService knowledgeCategoryService;
    
    @Resource
    private KnowledgeArticleService knowledgeArticleService;

    @Resource
    private RescueInfoService rescueInfoService;

    /**
     * 获取用户列表
     */
    @Operation(summary = "获取用户列表")
    @GetMapping("/user/list")
    public Result<Page<User>> getUserList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        Page<User> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(User::getUsername, keyword)
                    .or().like(User::getName, keyword)
                    .or().like(User::getPhone, keyword);
        }
        
        // 排序
        queryWrapper.orderByDesc(User::getCreateTime);
        
        // 查询
        Page<User> userPage = userMapper.selectPage(pageParam, queryWrapper);
        
        // 脱敏处理
        userPage.getRecords().forEach(user -> {
            user.setPassword(null);
            // 处理头像URL
            if (StringUtils.hasText(user.getAvatar())) {
                user.setAvatar(fileUploadUtil.getFileUrl(user.getAvatar()));
            }
        });
        
        return Result.success(userPage);
    }
    
    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/user/{id}")
    public Result<UserInfoDTO> getUserDetail(@PathVariable Long id) {
        UserInfoDTO userInfo = userService.getUserInfo(id);
        if (userInfo == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userInfo);
    }
    
    /**
     * 更新用户信息
     * 接收 JSON 请求体；如需上传头像，请先调用 /admin/upload 拿到相对路径再放入 avatar 字段
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/user/{id}")
    public Result<Void> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDTO updateDTO) {

        // 获取用户
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 不允许通过此接口修改管理员账号
        if (user.getRole() != null && user.getRole() == 1
                && (updateDTO.getStatus() != null && !updateDTO.getStatus().equals(user.getStatus()))) {
            return Result.error("不能修改管理员状态");
        }

        // 仅更新非空字段（按业务允许覆盖空字符串：手机号、邮箱、地址）
        if (StringUtils.hasText(updateDTO.getName())) {
            user.setName(updateDTO.getName());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getAddress() != null) {
            user.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }
        if (StringUtils.hasText(updateDTO.getAvatar())) {
            user.setAvatar(updateDTO.getAvatar());
        }

        // 保存用户
        userMapper.updateById(user);

        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/user/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // 获取用户
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 检查是否为管理员
        if (user.getRole() == 1) {
            return Result.error("不能删除管理员用户");
        }
        
        // 删除用户
        userMapper.deleteById(id);
        
        // 删除头像
        if (StringUtils.hasText(user.getAvatar())) {
            fileUploadUtil.deleteFile(user.getAvatar());
        }
        
        return Result.success();
    }
    
    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态")
    @PutMapping("/user/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> param) {
        
        Integer status = (Integer) param.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        
        // 获取用户
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 检查是否为管理员
        if (user.getRole() == 1) {
            return Result.error("不能修改管理员状态");
        }
        
        // 更新状态
        user.setStatus(status);
        userMapper.updateById(user);
        
        return Result.success();
    }
    
    /**
     * 重置用户密码
     */
    @Operation(summary = "重置用户密码")
    @PutMapping("/user/{id}/password/reset")
    public Result<Void> resetUserPassword(@PathVariable Long id) {
        // 获取用户
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 重置密码为123456
        user.setPassword("123456");
        userMapper.updateById(user);
        
        return Result.success();
    }

    /**
     * 获取轮播图列表
     */
    @Operation(summary = "获取轮播图列表")
    @GetMapping("/carousel/list")
    public Result<Page<Carousel>> getCarouselList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Carousel> page = carouselService.getCarouselList(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    /**
     * 添加轮播图
     */
    @Operation(summary = "添加轮播图")
    @PostMapping("/carousel/add")
    public Result<Long> addCarousel(@Valid CarouselDTO carouselDTO,
                                    @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        // 如果没有传图片，但carouselDTO中有imageUrl，说明图片已经上传过
        if ((image == null || image.isEmpty()) && (carouselDTO.getImageUrl() == null || carouselDTO.getImageUrl().isEmpty())) {
            return Result.error("图片不能为空");
        }
        
        // 如果carouselDTO中的imageUrl以/uploads开头，则移除该前缀
        if (StringUtils.hasText(carouselDTO.getImageUrl()) && carouselDTO.getImageUrl().startsWith("/uploads")) {
            carouselDTO.setImageUrl(carouselDTO.getImageUrl().substring("/uploads".length()));
        }
        
        Long id = carouselService.addCarousel(carouselDTO, image);
        return Result.success(id);
    }
    
    /**
     * 修改轮播图
     */
    @Operation(summary = "修改轮播图")
    @PostMapping("/carousel/update")
    public Result<Void> updateCarousel(@Valid CarouselDTO carouselDTO,
                                       @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        carouselService.updateCarousel(carouselDTO, image);
        return Result.success();
    }
    
    /**
     * 删除轮播图
     */
    @Operation(summary = "删除轮播图")
    @PostMapping("/carousel/delete/{id}")
    public Result<Void> deleteCarousel(@PathVariable Long id) {
        carouselService.deleteCarousel(id);
        return Result.success();
    }
    
    /**
     * 修改轮播图状态
     */
    @Operation(summary = "修改轮播图状态")
    @PostMapping("/carousel/status/{id}/{status}")
    public Result<Void> updateCarouselStatus(@PathVariable Long id, @PathVariable Integer status) {
        carouselService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 通用文件上传接口
     */
    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUploadUtil.uploadFile(file, "carousel");
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error("文件上传失败");
        }
    }

    /**
     * 分页查询公告列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/announcement/page")
    public Result<Page<Announcement>> getAnnouncementPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(announcementService.getAnnouncementPage(pageNum, pageSize));
    }
    
    /**
     * 新增公告
     * @param announcementDTO 公告信息
     * @return 操作结果
     */
    @PostMapping("/announcement")
    public Result<Void> addAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO) {
        announcementService.addAnnouncement(announcementDTO);
        return Result.success();
    }
    
    /**
     * 更新公告
     * @param announcementDTO 公告信息
     * @return 操作结果
     */
    @PutMapping("/announcement")
    public Result<Void> updateAnnouncement(@Valid @RequestBody AnnouncementDTO announcementDTO) {
        announcementService.updateAnnouncement(announcementDTO);
        return Result.success();
    }
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/announcement/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }
    
    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return 公告信息
     */
    @GetMapping("/announcement/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        return Result.success(announcementService.getAnnouncementById(id));
    }

    /**
     * 分页查询视频分类
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    @GetMapping("/video/category/page")
    public Result<Page<VideoCategory>> getVideoCategoryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(videoCategoryService.getCategoryPage(pageNum, pageSize, keyword));
    }
    
    /**
     * 获取所有视频分类
     * @return 分类列表
     */
    @GetMapping("/video/category/list")
    public Result<List<VideoCategory>> getAllVideoCategories() {
        return Result.success(videoCategoryService.getAllCategories());
    }
    
    /**
     * 根据ID查询视频分类
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/video/category/{id}")
    public Result<VideoCategory> getVideoCategoryById(@PathVariable Long id) {
        return Result.success(videoCategoryService.getCategoryById(id));
    }
    
    /**
     * 添加视频分类
     * @param categoryDTO 分类信息
     * @return 操作结果
     */
    @PostMapping("/video/category")
    public Result<Void> addVideoCategory(@Valid @RequestBody VideoCategoryDTO categoryDTO) {
        videoCategoryService.addCategory(categoryDTO);
        return Result.success();
    }
    
    /**
     * 更新视频分类
     * @param categoryDTO 分类信息
     * @return 操作结果
     */
    @PutMapping("/video/category")
    public Result<Void> updateVideoCategory(@Valid @RequestBody VideoCategoryDTO categoryDTO) {
        videoCategoryService.updateCategory(categoryDTO);
        return Result.success();
    }
    
    /**
     * 删除视频分类
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/video/category/{id}")
    public Result<Void> deleteVideoCategory(@PathVariable Long id) {
        try {
            videoCategoryService.deleteCategory(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询视频
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @return 分页结果
     */
    @GetMapping("/video/page")
    public Result<Page<Video>> getVideoPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(videoService.getVideoPage(pageNum, pageSize, keyword, categoryId));
    }
    
    /**
     * 根据ID查询视频
     * @param id 视频ID
     * @return 视频信息
     */
    @GetMapping("/video/{id}")
    public Result<Video> getVideoById(@PathVariable Long id) {
        return Result.success(videoService.getVideoById(id));
    }
    
    /**
     * 添加视频
     * @param videoDTO 视频信息
     * @return 操作结果
     */
    @PostMapping("/video")
    public Result<Void> addVideo(@Valid @RequestBody VideoDTO videoDTO) {
        videoService.addVideo(videoDTO);
        return Result.success();
    }
    
    /**
     * 更新视频
     * @param videoDTO 视频信息
     * @return 操作结果
     */
    @PutMapping("/video")
    public Result<Void> updateVideo(@Valid @RequestBody VideoDTO videoDTO) {
        videoService.updateVideo(videoDTO);
        return Result.success();
    }
    
    /**
     * 删除视频
     * @param id 视频ID
     * @return 操作结果
     */
    @DeleteMapping("/video/{id}")
    public Result<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return Result.success();
    }
    
    /**
     * 上传视频文件
     * @param file 视频文件
     * @return 视频URL
     */
    @PostMapping("/video/upload")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择视频文件");
        }
        try {
            String videoUrl = videoService.uploadVideo(file);
            return Result.success(fileUploadUtil.getFileUrl(videoUrl));
        } catch (Exception e) {
            return Result.error("上传视频失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传视频封面
     * @param file 封面文件
     * @return 封面URL
     */
    @PostMapping("/video/cover/upload")
    public Result<String> uploadVideoCover(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择封面图片");
        }
        try {
            String coverUrl = videoService.uploadCover(file);
            return Result.success(fileUploadUtil.getFileUrl(coverUrl));
        } catch (Exception e) {
            return Result.error("上传封面失败：" + e.getMessage());
        }
    }

    /**
     * 获取动物分类列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    @GetMapping("/animal/category/page")
    public Result<Page<AnimalCategory>> getAnimalCategoryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<AnimalCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AnimalCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(AnimalCategory::getName, keyword)
                        .or()
                        .like(AnimalCategory::getDescription, keyword);
        }
        
        queryWrapper.orderByDesc(AnimalCategory::getCreateTime);
        
        return Result.success(animalCategoryService.page(page, queryWrapper));
    }
    
    /**
     * 获取所有动物分类
     * @return 分类列表
     */
    @GetMapping("/animal/category/list")
    public Result<List<AnimalCategory>> getAllAnimalCategories() {
        return Result.success(animalCategoryService.listAllCategories());
    }
    
    /**
     * 根据ID查询动物分类
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/animal/category/{id}")
    public Result<AnimalCategory> getAnimalCategoryById(@PathVariable Long id) {
        return Result.success(animalCategoryService.getCategoryById(id));
    }
    
    /**
     * 添加动物分类
     * @param categoryDTO 分类信息
     * @return 操作结果
     */
    @PostMapping("/animal/category")
    public Result<Void> addAnimalCategory(@Valid @RequestBody AnimalCategoryDTO categoryDTO) {
        try {
            animalCategoryService.addCategory(categoryDTO);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新动物分类
     * @param categoryDTO 分类信息
     * @return 操作结果
     */
    @PutMapping("/animal/category")
    public Result<Void> updateAnimalCategory(@Valid @RequestBody AnimalCategoryDTO categoryDTO) {
        try {
            animalCategoryService.updateCategory(categoryDTO);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除动物分类
     * @param id 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/animal/category/{id}")
    public Result<Void> deleteAnimalCategory(@PathVariable Long id) {
        try {
            animalCategoryService.deleteCategory(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询动物
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @param status 状态
     * @return 分页结果
     */
    @GetMapping("/animal/page")
    public Result<Page<Animal>> getAnimalPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        Page<Animal> page = animalService.getAnimalPage(pageNum, pageSize, keyword, categoryId, status);
        
        // 处理图片URL
        page.getRecords().forEach(animal -> {
            if (StringUtils.hasText(animal.getImageUrl())) {
                animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
            }
        });
        
        return Result.success(page);
    }
    
    /**
     * 根据ID查询动物
     * @param id 动物ID
     * @return 动物信息
     */
    @GetMapping("/animal/{id}")
    public Result<Animal> getAnimalById(@PathVariable Long id) {
        Animal animal = animalService.getAnimalById(id);
        
        if (animal == null) {
            return Result.error("动物不存在");
        }
        
        // 处理图片URL
        if (StringUtils.hasText(animal.getImageUrl())) {
            animal.setImageUrl(fileUploadUtil.getFileUrl(animal.getImageUrl()));
        }
        
        return Result.success(animal);
    }
    
    /**
     * 添加动物
     * @param animalDTO 动物信息
     * @return 操作结果
     */
    @PostMapping("/animal")
    public Result<Void> addAnimal(@Valid @RequestBody AnimalDTO animalDTO) {
        try {
            animalService.addAnimal(animalDTO);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新动物
     * @param animalDTO 动物信息
     * @return 操作结果
     */
    @PutMapping("/animal")
    public Result<Void> updateAnimal(@Valid @RequestBody AnimalDTO animalDTO) {
        try {
            animalService.updateAnimal(animalDTO);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除动物
     * @param id 动物ID
     * @return 操作结果
     */
    @DeleteMapping("/animal/{id}")
    public Result<Void> deleteAnimal(@PathVariable Long id) {
        try {
            animalService.deleteAnimal(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传动物图片
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/animal/upload")
    public Result<String> uploadAnimalImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        
        try {
            String imageUrl = animalService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            log.error("上传动物图片失败: {}", e.getMessage());
            return Result.error("上传图片失败: " + e.getMessage());
        }
    }

    // ================ 救助信息管理 ================

    /**
     * 获取救助信息列表（分页）
     */
    @Operation(summary = "获取救助信息列表（分页）")
    @GetMapping("/rescue/page")
    public Result<Page<RescueInfo>> getRescueInfoPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        Page<RescueInfo> page = rescueInfoService.getRescueInfoPage(pageNum, pageSize, keyword, status);
        
        // 处理图片URL
        page.getRecords().forEach(rescueInfo -> {
            if (StringUtils.hasText(rescueInfo.getImageUrl())) {
                rescueInfo.setImageUrl(fileUploadUtil.getFileUrl(rescueInfo.getImageUrl()));
            }
        });
        
        return Result.success(page);
    }
    
    /**
     * 获取救助信息详情
     */
    @Operation(summary = "获取救助信息详情")
    @GetMapping("/rescue/{id}")
    public Result<RescueInfo> getRescueInfo(@PathVariable Long id) {
        RescueInfo rescueInfo = rescueInfoService.getById(id);
        if (rescueInfo == null) {
            return Result.error("救助信息不存在");
        }
        
        // 处理图片URL
        if (StringUtils.hasText(rescueInfo.getImageUrl())) {
            rescueInfo.setImageUrl(fileUploadUtil.getFileUrl(rescueInfo.getImageUrl()));
        }
        
        return Result.success(rescueInfo);
    }
    
    /**
     * 添加救助信息
     */
    @Operation(summary = "添加救助信息")
    @PostMapping("/rescue")
    public Result<Void> addRescueInfo(@RequestBody @Valid RescueInfoDTO rescueInfoDTO) {
        // 设置默认状态为待救助
        if (rescueInfoDTO.getStatus() == null) {
            rescueInfoDTO.setStatus(0);
        }
        
        // 设置发布用户为当前管理员（从session或token中获取）
        User currentUser = getCurrentUser();
        rescueInfoDTO.setUserId(currentUser.getId());
        
        rescueInfoService.addRescueInfo(rescueInfoDTO);
        return Result.success();
    }
    
    /**
     * 更新救助信息
     */
    @Operation(summary = "更新救助信息")
    @PutMapping("/rescue")
    public Result<Void> updateRescueInfo(@RequestBody @Valid RescueInfoDTO rescueInfoDTO) {
        rescueInfoService.updateRescueInfo(rescueInfoDTO);
        return Result.success();
    }
    
    /**
     * 更新救助信息状态
     */
    @Operation(summary = "更新救助信息状态")
    @PutMapping("/rescue/status")
    public Result<Void> updateRescueStatus(
            @RequestParam Long id,
            @RequestParam Integer status
    ) {
        rescueInfoService.updateStatus(id, status);
        return Result.success();
    }
    
    /**
     * 删除救助信息
     */
    @Operation(summary = "删除救助信息")
    @DeleteMapping("/rescue/{id}")
    public Result<Void> deleteRescueInfo(@PathVariable Long id) {
        rescueInfoService.removeById(id);
        return Result.success();
    }
    
    /**
     * 上传救助图片
     */
    @Operation(summary = "上传救助图片")
    @PostMapping("/rescue/upload")
    public Result<String> uploadRescueImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = rescueInfoService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (IOException e) {
            log.error("上传救助图片失败", e);
            return Result.error("上传图片失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询领养申请列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 申请状态 0-待审核 1-通过 2-拒绝
     * @return 分页结果
     */
    @Operation(summary = "查询领养申请列表")
    @GetMapping("/adoption/page")
    public Result<Page<AdoptionApplicationDTO>> getAdoptionPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        try {
            Page<AdoptionApplicationDTO> page = adoptionApplicationService.getAdoptionPage(pageNum, pageSize, status);
            return Result.success(page);
        } catch (Exception e) {
            log.error("获取领养申请列表失败", e);
            return Result.error("获取领养申请列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 查看领养申请详情
     * @param id 申请ID
     * @return 申请详情
     */
    @Operation(summary = "查看领养申请详情")
    @GetMapping("/adoption/{id}")
    public Result<AdoptionApplicationDTO> getAdoptionDetail(@PathVariable Long id) {
        AdoptionApplicationDTO application = adoptionApplicationService.getApplicationById(id);
        
        if (application == null) {
            return Result.error("申请不存在");
        }
        
        return Result.success(application);
    }
    
    /**
     * 审核领养申请
     * @param auditDTO 审核信息
     * @return 操作结果
     */
    @Operation(summary = "审核领养申请")
    @PostMapping("/adoption/audit")
    public Result<Void> auditAdoption(@Valid @RequestBody AdoptionAuditDTO auditDTO) {
        try {
            adoptionApplicationService.auditAdoption(auditDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("审核领养申请失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        // 从Session或Redis中获取当前用户信息，这里暂时返回固定的管理员用户
        return userService.getById(1L);
    }

    /**
     * 知识分类管理
     */
    
    /**
     * 分页查询知识分类
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 分页结果
     */
    @Operation(summary = "分页查询知识分类")
    @GetMapping("/knowledge/category/page")
    public Result<Page<KnowledgeCategory>> getKnowledgeCategoryPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        Page<KnowledgeCategory> page = knowledgeCategoryService.getCategoryPage(pageNum, pageSize, keyword);
        return Result.success(page);
    }
    
    /**
     * 获取所有知识分类
     * @return 分类列表
     */
    @Operation(summary = "获取所有知识分类")
    @GetMapping("/knowledge/category/list")
    public Result<List<KnowledgeCategory>> getAllKnowledgeCategories() {
        List<KnowledgeCategory> categories = knowledgeCategoryService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 根据ID获取知识分类
     * @param id 分类ID
     * @return 分类信息
     */
    @Operation(summary = "根据ID获取知识分类")
    @GetMapping("/knowledge/category/{id}")
    public Result<KnowledgeCategory> getKnowledgeCategoryById(@PathVariable Long id) {
        KnowledgeCategory category = knowledgeCategoryService.getCategoryById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }
    
    /**
     * 添加知识分类
     * @param categoryDTO 分类信息
     * @return 操作结果
     */
    @Operation(summary = "添加知识分类")
    @PostMapping("/knowledge/category")
    public Result<Void> addKnowledgeCategory(@Valid @RequestBody KnowledgeCategoryDTO categoryDTO) {
        try {
            knowledgeCategoryService.addCategory(categoryDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新知识分类
     * @param categoryDTO 分类信息
     * @return 操作结果
     */
    @Operation(summary = "更新知识分类")
    @PutMapping("/knowledge/category")
    public Result<Void> updateKnowledgeCategory(@Valid @RequestBody KnowledgeCategoryDTO categoryDTO) {
        try {
            knowledgeCategoryService.updateCategory(categoryDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除知识分类
     * @param id 分类ID
     * @return 操作结果
     */
    @Operation(summary = "删除知识分类")
    @DeleteMapping("/knowledge/category/{id}")
    public Result<Void> deleteKnowledgeCategory(@PathVariable Long id) {
        try {
            knowledgeCategoryService.deleteCategory(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 知识文章管理
     */
    
    /**
     * 分页查询知识文章
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @param categoryId 分类ID
     * @return 分页结果
     */
    @Operation(summary = "分页查询知识文章")
    @GetMapping("/knowledge/article/page")
    public Result<Page<KnowledgeArticle>> getKnowledgeArticlePage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        
        Page<KnowledgeArticle> page = knowledgeArticleService.getArticlePage(pageNum, pageSize, keyword, categoryId);
        
        // 处理图片URL
        page.getRecords().forEach(article -> {
            if (StringUtils.hasText(article.getImageUrl())) {
                article.setImageUrl(fileUploadUtil.getFileUrl(article.getImageUrl()));
            }
            if (StringUtils.hasText(article.getVideoUrl())) {
                article.setVideoUrl(fileUploadUtil.getFileUrl(article.getVideoUrl()));
            }
            if (StringUtils.hasText(article.getVideoCoverUrl())) {
                article.setVideoCoverUrl(fileUploadUtil.getFileUrl(article.getVideoCoverUrl()));
            }
        });
        
        return Result.success(page);
    }
    
    /**
     * 根据ID获取知识文章
     * @param id 文章ID
     * @return 文章信息
     */
    @Operation(summary = "根据ID获取知识文章")
    @GetMapping("/knowledge/article/{id}")
    public Result<KnowledgeArticle> getKnowledgeArticleById(@PathVariable Long id) {
        KnowledgeArticle article = knowledgeArticleService.getArticleById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        
        // 处理图片URL
        if (StringUtils.hasText(article.getImageUrl())) {
            article.setImageUrl(fileUploadUtil.getFileUrl(article.getImageUrl()));
        }
        if (StringUtils.hasText(article.getVideoUrl())) {
            article.setVideoUrl(fileUploadUtil.getFileUrl(article.getVideoUrl()));
        }
        if (StringUtils.hasText(article.getVideoCoverUrl())) {
            article.setVideoCoverUrl(fileUploadUtil.getFileUrl(article.getVideoCoverUrl()));
        }
        
        return Result.success(article);
    }
    
    /**
     * 添加知识文章
     * @param articleDTO 文章信息
     * @return 操作结果
     */
    @Operation(summary = "添加知识文章")
    @PostMapping("/knowledge/article")
    public Result<Void> addKnowledgeArticle(@Valid @RequestBody KnowledgeArticleDTO articleDTO) {
        try {
            knowledgeArticleService.addArticle(articleDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新知识文章
     * @param articleDTO 文章信息
     * @return 操作结果
     */
    @Operation(summary = "更新知识文章")
    @PutMapping("/knowledge/article")
    public Result<Void> updateKnowledgeArticle(@Valid @RequestBody KnowledgeArticleDTO articleDTO) {
        try {
            knowledgeArticleService.updateArticle(articleDTO);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除知识文章
     * @param id 文章ID
     * @return 操作结果
     */
    @Operation(summary = "删除知识文章")
    @DeleteMapping("/knowledge/article/{id}")
    public Result<Void> deleteKnowledgeArticle(@PathVariable Long id) {
        try {
            knowledgeArticleService.deleteArticle(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传知识文章封面图片
     * @param file 图片文件
     * @return 图片URL
     */
    @Operation(summary = "上传知识文章封面图片")
    @PostMapping("/knowledge/article/upload/image")
    public Result<String> uploadKnowledgeArticleImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = knowledgeArticleService.uploadImage(file);
            return Result.success(fileUploadUtil.getFileUrl(imageUrl));
        } catch (IOException e) {
            log.error("上传知识文章封面图片失败", e);
            return Result.error("上传图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传知识文章视频
     * @param file 视频文件
     * @return 视频URL
     */
    @Operation(summary = "上传知识文章视频")
    @PostMapping("/knowledge/article/upload/video")
    public Result<String> uploadKnowledgeArticleVideo(@RequestParam("file") MultipartFile file) {
        try {
            String videoUrl = knowledgeArticleService.uploadVideo(file);
            return Result.success(fileUploadUtil.getFileUrl(videoUrl));
        } catch (IOException e) {
            log.error("上传知识文章视频失败", e);
            return Result.error("上传视频失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传知识文章视频封面
     * @param file 图片文件
     * @return 封面URL
     */
    @Operation(summary = "上传知识文章视频封面")
    @PostMapping("/knowledge/article/upload/video-cover")
    public Result<String> uploadKnowledgeArticleVideoCover(@RequestParam("file") MultipartFile file) {
        try {
            String coverUrl = knowledgeArticleService.uploadVideoCover(file);
            return Result.success(fileUploadUtil.getFileUrl(coverUrl));
        } catch (IOException e) {
            log.error("上传知识文章视频封面失败", e);
            return Result.error("上传视频封面失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 1. 动物种类分布统计
            List<Map<String, Object>> categoryStats = animalService.getAnimalCategoryStats();
            stats.put("categoryStats", categoryStats);
            
            // 2. 领养状态统计
            List<Map<String, Object>> adoptionStats = animalService.getAnimalAdoptionStats();
            stats.put("adoptionStats", adoptionStats);
            
            // 3. 健康状况统计
            List<Map<String, Object>> healthStats = animalService.getAnimalHealthStats();
            stats.put("healthStats", healthStats);
            
            // 4. 领养申请状态统计
            List<Map<String, Object>> applicationStats = adoptionApplicationService.getAdoptionApplicationStats();
            stats.put("applicationStats", applicationStats);
            
            // 5. 救助信息状态统计
            List<Map<String, Object>> rescueStats = rescueInfoService.getRescueInfoStats();
            stats.put("rescueStats", rescueStats);
            
            // 6. 总计数量
            Map<String, Object> totalCounts = new HashMap<>();
            totalCounts.put("animalCount", animalService.count());
            totalCounts.put("userCount", userService.count());
            totalCounts.put("adoptionCount", adoptionApplicationService.count());
            totalCounts.put("rescueCount", rescueInfoService.count());
            stats.put("totalCounts", totalCounts);
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return Result.error("获取仪表盘统计数据失败: " + e.getMessage());
        }
    }
    
    // ==================== 领养申请管理 - 新增备注功能 ====================
    
    /**
     * 添加/修改备注
     */
    @Operation(summary = "添加/修改备注")
    @PostMapping("/adoption/remark")
    public Result<String> addAdoptionRemark(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.valueOf(params.get("id").toString());
            String remark = params.get("remark").toString();
            adoptionApplicationService.addRemark(id, remark);
            return Result.success("备注添加成功");
        } catch (Exception e) {
            log.error("添加备注失败", e);
            return Result.error("添加备注失败: " + e.getMessage());
        }
    }
}
