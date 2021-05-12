package com.th.workbase.config;

import com.th.workbase.Exception.TokenException;
import com.th.workbase.common.utils.RedisUtil;
import com.th.workbase.common.utils.StringUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class TokenInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private JwtConfig jwtConfig;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 地址过滤
        String uri = request.getRequestURI();
        if (uri.contains("/files/connect")) {
            return true;
        }

        if (uri.contains("/files/create")) {
            return true;
        }
        if (uri.contains("/static")) {
            return true;
        }
        //测速过滤
        if (uri.contains("/checkSpeed")) {
            return true;
        }
        //设备id换取设备详情
        if (uri.contains("/equipmentByDeviceId")) {
            return true;
        }
        //获取系统配置信息
        if (request.getMethod().toLowerCase().equals("get")
                && uri.contains("/sysConfig")) {
            return true;
        }
        //绑定设备操作人
        if (request.getMethod().toLowerCase().equals("put")
                && uri.contains("/equipment/equipment")) {
            return true;
        }
        //检查App更新
        if (uri.contains("/checkUpdate")) {
            return true;
        }

        if (uri.contains("/es")) {
            return true;
        }
        // 登录
        if (uri.contains("/login")) {
            return true;
        }
        if (uri.contains("/logout")) {
            return true;
        }
        if (uri.contains("swagger")) {
            return true;
        }
        if (StringUtil.isEmpty(uri) || "/".equals(uri)) {
            return true;
        }
        if (uri.contains("/error")) {
            return true;
        }
        if (uri.contains("/csrf") || uri.contains("/robots") || uri.contains("/favicon.ico")) {
            return true;
        }
        // Token 验证
        String token = request.getHeader(jwtConfig.getHeader());
        if (StringUtil.isEmpty(token)) {
            token = request.getParameter(jwtConfig.getHeader());
        }
        if (StringUtil.isEmpty(token)) {
            System.out.println(uri);
            throw new TokenException(jwtConfig.getHeader() + "不能为空");
        }
        if (!redisUtil.hasKey(token)) {
            Claims claims = jwtConfig.getTokenClaim(token);
            if (claims != null) {
                String info = claims.getSubject();
                if (StringUtil.isNotNullOrEmpty(info) && info.indexOf("_") > 0) {
                    String[] array = info.split("_");
                    String tmp = array[0] + "_" + array[1];
                    redisUtil.del(tmp);
                }
            }
            throw new TokenException(jwtConfig.getHeader() + "失效，请重新登录");
        }
        Claims claims = jwtConfig.getTokenClaim(token);
        if (claims == null) {
            String tmp = redisUtil.get(token, String.class);
            redisUtil.del(tmp);
            redisUtil.del(token);
            throw new TokenException(jwtConfig.getHeader() + "失效，请重新登录");
        }
        //设置 identityId 用户身份ID
        request.setAttribute("identityId", claims.getSubject());
        return true;
    }
}
