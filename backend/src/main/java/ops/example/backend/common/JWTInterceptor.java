package ops.example.backend.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ops.example.backend.entity.Account;
import ops.example.backend.exception.CustomerException;
import ops.example.backend.service.AdminService;
import ops.example.backend.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author ZhanHui TONG
 * @version 1.0
 * @created_date 2025-05-24-16:47
 * @role Token拦截器
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            token = request.getParameter("token");
        }
        if (StrUtil.isBlank(token)) {
            try {
                throw new CustomerException("无权限操作");
            } catch (CustomerException e) {
                throw new RuntimeException(e);
            }
        }

        Account account = null;
        try {
            // 拿到token 的载荷数据
            String audience = JWT.decode(token).getAudience().get(0);
            String[] split = audience.split("-");
            String userId = split[0];
            String role = split[1];

            if ("ADMIN".equals(role)) {
                account = adminService.selectById(userId);
            } else if ("USER".equals(role)) {
                account = userService.selectById(userId);
            }
        } catch (Exception e) {
            try {
                throw new CustomerException("无权限操作");
            } catch (CustomerException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (account == null) {
            try {
                throw new CustomerException("无权限操作");
            } catch (CustomerException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            // 验证签名
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            try {
                throw new CustomerException("无权限操作");
            } catch (CustomerException ex) {
                throw new RuntimeException(ex);
            }
        }

        return true;
    }
}
