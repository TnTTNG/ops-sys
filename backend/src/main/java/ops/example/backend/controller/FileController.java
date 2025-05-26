package ops.example.backend.controller;

import cn.hutool.core.io.FileUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import ops.example.backend.common.Result;
import ops.example.backend.exception.CustomerException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-24-22:57
 * @interface 处理文件上传下载的接口
 */

@RestController
@RequestMapping("/files")
public class FileController {

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {

        String filePath = System.getProperty("user.dir") + "/files/";
        if (!FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;

        // 写入文件
        FileUtil.writeBytes(file.getBytes(), filePath + fileName);
        String url = "http://localhost:9999/files/download/" + fileName;
        return Result.success(url);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws Exception, CustomerException {
        String filePath = System.getProperty("user.dir") + "/files/";
        String realPath = filePath + fileName;
        
        if (!FileUtil.exist(realPath)) {
            throw new CustomerException("文件不存在");
        }

        // 设置响应头
        response.setContentType("image/jpeg");
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
        
        // 写入响应
        ServletOutputStream os = response.getOutputStream();
        os.write(FileUtil.readBytes(realPath));
        os.flush();
        os.close();
    }
}
