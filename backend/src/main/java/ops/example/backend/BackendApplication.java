package ops.example.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @ComponentScan(basePackages = {"ops.example.backend"})
@MapperScan({"ops.example.backend.mapper", "ops.example.backend.aliyun.mapper"})
// @MapperScan("ops.aliyun.mapper")// 扫描mapper接口层
// @PropertySource("classpath:application.yml")
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
