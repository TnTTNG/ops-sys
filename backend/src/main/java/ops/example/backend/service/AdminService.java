package ops.example.backend.service;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import ops.example.backend.entity.Account;
import ops.example.backend.entity.Admin;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.exception.UsersException;
import ops.example.backend.mapper.AdminMapper;
import ops.example.backend.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-18:17
 * @role 超级管理员
 */
@Service
public class AdminService {

    @Resource
    AdminMapper adminMapper;
    public void add(Admin admin) throws CustomerException {
        Admin dbuser = adminMapper.selectByUsername(admin.getUsername());
        if (dbuser != null) {
            throw new CustomerException("用户名已存在");
        }
        // 默认密码
        if (StrUtil.isBlank(admin.getPassword())) {
            admin.setPassword("admin");
        }
        admin.setRole("ADMIN");
        adminMapper.insert(admin);
    }
    public String admin(String name) {
        if ("admin".equals(name)) {
            return "admin";
        } else {
            throw new UsersException("账号错误");
        }
    }

    public void update(Admin admin) {
        adminMapper.updateById(admin);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Admin> list) {
        for (Admin admin : list) {
            this.deleteById(admin.getId());
        }
    }

    public Admin selectById(String id) {
        return adminMapper.selectById(id);
    }

    public List<Admin> selectAll() {
        return adminMapper.selectAll(null);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     */
    public PageInfo<Admin> selectPage(Integer pageNum, Integer pageSize, Admin admin) {

        Account currentUser = TokenUtils.getCurrentUser();

        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> adminList = adminMapper.selectAll(admin);
        return PageInfo.of(adminList);
    }


    public Admin login(Account account) throws CustomerException {
        // 验证账号是否存在
        Admin dbUser = adminMapper.selectByUsername(account.getUsername());
        if (dbUser == null) {
            throw new CustomerException("账号不存在");
        }
        // 验证密码是否正确
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }
        // 创建token并返回给前端
        String token = TokenUtils.createToken(dbUser.getId() + "-" + "ADMIN", dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }


    public void updatePassword(Admin admin) {
        String id = admin.getId().toString();
        // 验证原密码是否正确
        Admin admin1 = adminMapper.selectById(id);
        if (admin1 == null) {
            throw new CustomerException("用户不存在");
        }
        if (!admin1.getPassword().equals(admin.getOldPassword())) {
            throw new CustomerException("原密码错误");
        }
        adminMapper.updatePassword(admin);
    }
}
