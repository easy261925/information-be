package com.th.workbase.common.aop;

import com.th.workbase.bean.system.SysLogDto;
import com.th.workbase.bean.system.SysLoginDto;
import com.th.workbase.bean.system.SysUserDto;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.config.annotation.InLogAnnotation;
import com.th.workbase.mapper.system.SysLogMapper;
import com.th.workbase.service.system.SysUserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;

/**
 * AOP实现日志
 *
 * @author 最后的轻语_dd43
 */
@Order(3)
@Component
@Aspect
public class LogAopAspect {
    // 日志mapper，这里省事少写了service
    @Resource
    private SysLogMapper sysLogMapper;
    @Resource
    private SysUserService userService;

    /**
     * 环绕通知记录日志通过注解匹配到需要增加日志功能的方法
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.th.workbase.config.annotation.InLogAnnotation)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        // 1.方法执行前的处理，相当于前置通知
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        // 获取方法
        Method method = methodSignature.getMethod();
        // 获取方法上面的注解
        InLogAnnotation logAnno = method.getAnnotation(InLogAnnotation.class);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        // 创建一个日志对象(准备记录日志)
        SysLogDto sysLogDto = new SysLogDto();
        sysLogDto.setModeType(logAnno.type());
        sysLogDto.setModeName(logAnno.name());
        Object[] args = pjp.getArgs();
        if (method.getName().startsWith("add")) {
            sysLogDto.setInfo("新增记录:" + args[0].toString());// 操作说明
        } else if (method.getName().startsWith("update")) {
            sysLogDto.setInfo("修改记录id:" + args[1].toString() + "|修改记录内容:" + args[0].toString());// 操作说明
        } else if (method.getName().startsWith("delete")) {
            sysLogDto.setInfo("删除记录id:" + args[0].toString());// 操作说明
        } else if (method.getName().startsWith("login")) {
            sysLogDto.setInfo("安全登录");// 操作说明
        } else if (method.getName().startsWith("logout")) {
            sysLogDto.setInfo("安全登出");// 操作说明
        }
        // 设置操作人，从session中获取，这里简化了一下，写死了。

        if (method.getName().startsWith("login")) {
            try {
                SysLoginDto dto = (SysLoginDto) args[0];
                if (dto != null) {
                    sysLogDto.setOperUser(dto.getUserName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SysUserDto userDto = userService.getUserByLoginToken(request);
            sysLogDto.setOperUser(userDto.getLoginName());
        }
        String ip = StringUtil.getIpAddress(request);
        sysLogDto.setOperIp(ip);// 操作说明
        Object result = null;
        try {
            // 让代理方法执行
            result = pjp.proceed();
            // 2.相当于后置通知(方法成功执行之后走这里)
        } catch (SQLException e) {
            // 3.相当于异常通知部分
        } finally {
            // 4.相当于最终通知
            sysLogDto.setDtCreaDateTime(new Date());// 设置操作日期
            sysLogMapper.insert(sysLogDto);// 添加日志记录
        }
        return result;
    }

}
