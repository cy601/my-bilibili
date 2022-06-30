package com.TanNgee.bilibili.api;

import com.TanNgee.bilibili.api.support.UserSupport;
import com.TanNgee.bilibili.domain.FollowingGroup;
import com.TanNgee.bilibili.domain.JsonResponse;
import com.TanNgee.bilibili.domain.User;
import com.TanNgee.bilibili.domain.UserFollowing;
import com.TanNgee.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户关注列表 Controller
 *
 * @Author TanNgee
 * @Date 2022/6/25 10:38
 **/

@RestController
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;


    /**
     * 添加用户关注
     *
     * @param userFollowing
     * @return
     */
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowing(@RequestBody UserFollowing userFollowing) {

        Long userId = userSupport.getCurrentUserId();   // 当前的操作用户
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 获取用户关注列表，以分组的方式返回
     *
     * @return
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings() {
        Long userId = userSupport.getCurrentUserId(); // 当前的操作用户
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(result);
    }

    /**
     * 获取用户粉丝
     *
     * @return
     */
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentUserId(); // 当前的操作用户
        List<UserFollowing> result = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(result);
    }


    /**
     * 添加用户分组
     *
     * @param followingGroup
     * @return
     */
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);  //分组的Id
        return new JsonResponse<>(groupId);
    }

    /**
     * 获取用户分组
     *
     * @return
     */
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list = userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(list);
    }
}


