package com.th.workbase.Exception;

import com.alibaba.fastjson.JSONObject;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.common.system.ErrorEnum;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author hut
 * @Description 统一拦截异常
 * @Date 2019/3/18 11:15
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseResultDto defaultErrorHandler(HttpServletRequest req, ServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        System.out.println("===============error=================" + e.toString());
        e.printStackTrace();
        return ResponseResultDto.error();
    }

    @ExceptionHandler(value = TokenException.class)
    public ResponseResultDto TokenException(HttpServletRequest req, ServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        System.out.println("===============E_401=================" + e.toString());
        return ResponseResultDto.loginError();
    }

    /**
     * GET/POST请求方法错误的拦截器
     * 因为开发时可能比较常见,而且发生在进入controller之前,上面的拦截器拦截不到这个错误
     * 所以定义了这个拦截器
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResultDto httpRequestMethodHandler(HttpServletRequest req, ServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        return ResponseResultDto.methodError();
    }

}
