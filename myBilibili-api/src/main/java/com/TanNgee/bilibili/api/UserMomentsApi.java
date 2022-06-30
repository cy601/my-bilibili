package com.TanNgee.bilibili.api;

import com.TanNgee.bilibili.api.support.UserSupport;
import com.TanNgee.bilibili.domain.JsonResponse;
import com.TanNgee.bilibili.domain.UserMoment;
import com.TanNgee.bilibili.domain.annotation.ApiLimitedRole;
import com.TanNgee.bilibili.domain.annotation.DataLimited;
import com.TanNgee.bilibili.domain.constant.AuthRoleConstant;
import com.TanNgee.bilibili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户动态相关Controller
 *
 * @Author TanNgee
 * @Date 2022/6/28 22:29
 **/

@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;


    /**
     * 新建用户动态
     *
     * @param userMoment
     * @return
     * @throws Exception
     */
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})   //需要控制的角色，LV0的用户不配调用这个接口，通过AOP实现
    @DataLimited  //用于判断在当前角色下传入的数值是否正确（可能权限不足）
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);

        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    /**
     * 获取当前用户订阅的动态
     *
     * @return
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }

}
