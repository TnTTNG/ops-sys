package ops.example.backend.mapper;

import ops.example.backend.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-04-20-22:54
 */
@Mapper
public interface UserMapper {
    List<User> selectAll(User user);
    @Select("select * from `user` where username = #{username}")
    User selectByUsername(String username);
    void insert(User user);


    void updateById(User user);

    @Delete("delete from `user` where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from `user` where id = #{id}")
    User selectById(String id);


    void updatePassword(User user);
}
