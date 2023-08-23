package com.sky.controller.admin;

import com.sky.config.OssConfiguration;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Api(tags = "通用controller")
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil ailOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("正在进行文件上传：{}", file);
        //获取文件名
        String filename = file.getOriginalFilename();
        //对文件名进行拆分
        String newFileName = filename.substring(filename.lastIndexOf("."));
        //构造新文件名，防止重复
        String objectName = UUID.randomUUID().toString() + newFileName;

        //文件请求路径
        try {
            String filePath = ailOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败，{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
