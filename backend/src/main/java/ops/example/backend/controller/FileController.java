package ops.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-24-22:57
 * @interface 处理文件上传下载的接口
 */

@RestController
@RequestMapping("/files")
public class FileController {

    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName) {

    }
}
