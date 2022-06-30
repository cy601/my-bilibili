package com.TanNgee.bilibili.api.support;

import com.TanNgee.bilibili.domain.exception.ConditionException;
import com.TanNgee.bilibili.service.UserService;
import com.TanNgee.bilibili.service.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author TanNgee
 * @Date 2022/6/23 21:14
 **/

@Component
public class UserSupport {

    @Autowired
    private UserService userService;

    public Long getCurrentUserId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 从请求头里面取出 token
        String token = request.getHeader("token");
        // 解析
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0) {
            throw new ConditionException("非法用户");
        }
//        this.verifyRefreshToken(userId);
        return userId;
    }
}
