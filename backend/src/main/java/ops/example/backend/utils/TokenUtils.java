package ops.example.backend.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import ops.example.backend.entity.Account;
import ops.example.backend.service.AdminService;
import ops.example.backend.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-23-22:12
 * @工具类 生成Token
 */
@Component
public class TokenUtils {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    static AdminService staticAdminService;
    static UserService staticUserService;


    // springboot工程启动后会加载这段代码
    @PostConstruct
    public void init() {
        staticAdminService = adminService;
        staticUserService = userService;
    }

    /**
     * 生成Token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // userId-role 保存到 token 里面，作为载荷
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // 1天后token过期
                .sign(Algorithm.HMAC256(sign)); // password 作为 token 的密钥，HMAC256算法加密
    }

    /**
     * 获取当前登录的用户信息
     */
    public static Account getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        // 拿到token 的载荷数据
        String audience = JWT.decode(token).getAudience().get(0);
        String[] split = audience.split("-");
        String userId = split[0];
        String role = split[1];

        if ("ADMIN".equals(role)) {
            return staticAdminService.selectById(userId);
        } else if ("USER".equals(role)) {
            return staticUserService.selectById(userId);
        }
        return null;
    }

}
