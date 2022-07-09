package com.TanNgee.bilibili.dao;

import com.TanNgee.bilibili.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author TanNgee
 * @Date 2022/7/2 22:13
 **/

@Mapper

public interface VideoDao {
    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    Integer pageCountVideos(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);

    List<VideoTag> getVideoTagsByVideoId(Long videoId);

    Integer deleteVideoTags(@Param("tagIdList") List<Long> tagIdList,
                            @Param("videoId") Long videoId);


    Video getVideoById(Long videoId);

    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    void addVideoLike(VideoLike videoLike);

    Integer deleteVideoLike(@Param("videoId") Long videoId,
                            @Param("userId") Long userId);

    Long getVideoLikes(Long videoId);

    Integer deleteVideoCollection(@Param("videoId") Long videoId,
                                  @Param("userId") Long userId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Long getVideoCollections(Long videoId);


    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("videoId") Long videoId,
                                                         @Param("userId") Long userId);


    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId,
                                             @Param("userId") Long userId);

    Integer addVideoCoin(VideoCoin videoCoin);

    Integer updateVideoCoin(VideoCoin videoCoin);

    Long getVideoCoinsAmount(Long videoId);

    Integer addVideoComment(VideoComment videoComment);

    Integer pageCountVideoComments(Map<String, Object> params);

    List<VideoComment> pageListVideoComments(Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(List<Long> parentIdList);

    Video getVideoDetails(Long videoId);

    VideoView getVideoView(Map<String, Object> params);

    void addVideoView(VideoView videoView);

    Integer getVideoViewCounts(Long videoId);
}
