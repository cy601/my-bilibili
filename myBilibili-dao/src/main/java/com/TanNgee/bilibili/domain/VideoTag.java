package com.TanNgee.bilibili.domain;

import java.util.Date;

/**
 * 标签实体
 *
 * @Author TanNgee
 * @Date 2022/7/2 22:16
 **/
public class VideoTag {
    private Long id;

    private Long videoId;

    private Long tagId;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
