package com.TanNgee.bilibili.domain;
import java.util.Date;

/**
 * @Author TanNgee
 * @Date 2022/6/30 22:48
 **/
public class RefreshTokenDetail {
    private Long id;

    private String refreshToken;

    private Long userId;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
