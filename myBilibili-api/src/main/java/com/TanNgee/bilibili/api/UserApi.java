package com.TanNgee.bilibili.api;

import com.TanNgee.bilibili.api.support.UserSupport;
import com.TanNgee.bilibili.domain.*;
import com.TanNgee.bilibili.service.UserFollowingService;
import com.TanNgee.bilibili.service.UserService;
import com.TanNgee.bilibili.service.util.RSAUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

/**
 * 用户相关Controller
 *
 * @Author TanNgee
 * @Date 2022/6/22 22:12
 **/
@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();

        User user = userService.getUserInfo(userId);

        return new JsonResponse<>(user);
    }

    /**
     * 获取公钥
     *
     * @return
     */
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();// 添加成功
    }

    /**
     * 获取token
     *
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }


    /**
     * 更新用户信息
     *
     * @param userInfo
     * @return
     */
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    /**
     * 按页查询用户信息
     *
     * @param no
     * @param size
     * @param nick
     * @return
     */
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no, @RequestParam Integer size, String nick) {
        Long userId = userSupport.getCurrentUserId();
        JSONObject params = new JSONObject();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);

        PageResult<UserInfo> result = userService.pageListUserInfos(params);

        if (result.getTotal() > 0) {
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(result.getList(), userId);
            result.setList(checkedUserInfoList);
        }
        return new JsonResponse<>(result);
    }

    /**
     * 双令牌实现无感知登录 access token  和   refreshtoken
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/user-dts")
    public JsonResponse<Map<String, Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String, Object> map = userService.loginForDts(user);
        return new JsonResponse<>(map);
    }


    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(HttpServletRequest request){
        String refreshToken = request.getHeader("refreshToken");  //从前端请求中取出refreshtoken
        Long userId = userSupport.getCurrentUserId();

        userService.logout(refreshToken, userId);
        return JsonResponse.success();
    }


    /**
     * access token过期，更新access token
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/access-tokens")
    public JsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken"); //从前端请求中取出refreshtoken
        String accessToken = userService.refreshAccessToken(refreshToken);  //获取新的accesstoken
        return new JsonResponse<>(accessToken);
    }


}


