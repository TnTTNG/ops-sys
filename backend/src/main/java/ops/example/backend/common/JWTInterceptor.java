package ops.example.backend.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ops.example.backend.entity.Account;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.AdminService;
import ops.example.backend.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-24-16:47
 * @role Token拦截器
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    // 白名单路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/test/connection",  // 测试连接接口
            "/api/test",  // 测试连接接口
            "/admin/login",      // 登录接口
            "/api/admin/register",   // 注册接口
            "/websocket",           // WebSocket接口
            "/websocket/user",      // WebSocket用户接口
            "/api/websocket",       // WebSocket测试接口
            "/websocket/broadcast"  // WebSocket广播接口
    );

    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$");

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取请求路径
        String requestURI = request.getRequestURI();

        // 检查是否是白名单路径
        if (WHITE_LIST.stream().anyMatch(requestURI::contains)) {
            return true;
        }

        // 获取token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            throw new CustomerException("401", "未登录");
        }

        // 验证token格式
        if (!JWT_PATTERN.matcher(token).matches()) {
            throw new CustomerException("401", "token格式错误");
        }

        Account account = null;
        try {
            // 验证token
            DecodedJWT jwt = JWT.decode(token);
            String audience = jwt.getAudience().get(0);
            String[] split = audience.split("-");
            if (split.length != 2) {
                throw new CustomerException("401", "token格式错误");
            }
            String userId = split[0];
            String role = split[1];

            if ("ADMIN".equals(role)) {
                account = adminService.selectById(userId);
            } else if ("USER".equals(role)) {
                account = userService.selectById(userId);
            } else {
                throw new CustomerException("401", "无效的用户角色");
            }
        } catch (Exception e) {
            throw new CustomerException("401", "token验证失败: " + e.getMessage());
        }

        if (account == null) {
            throw new CustomerException("401", "用户不存在");
        }

        try {
            // 验证签名
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            throw new CustomerException("401", "token验证失败: " + e.getMessage());
        }
    }
}
