package org.petcare.controller;

import lombok.RequiredArgsConstructor;
import org.petcare.common.Result;
import org.petcare.entity.Pet;
import org.petcare.entity.Technician;
import org.petcare.entity.User;
import org.petcare.mapper.PetMapper;
import org.petcare.mapper.TechnicianMapper;
import org.petcare.mapper.UserMapper;
import org.petcare.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final TechnicianMapper technicianMapper;
    private final PetMapper petMapper;

    // 上传目录
    private static final String UPLOAD_DIR = "uploads/avatars/";
    private static final String PET_UPLOAD_DIR = "uploads/pets/";

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<Map<String, Object>> uploadAvatar(@RequestHeader("Authorization") String token,
                                                    @RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(actualToken);

            // 创建上传目录
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            // 更新用户头像
            User user = userMapper.selectById(userId);
            if (user != null) {
                user.setAvatar("/uploads/avatars/" + fileName);
                userMapper.updateById(user);
            }
            
            // 如果是技师，同时更新技师表的头像
            Technician technician = technicianMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Technician>()
                    .eq(Technician::getUserId, userId)
            );
            if (technician != null) {
                technician.setAvatar("/uploads/avatars/" + fileName);
                technicianMapper.updateById(technician);
            }

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/avatars/" + fileName);
            result.put("fileName", fileName);
            
            return Result.success("头像上传成功", result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传宠物照片
     */
    @PostMapping("/pet-photo")
    public Result<Map<String, Object>> uploadPetPhoto(@RequestHeader("Authorization") String token,
                                                      @RequestParam("file") MultipartFile file) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 获取用户ID
            String actualToken = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(actualToken);

            // 创建上传目录
            File uploadDir = new File(PET_UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(PET_UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", "/uploads/pets/" + fileName);
            result.put("fileName", fileName);
            
            return Result.success("宠物照片上传成功", result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
