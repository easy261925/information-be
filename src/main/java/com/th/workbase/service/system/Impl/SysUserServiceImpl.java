package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.equipment.EquipmentMain;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysOrganizationDto;
import com.th.workbase.bean.system.SysRoleDto;
import com.th.workbase.bean.system.SysUserDto;
import com.th.workbase.common.system.ContentSystem;
import com.th.workbase.common.utils.DESUtil;
import com.th.workbase.common.utils.RedisUtil;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.config.JwtConfig;
import com.th.workbase.mapper.equipment.EquipmentMainMapper;
import com.th.workbase.mapper.system.SysUserMapper;
import com.th.workbase.service.system.SysOrganizationService;
import com.th.workbase.service.system.SysRoleRightService;
import com.th.workbase.service.system.SysUserRoleService;
import com.th.workbase.service.system.SysUserService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserDto> implements SysUserService {
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysUserRoleService userRoleService;
    @Resource
    private SysRoleRightService rightService;
    @Resource
    private JwtConfig jwtConfig;
    @Resource
    private SysOrganizationService organizationService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private Environment env;
    @Resource
    EquipmentMainMapper equipmentMainMapper;
    @Value("${serverAddress}")
    private String SERVER_ADDRESS;

    private final String DEFAULT_PASSWORD = "111111";

    /**
     * 用户登录接口
     *
     * @param request   请求
     * @param loginName 用户名
     * @param loginPass 密码
     * @return 返回JSON对象
     */
    @Override
    public ResponseResultDto login(HttpServletRequest request, String loginName, String loginPass, String equipmentNo, String versionNo) {
        // 用户名密码不可为空
        if (StringUtil.isNotNullOrEmpty(loginName) && StringUtil.isNotNullOrEmpty(loginPass)) {
            // 验证用户名密码正确性
            QueryWrapper<SysUserDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login_name", loginName);
            queryWrapper.eq("login_pass", DESUtil.encrypt(env.getProperty("local.pass.des.baseKey"), loginPass));
            SysUserDto resultUser = userMapper.selectOne(queryWrapper);
            if (resultUser != null) {
                // 刷新token 删除已存在token key结构为用户名_用户类型_机构id
                String tmp = redisUtil.get(loginName + "_" + resultUser.getUserType() + "_" + resultUser.getOrganizationId() + "_" + resultUser.getId(), String.class);
                String tmpToken = loginName + "_" + resultUser.getUserType() + "_" + resultUser.getOrganizationId() + "_" + resultUser.getId();
                // 删除redis中的token信息
                if (StringUtil.isNotNullOrEmpty(tmp)) {
                    redisUtil.del(tmpToken);
                    redisUtil.del(tmp);
                }
                // 生成新token 保存到redis中
                String token = jwtConfig.getToken(loginName + "_" + resultUser.getUserType() + "_" + loginPass + "_" + resultUser.getOrganizationId() + "_" + resultUser.getId());
                redisUtil.set(loginName + "_" + resultUser.getUserType() + "_" + resultUser.getOrganizationId() + "_" + resultUser.getId(), token);
                redisUtil.set(token, loginName + "_" + resultUser.getUserType() + "_" + resultUser.getOrganizationId() + "_" + resultUser.getId());
                // 刷新登录时间
                resultUser.setLoginDate(new Date());
                // 刷新登录地址
                resultUser.setLoginIp(StringUtil.getIpAddress(request));
                userMapper.updateById(resultUser);
                // 设置用户权限
                resultUser.setUserRoles(userRoleService.getUserRolesStr(resultUser.getLoginName(), resultUser.getUserType()));
                List<String> rightDtos = rightService.getRoleRights(resultUser.getUserRoles(), "1");
                List<String> authority = rightService.getRoleRights(resultUser.getUserRoles(), "2");
                return ResponseResultDto.ok().data("user", resultUser).data("right", rightDtos).data("authority", authority).data(jwtConfig.getHeader(), token);
            } else {
                return ResponseResultDto.loginError();
            }
        } else {
            return ResponseResultDto.loginError();
        }
    }

    /**
     * 登出
     *
     * @param request 请求
     * @return 返回JSON对象
     */
    @Override
    public ResponseResultDto logout(HttpServletRequest request) {
        try {
            // 获取token
            String token = request.getHeader(jwtConfig.getHeader());
            String tmp = redisUtil.get(token, String.class);
            // 注销票据
            redisUtil.del(token);
            redisUtil.del(tmp);
            return ResponseResultDto.ok();
        } catch (Exception e) {
            return ResponseResultDto.ServiceError("退出失败");
        }
    }

    /**
     * 新增用户
     *
     * @param request    请求
     * @param sysUserDto 用户对象实体类
     * @return 返回JSON对象
     */
    @Override
    public ResponseResultDto addUser(HttpServletRequest request, SysUserDto sysUserDto) {
        // 登录账号不可为空
        if (sysUserDto != null && StringUtil.isNotNullOrEmpty(sysUserDto.getLoginName())) {
            // 验证用户名是否已经存在
            QueryWrapper<SysUserDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login_name", sysUserDto.getLoginName());
            SysUserDto checkDto = userMapper.selectOne(queryWrapper);
            if (checkDto == null) {
                checkDto = new SysUserDto();
                // 新增用户信息
                sysUserDto.setLoginPass(DESUtil.encrypt(env.getProperty("local.pass.des.baseKey"), env.getProperty("local.init.pass")));
                userMapper.insert(sysUserDto);
                // 修改用户角色
                userRoleService.updateUserRole(sysUserDto.getLoginName(), sysUserDto.getUserType(), sysUserDto.getUserRoles());
                return ResponseResultDto.ok();
            } else {
                return ResponseResultDto.ServiceError("用户已存在");
            }
        } else {
            return ResponseResultDto.ServiceError("用户信息不完整");
        }
    }

    /**
     * 修改用户
     *
     * @param request    请求
     * @param sysUserDto 用户对象实体类
     * @return 返回JSON对象
     */
    @Override
    public ResponseResultDto updateUser(HttpServletRequest request, SysUserDto sysUserDto) {
        // 修改用户信息
        userMapper.updateById(sysUserDto);
        // 修改用户角色
        userRoleService.updateUserRole(sysUserDto.getLoginName(), sysUserDto.getUserType(), sysUserDto.getUserRoles());
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto getUserByPage(HttpServletRequest request, SysUserDto sysUserDto, int current, int pageSize) {
        SysUserDto userDto = getUserByLoginToken(request);
        QueryWrapper<SysUserDto> queryWrapper = new QueryWrapper<>();
        boolean b = checkUserIsAdmin(userDto.getLoginName(), userDto.getUserType());

        if (StringUtil.isNotNullOrEmpty(sysUserDto.getUserType())) {
            queryWrapper.eq("user_type", sysUserDto.getUserType());
        }

        if (StringUtil.isNotNullOrEmpty(sysUserDto.getLoginName())) {
            queryWrapper.eq("login_name", sysUserDto.getLoginName());
        }
        if (StringUtil.isNotNullOrEmpty(sysUserDto.getUserName())) {
            queryWrapper.like("user_name", sysUserDto.getUserName());
        }
        Page<SysUserDto> page = new Page<>(current, pageSize);
        if (sysUserDto.getIsUse() != null) {
            queryWrapper.eq("is_use", 1);
        }
        IPage<SysUserDto> results = userMapper.selectPage(page, queryWrapper);
        for (SysUserDto tmpDto : results.getRecords()) {
            SysOrganizationDto organizationDto = organizationService.getById(tmpDto.getOrganizationId());
            List<SysOrganizationDto> organizationDtos = new ArrayList<>();
            if (organizationDto != null) {
                organizationDtos.add(organizationDto);
            }
            List<Integer> list = new ArrayList<>();
            if (tmpDto.getOrganizationId() != null) {
                list.add(tmpDto.getOrganizationId());
            }
            tmpDto.setOrganizationNo(organizationDto.getOrganizationNo());
            tmpDto.setOrganizationName(organizationDto.getOrganizationName());
            tmpDto.setOrganizationDtos(organizationDtos);
            tmpDto.setOrganizationIds(list);
            tmpDto.setRoleDtos(userRoleService.getUserRoles(tmpDto.getLoginName(), tmpDto.getUserType()));
            tmpDto.setUserRoles(userRoleService.getUserRolesStr(tmpDto.getLoginName(), tmpDto.getUserType()));
        }
        return ResponseResultDto.ok().data("data", results.getRecords()).data("total", results.getTotal());
    }

    @Override
    public Boolean checkUserIsAdmin(String loginName, String userType) {
        Boolean result = false;
        try {
            List<SysRoleDto> userRoleDtos = userRoleService.getUserRoles(loginName, userType);
            if (userRoleDtos != null && userRoleDtos.size() > 0) {
                for (SysRoleDto roleDto : userRoleDtos) {
                    if (ContentSystem.SYSTEMROLE.equals(roleDto.getRoleNo())) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ResponseResultDto getUserInfo(HttpServletRequest request) {
        String token = request.getHeader(jwtConfig.getHeader());
        if (StringUtil.isNotNullOrEmpty(token)) {
            Claims claims = jwtConfig.getTokenClaim(token);
            String arg = claims.getSubject();
            String[] argArray = arg.split("_");
            QueryWrapper<SysUserDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login_name", argArray[0]);
            SysUserDto userDto = userMapper.selectOne(queryWrapper);
            if (userDto != null) {
                userDto.setUserRoles(userRoleService.getUserRolesStr(userDto.getLoginName(), userDto.getUserType()));
                List<String> rightDtos = rightService.getRoleRights(userDto.getUserRoles(), "1");
                List<String> authority = rightService.getRoleRights(userDto.getUserRoles(), "2");
                return ResponseResultDto.ok().data("user", userDto)
                        .data("right", rightDtos)
                        .data("authority", authority)
                        .data("serverAddress", SERVER_ADDRESS);
            } else {
                return ResponseResultDto.ServiceError("用户记录不存在");
            }
        } else {
            return ResponseResultDto.ServiceError("用户信息不完整");
        }
    }

    @Override
    public ResponseResultDto deleteUser(Integer id) {
        if (id != null) {
            SysUserDto checkDto = userMapper.selectById(id);
            if (checkDto != null) {
                userMapper.deleteById(id);
                userRoleService.deleteUserRole(checkDto.getLoginName(), checkDto.getUserType());
                return ResponseResultDto.ok();
            } else {
                return ResponseResultDto.ServiceError("用户不存在");
            }
        } else {
            return ResponseResultDto.ServiceError("用户信息不完整");
        }
    }

    @Override
    public ResponseResultDto changePassword(SysUserDto sysUserDto) {
        String originalLoginPass = sysUserDto.getOriginalLoginPass();//原始密码
        String encryptedPassword = DESUtil.encrypt(env.getProperty("local.pass.des.baseKey"), originalLoginPass);
        Integer id = sysUserDto.getId();
        SysUserDto user = userMapper.selectById(id);
        if (user == null) {
            return ResponseResultDto.ServiceError("当前用户不存在");
        }
        if (!user.getLoginPass().equals(encryptedPassword)) {
            return ResponseResultDto.ServiceError("原始密码不正确");
        }
        String newPassword = DESUtil.encrypt(env.getProperty("local.pass.des.baseKey"), sysUserDto.getLoginPass());
        user.setLoginPass(newPassword);
        userMapper.updateById(user);
        return ResponseResultDto.ok();
    }

    @Override
    public ResponseResultDto resetPassword(String id) {
        SysUserDto sysUserDto = userMapper.selectById(id);
        if (sysUserDto == null) {
            return ResponseResultDto.ServiceError("当前用户不存在");
        }
        String encryptedPassword = DESUtil.encrypt(env.getProperty("local.pass.des.baseKey"), DEFAULT_PASSWORD);
        sysUserDto.setLoginPass(encryptedPassword);
        userMapper.updateById(sysUserDto);
        return ResponseResultDto.ok();
    }

    @Override
    public SysUserDto getUserByLoginToken(HttpServletRequest request) {
        SysUserDto sysUserDto = new SysUserDto();
        try {
            String token = request.getHeader(jwtConfig.getHeader());
            if (StringUtil.isNotNullOrEmpty(token)) {
                Claims claims = jwtConfig.getTokenClaim(token);
                String arg = claims.getSubject();
                String[] argArray = arg.split("_");
                sysUserDto.setLoginName(argArray[0]);
                sysUserDto.setUserType(argArray[1]);
                sysUserDto.setOrganizationId(Integer.parseInt(argArray[3]));
                sysUserDto.setId(Integer.parseInt(argArray[4]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sysUserDto;
    }
}
