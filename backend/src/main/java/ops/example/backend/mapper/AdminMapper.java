package ops.example.backend.mapper;

import ops.example.backend.entity.Users;
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
public interface AdminMapper {
    List<Users> selectAll(Users users);
    @Select("select * from sys_user where username = #{username}")
    Users selectByUsername(String username);
    void insert(Users users);


    void updateById(Users users);

    @Delete("delete from `sys_user` where id = #{id}")
    void deleteById(Integer id);
}
