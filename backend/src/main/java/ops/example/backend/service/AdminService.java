package ops.example.backend.service;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import ops.example.backend.entity.Users;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.exception.UsersException;
import ops.example.backend.mapper.AdminMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-18:17
 */
@Service
public class AdminService {

    @Resource
    AdminMapper adminMapper;

    public void add(Users users) throws CustomerException {
        Users dbuser = adminMapper.selectByUsername(users.getUsername());
        if (dbuser != null) {
            throw new CustomerException("用户名已存在");
        }
        // 默认密码
        if (StrUtil.isBlank(users.getPassword())) {
            users.setPassword("123456");
        }
        adminMapper.insert(users);
    }
    public String admin(String name) {
        if ("admin".equals(name)) {
            return "admin";
        } else {
            throw new UsersException("账号错误");
        }
    }

    public void update(Users users) {
        adminMapper.updateById(users);
    }

    public void deleteById(Integer id) {
        adminMapper.deleteById(id);
    }

    public void deleteBatch(List<Users> list) {
        for (Users users : list) {
            this.deleteById(users.getId());
        }
    }

    public List<Users> selectAll() {
        return adminMapper.selectAll(null);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     */
    public PageInfo<Users> selectPage(Integer pageNum, Integer pageSize, Users users) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<Users> usersList = adminMapper.selectAll(users);
        return PageInfo.of(usersList);
    }


    public Users login(Users users) throws CustomerException {
        // 验证账号是否存在
        Users dbUser = adminMapper.selectByUsername(users.getUsername());
        if (dbUser == null) {
            throw new CustomerException("账号不存在");
        }
        // 验证密码是否正确
        if (!dbUser.getPassword().equals(users.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }

        return dbUser;
    }
}
