package ops.example.backend.mapper;

import ops.example.backend.entity.Admin;
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
    List<Admin> selectAll(Admin admin);
    @Select("select * from sys_user where username = #{username}")
    Admin selectByUsername(String username);
    void insert(Admin admin);


    void updateById(Admin admin);

    @Delete("delete from `sys_user` where id = #{id}")
    void deleteById(Integer id);
}
