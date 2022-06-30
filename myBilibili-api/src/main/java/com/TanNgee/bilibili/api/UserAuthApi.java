package com.TanNgee.bilibili.api;

import com.TanNgee.bilibili.api.support.UserSupport;
import com.TanNgee.bilibili.domain.JsonResponse;
import com.TanNgee.bilibili.domain.auth.UserAuthorities;
import com.TanNgee.bilibili.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author TanNgee
 * @Date 2022/6/29 21:56
 **/
@RestController

public class UserAuthApi {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 获取当前的用户权限
     *
     * @return
     */
    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities() {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }

}
