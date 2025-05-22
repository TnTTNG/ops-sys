package ops.example.backend.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import ops.example.backend.common.Result;
import ops.example.backend.entity.Users;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-21-9:21
 */

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    AdminService adminService;

    @PostMapping("/add")
    public Result add(@RequestBody Users users) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.add(users);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Users users) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.update(users);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) throws CustomerException { // @PathVariable 接收前端传来的 路径参数
        adminService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Users> list) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.deleteBatch(list);
        return Result.success();
    }

    @GetMapping("/selectAll") // 完整的请求路径 http://ip:port/admin/selectAll
    public Result selectAll() {
        List<Users> usersList = adminService.selectAll();
        return Result.success(usersList);
    }

    /**
     * 分页查询
     * @pageNum 当前的页码
     * @pageSize 每页的个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Users users) {
        PageInfo<Users> pageInfo = adminService.selectPage(pageNum, pageSize, users);
        return Result.success(pageInfo);
    }
}
