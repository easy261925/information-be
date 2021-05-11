package com.th.workbase.config;

import com.th.workbase.Exception.TokenException;
import com.th.workbase.common.utils.RedisUtil;
import com.th.workbase.common.utils.StringUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author cc
 * @date 2021-01-29-上午10:57
 */
@Component
public class StaticInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        return true;
    }
}
