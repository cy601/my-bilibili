package com.TanNgee.bilibili.service.handler;

import com.TanNgee.bilibili.domain.JsonResponse;
import com.TanNgee.bilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author TanNgee
 * @Date 2022/6/22 21:44
 * 全局异常处理器
 **/
@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)  // 这个Controller的优先级是最高的
public class CommonGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)  //对指定异常的类型进行处理
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e) {
        String errMsg = e.getMessage();
        if (e instanceof ConditionException) {
            String errorCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errorCode, errMsg);
        } else {
            return new JsonResponse<>("500", errMsg);
        }

    }

}
