package com.smartcoffee.controller;

import com.smartcoffee.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class AdminUploadController {
  @Resource private ServletContext servletContext;

  @PostMapping("/image")
  // 接收后台上传的商品图片文件，保存到本地 uploads 目录并返回访问路径。
  public Result<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
    if (file == null || file.isEmpty()) {
      return Result.fail(400, "文件为空");
    }
    String original = file.getOriginalFilename();
    String ext = "";
    if (original != null && original.lastIndexOf('.') >= 0) {
      ext = original.substring(original.lastIndexOf('.'));
    }
    String filename = UUID.randomUUID().toString().replace("-", "") + ext;
    String uploadDir = new File(System.getProperty("user.dir"), "uploads").getAbsolutePath();
    File dir = new File(uploadDir);
    if (!dir.exists()) dir.mkdirs();
    File dest = new File(dir, filename);
    file.transferTo(dest);
    String urlPath = "/uploads/" + filename;
    return Result.ok(urlPath);
  }
}
