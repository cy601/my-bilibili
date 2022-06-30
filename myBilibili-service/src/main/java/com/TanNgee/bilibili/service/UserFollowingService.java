package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.UserFollowingDao;
import com.TanNgee.bilibili.domain.FollowingGroup;
import com.TanNgee.bilibili.domain.User;
import com.TanNgee.bilibili.domain.UserFollowing;
import com.TanNgee.bilibili.domain.UserInfo;
import com.TanNgee.bilibili.domain.constant.UserConstant;
import com.TanNgee.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author TanNgee
 * @Date 2022/6/24 22:14
 **/
@Service
public class UserFollowingService {


    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;


    /**
     * 添加关注
     *
     * @param userFollowing
     */
    @Transactional   // 事务操作
    public void addUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        // 没有写分组，添加到默认之中
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }
        //用户指定了
        else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在！");
            }
        }

        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId); //关注的那个人
        if (user == null) {
            throw new ConditionException("关注的用户不存在！");
        }


        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);   //删除掉之前的关联关系
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);  // 添加关联关系
    }


    /**
     * 获取用户的关注列表，以分组的形式返回
     *
     * @param userId
     * @return
     */
    // 第一步：获取关注的用户列表
    // 第二步：根据关注用户的id查询关注用户的基本信息
    // 第三步：将关注用户按关注分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId) {

        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);  //用户关注列表

        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());  //用户关注的用户的id集合

        List<UserInfo> userInfoList = new ArrayList<>();

        if (followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);   //被关注者的信息集合
        }

        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);  //对被关注用户信息进行匹配
                }
            }
        }

        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);  // 用户的所有分组

        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);  //全部添加到默认分组中


        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        for (FollowingGroup group : groupList) { //对不同的关注列表中的关注用户进行归类
            List<UserInfo> infoList = new ArrayList<>();

            for (UserFollowing userFollowing : list) {
                if (group.getId().equals(userFollowing.getGroupId())) {  //进行匹配
                    infoList.add(userFollowing.getUserInfo());
                }

            }
            group.setFollowingUserInfoList(infoList);

            result.add(group);
        }
        return result;
    }

    /**
     * 获取用户粉丝列表
     *
     * @param userId
     * @return
     */
    //第一步：获取当前用户的粉丝列表
    // 第二步：根据粉丝的用户id查询基本信息
    // 第三步：查询当前用户是否已经关注该粉丝
    public List<UserFollowing> getUserFans(Long userId) {

        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId); // 获取用户粉丝
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet()); // 把粉丝ID抽取出来
        List<UserInfo> userInfoList = new ArrayList<>();

        if (fanIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);  // 粉丝列表的详细信息
        }
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);  //获取当前用户的关注列表，看看和粉丝列表有没有匹配的，如果有，那就是互粉


        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : userInfoList) {
                //进行粉丝信息匹配
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            // 进行匹配，看看是不是互粉
            for (UserFollowing following : followingList) {
                if (following.getFollowingId().equals(fan.getUserId())) {
                    fan.getUserInfo().setFollowed(true);   // 互粉列表
                }
            }
        }

        return fanList;
    }

    /**
     * 新建关注分组
     *
     * @param followingGroup
     * @return
     */
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);  //自定义分组 3
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    /**
     * 获取用户分组
     *
     * @param userId
     * @return
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    /**
     * 查询当前列表的用户是否已经关注
     *
     * @param userInfoList
     * @param userId
     * @return
     */
    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {

        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}






