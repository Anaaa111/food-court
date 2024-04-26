package com.food.controller.admin;

import com.food.constant.MessageConstant;
import com.food.result.Result;
import com.food.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@Api(tags = "通用接口")
@RequestMapping("admin/common")
public class CommonController {
    @Autowired
    AliOssUtil aliOssUtil;

    @ApiOperation("文件上传")
    @PostMapping("upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file);
        try {
            // 获取到原始的文件名
            String originalFilename = file.getOriginalFilename();
            // 截取原始文件名的后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 通过UUID生成新的文件名(防止文件名重复)
            String objectName = UUID.randomUUID().toString() + suffix;
            // 通过aliOssUtil将文件上传到云端
            String fileUrl = aliOssUtil.upload(file.getBytes(), objectName);
            // 将文件路径返回给前端
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
