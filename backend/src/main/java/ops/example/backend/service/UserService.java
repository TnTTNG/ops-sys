package ops.example.backend.service;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import ops.example.backend.entity.Account;
import ops.example.backend.entity.User;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.exception.UsersException;
import ops.example.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-18:17
 * @role 普通管理员
 */
@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    public void add(User user) throws CustomerException {
        User dbuser = userMapper.selectByUsername(user.getUsername());
        if (dbuser != null) {
            throw new CustomerException("用户名已存在");
        }
        // 默认密码
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("123456");
        }
        user.setStatus("启用");
        userMapper.insert(user);
    }
    public String user(String name) {
        if ("user".equals(name)) {
            return "user";
        } else {
            throw new UsersException("账号错误");
        }
    }

    public void update(User user) {
        userMapper.updateById(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    public void deleteBatch(List<User> list) {
        for (User user : list) {
            this.deleteById(user.getId());
        }
    }

    public List<User> selectAll() {
        return userMapper.selectAll(null);
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     */
    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize, User user) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAll(user);
        return PageInfo.of(userList);
    }


    public User login(Account account) throws CustomerException {
        // 验证账号是否存在
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (dbUser == null) {
            throw new CustomerException("账号不存在");
        }
        // 验证密码是否正确
        if (!dbUser.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }

        return dbUser;
    }

    public void register(User user) throws CustomerException {
        this.add(user);
    }
}
