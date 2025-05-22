package ops.example.backend.controller;

import jakarta.annotation.Resource;
import ops.example.backend.common.Result;
import ops.example.backend.entity.Users;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-17:25
 */

@RestController
public class WebController {

    @Resource
    AdminService adminService;

    // 表示一个GET请求接口
    @GetMapping("/hello")
    public Result hello() {
        // Result result = new Result();
        // result.setCode("200");
        // result.setData("hello");
        // int a = 1 / 0;
        return Result.success("hello");
    }

    @GetMapping("/admin")
    public Result admin(String name) {
        String admin = adminService.admin(name);
        return Result.success(admin);
    }

    @PostMapping("/login")
    public Result login(@RequestBody Users users) throws CustomerException {
        Users dbUser = adminService.login(users);
        return Result.success(dbUser);
    }
}
