package ops.example.backend.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import ops.example.backend.common.Result;
import ops.example.backend.entity.User;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-21-9:21
 * @作用 管理员接口
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/add")
    public Result add(@RequestBody User user) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        userService.add(user);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody User user) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        userService.update(user);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) throws CustomerException { // @PathVariable 接收前端传来的 路径参数
        userService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<User> list) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        userService.deleteBatch(list);
        return Result.success();
    }

    @GetMapping("/selectAll") // 完整的请求路径 http://ip:port/user/selectAll
    public Result selectAll() {
        List<User> userList = userService.selectAll();
        return Result.success(userList);
    }

    /**
     * 分页查询
     * @pageNum 当前的页码
     * @pageSize 每页的个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             User user) {
        PageInfo<User> pageInfo = userService.selectPage(pageNum, pageSize, user);
        return Result.success(pageInfo);
    }
}
