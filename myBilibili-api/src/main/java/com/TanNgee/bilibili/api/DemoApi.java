package com.TanNgee.bilibili.api;
import com.TanNgee.bilibili.domain.JsonResponse;
import com.TanNgee.bilibili.domain.Video;
import com.TanNgee.bilibili.service.ElasticSearchService;
import com.TanNgee.bilibili.service.util.FastDFSUtil;

import com.TanNgee.bilibili.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author TanNgee
 * @Date 2022/6/21 21:31
 **/
@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;
    @GetMapping("/query")
    public Long query(Long id) {
        return demoService.query(id);
    }


    @GetMapping("/slices")
    public void slices(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
    }

    @GetMapping("/es-videos")
    public JsonResponse<Video> getEsVideos(@RequestParam String keyword){
        Video video = elasticSearchService.getVideos(keyword);
        return new JsonResponse<>(video);
    }
}
