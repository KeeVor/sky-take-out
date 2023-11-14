package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.Data;
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
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("图片上传：{}",file);

        try {
            //获取图片的名称
            String name = file.getOriginalFilename();
            //获取图片的格式
            String format = null;
            if (name != null) {
                format = name.substring(name.lastIndexOf("."));
            }
            //使用uuid生成图片的名字
            name = UUID.randomUUID().toString() + format;
            //调用上传工具类
            String upload = aliOssUtil.upload(file.getBytes(), name);
            return Result.success(upload);
        } catch (IOException e) {
            log.info("文件上传失败：{}",e.toString());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
