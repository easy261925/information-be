package com.th.workbase.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysUserDto;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
public interface SysUserService extends IService<SysUserDto> {
    /**
     * 用户登录接口
     *
     * @param request   请求
     * @param loginName 用户名
     * @param loginPass 密码
     * @return 返回JSON对象
     */
    ResponseResultDto login(HttpServletRequest request, String loginName, String loginPass, String equipmentNo, String versionNo);

    /**
     * 登出
     *
     * @param request 请求
     * @return 返回JSON对象
     */
    ResponseResultDto logout(HttpServletRequest request);

    /**
     * 新增用户
     *
     * @param request    请求
     * @param sysUserDto 用户对象实体类
     * @return 返回JSON对象
     */
    ResponseResultDto addUser(HttpServletRequest request, SysUserDto sysUserDto);

    /**
     * 修改用户
     *
     * @param request    请求
     * @param sysUserDto 用户对象实体类
     * @return 返回JSON对象
     */
    ResponseResultDto updateUser(HttpServletRequest request, SysUserDto sysUserDto);

    /**
     * 获得当前登录用户信息
     *
     * @param request 请求
     * @return 返回JSON对象
     */
    ResponseResultDto getUserInfo(HttpServletRequest request);

    /**
     * 获得token中所包含的用户信息
     *
     * @param request
     * @return SysUserDto token解析user对象
     */
    SysUserDto getUserByLoginToken(HttpServletRequest request);

    /**
     * 验证用户是否是管理员
     *
     * @param loginName 用户名
     * @param userType  用户类型
     * @return true: 超级管理员
     */
    Boolean checkUserIsAdmin(String loginName, String userType);

    /**
     * 获取人员列表分页查询
     *
     * @param request    请求
     * @param sysUserDto 查询条件
     * @param current    第多少页
     * @param pageSize   一页查询多少数据
     * @return 返回json对象
     */
    ResponseResultDto getUserByPage(HttpServletRequest request, SysUserDto sysUserDto, int current, int pageSize);

    /**
     * 删除用户
     *
     * @param id 数据id
     * @return 返回json对象
     */
    ResponseResultDto deleteUser(Integer id);

    ResponseResultDto changePassword(SysUserDto sysUserDto);

    ResponseResultDto resetPassword(String id);
}
