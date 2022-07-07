package com.TanNgee.bilibili.domain;
import java.util.Date;

/**
 * @Author TanNgee
 * @Date 2022/7/3 16:27
 **/
public class Danmu {
    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    private String danmuTime;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDanmuTime() {
        return danmuTime;
    }

    public void setDanmuTime(String danmuTime) {
        this.danmuTime = danmuTime;
    }
}
