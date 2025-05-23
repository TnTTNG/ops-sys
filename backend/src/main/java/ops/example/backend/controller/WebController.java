package ops.example.backend.controller;

import jakarta.annotation.Resource;
import ops.example.backend.common.Result;
import ops.example.backend.entity.Account;
import ops.example.backend.entity.User;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.AdminService;
import ops.example.backend.service.UserService;
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
    @Resource
    UserService userService;

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
    public Result login(@RequestBody Account account) throws CustomerException {
        Account dbAccount = null;
        if ("ADMIN".equals(account.getRole())) {
            dbAccount = adminService.login(account);
        } else if ("USER".equals(account.getRole())){
            dbAccount = userService.login(account);
        } else {
            throw new CustomerException("非法请求");
        }
        return Result.success(dbAccount);
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user) throws CustomerException {
        userService.register(user);
        return Result.success();
    }

}
