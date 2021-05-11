package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysRoleRightDto;
import com.th.workbase.mapper.system.SysRoleRightMapper;
import com.th.workbase.service.system.SysRoleRightService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class SysRoleRightServiceImpl extends ServiceImpl<SysRoleRightMapper, SysRoleRightDto> implements SysRoleRightService {
    @Resource
    private SysRoleRightMapper rightMapper;

    @Override
    public ResponseResultDto updateRoleRight(Integer role, List<String> rights, List<String> others) {
        QueryWrapper<SysRoleRightDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", role);
        rightMapper.delete(queryWrapper);
        if (rights != null && rights.size() > 0) {
            for (String arg : rights) {
                SysRoleRightDto addDto = new SysRoleRightDto();
                addDto.setRoleId(role);
                addDto.setMenuNo(arg);
                addDto.setHandleType("1");
                rightMapper.insert(addDto);
            }
        }
        if (others != null && others.size() > 0) {
            for (String arg : others) {
                SysRoleRightDto addDto = new SysRoleRightDto();
                addDto.setRoleId(role);
                addDto.setMenuNo(arg);
                addDto.setHandleType("2");
                rightMapper.insert(addDto);
            }
        }
        return ResponseResultDto.ok();
    }

    @Override
    public List<String> getRoleRight(Integer role, String handleType) {
        List<String> reList = new ArrayList<>();
        try {
            QueryWrapper<SysRoleRightDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", role);
            queryWrapper.eq("handle_type", handleType);
            List<SysRoleRightDto> rightDtos = rightMapper.selectList(queryWrapper);
            if (rightDtos != null && rightDtos.size() > 0) {
                for (SysRoleRightDto arg : rightDtos) {
                    reList.add(arg.getMenuNo());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reList = new ArrayList<>();
        }
        return reList;
    }

    @Override
    public List<String> getRoleRights(List<Integer> roles, String handleType) {
        List<String> reList = new ArrayList<>();
        try {
            if (roles != null && roles.size() > 0) {
                for (int arg : roles) {
                    QueryWrapper<SysRoleRightDto> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("role_id", arg);
                    queryWrapper.eq("handle_type", handleType);
                    List<SysRoleRightDto> rightDtos = rightMapper.selectList(queryWrapper);
                    if (rightDtos != null && rightDtos.size() > 0) {
                        for (SysRoleRightDto roleRightDto : rightDtos) {
                            reList.add(roleRightDto.getMenuNo());
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            reList = new ArrayList<>();
        }
        return reList;
    }

    @Override
    public ResponseResultDto deleteRoleRight(Integer roleId) {
        QueryWrapper<SysRoleRightDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        rightMapper.delete(queryWrapper);
        return ResponseResultDto.ok();
    }
}
