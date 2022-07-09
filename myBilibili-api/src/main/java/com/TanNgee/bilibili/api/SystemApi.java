package com.TanNgee.bilibili.api;

import com.TanNgee.bilibili.domain.JsonResponse;
import com.TanNgee.bilibili.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 使用ES全文搜索
 *
 * @Author TanNgee
 * @Date 2022/7/9 10:50
 **/
@RestController

public class SystemApi {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/contents")
    public JsonResponse<List<Map<String, Object>>> getContents(@RequestParam String keyword, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws IOException {
        List<Map<String, Object>> list = elasticSearchService.getContents(keyword, pageNo, pageSize);
        return new JsonResponse<>(list);
    }
}
