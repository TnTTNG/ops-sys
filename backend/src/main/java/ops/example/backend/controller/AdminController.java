package ops.example.backend.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import ops.example.backend.common.Result;
import ops.example.backend.entity.Admin;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-21-9:21
 * @作用 超级管理员接口
 */

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    AdminService adminService;

    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.add(admin);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Admin admin) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.update(admin);
        return Result.success();
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Admin admin) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.updatePassword(admin);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) throws CustomerException { // @PathVariable 接收前端传来的 路径参数
        adminService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Admin> list) throws CustomerException { // @RequestBody 接收前端传来的 json参数
        adminService.deleteBatch(list);
        return Result.success();
    }

    @GetMapping("/selectAll") // 完整的请求路径 http://ip:port/admin/selectAll
    public Result selectAll() {
        List<Admin> adminList = adminService.selectAll();
        return Result.success(adminList);
    }

    /**
     * 分页查询
     * @pageNum 当前的页码
     * @pageSize 每页的个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Admin admin) {
        PageInfo<Admin> pageInfo = adminService.selectPage(pageNum, pageSize, admin);
        return Result.success(pageInfo);
    }
}
