package com.th.workbase.service.system.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.th.workbase.bean.system.ResponseResultDto;
import com.th.workbase.bean.system.SysOrganizationDto;
import com.th.workbase.bean.system.SysOrganizationTreeDto;
import com.th.workbase.bean.system.SysUserDto;
import com.th.workbase.common.utils.StringUtil;
import com.th.workbase.mapper.system.SysOrganizationMapper;
import com.th.workbase.service.system.SysOrganizationService;
import com.th.workbase.service.system.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hut
 * @since 2020-09-29
 */
@Service
public class SysOrganizationServiceImpl extends ServiceImpl<SysOrganizationMapper, SysOrganizationDto> implements SysOrganizationService {
    @Resource
    private SysOrganizationMapper sysOrganizationMapper;
    @Resource
    private SysUserService userService;

    @Override
    public ResponseResultDto addSysOrganization(HttpServletRequest request, SysOrganizationDto sysOrganizationDto) {
        QueryWrapper<SysOrganizationDto> queryWrapper = new QueryWrapper<>();
        // 如果机构编号为空则默认用UUID
        if (StringUtil.isNullOrEmpty(sysOrganizationDto.getOrganizationNo())) {
            sysOrganizationDto.setOrganizationNo(UUID.randomUUID().toString());
        }
        queryWrapper.eq("organization_no", sysOrganizationDto.getOrganizationNo());
        SysOrganizationDto checkDto = sysOrganizationMapper.selectOne(queryWrapper);
        if (checkDto == null) {
            Integer up = sysOrganizationDto.getUpId();
            if (up != null && up != 0) {
                SysOrganizationDto upOrganizationDto = sysOrganizationMapper.selectById(up);
                if (upOrganizationDto != null) {
                    sysOrganizationDto.setUpOrganizationNo(upOrganizationDto.getOrganizationNo());
                    getOrganizationUpByLvl(upOrganizationDto, sysOrganizationDto, up);
                    if (StringUtil.isNotNullOrEmpty(sysOrganizationDto.getOrganizationType())) {
                        if (!upOrganizationDto.getOrganizationType().equals(sysOrganizationDto.getOrganizationType())) {
                            return ResponseResultDto.ServiceError("机构类型需要与上级机构类型保持一致");
                        }
                    } else {
                        sysOrganizationDto.setOrganizationType(upOrganizationDto.getOrganizationType());
                    }
                }
            } else {
                sysOrganizationDto.setOrganizationLvl(0);
                sysOrganizationDto.setUpLvl0(sysOrganizationDto.getOrganizationNo());
            }
            sysOrganizationMapper.insert(sysOrganizationDto);
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("机构已存在");
        }
    }

    @Override
    public ResponseResultDto updateSysOrganization(HttpServletRequest request, SysOrganizationDto sysOrganizationDto) {
        QueryWrapper<SysOrganizationDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("organization_no", sysOrganizationDto.getOrganizationNo());
        SysOrganizationDto checkDto = sysOrganizationMapper.selectOne(queryWrapper);
        if (checkDto != null) {
            if (checkDto.getId() == sysOrganizationDto.getId()) {
                // 是否修改上级机构id
                if (sysOrganizationDto.getUpId() != null && checkDto.getUpId() != sysOrganizationDto.getUpId()) {
                    List<SysOrganizationDto> children = checkIsEnd(sysOrganizationDto);
                    if (children != null && children.size() > 0) {
                        return ResponseResultDto.ServiceError("该机构存在子机构，不可修改起上级机构");
                    } else {
                        Integer up = checkDto.getUpId();
                        if (up != null && up != 0) {
                            SysOrganizationDto upOrganizationDto = sysOrganizationMapper.selectById(up);
                            if (upOrganizationDto != null) {
                                sysOrganizationDto.setUpOrganizationNo(upOrganizationDto.getOrganizationNo());
                                if (StringUtil.isNotNullOrEmpty(sysOrganizationDto.getOrganizationType())) {
                                    if (!upOrganizationDto.getOrganizationType().equals(sysOrganizationDto.getOrganizationType())) {
                                        return ResponseResultDto.ServiceError("机构类型需要与上级机构类型保持一致");
                                    }
                                } else {
                                    sysOrganizationDto.setOrganizationType(upOrganizationDto.getOrganizationType());
                                }
                            }
                        }
                    }
                }
                sysOrganizationMapper.updateById(sysOrganizationDto);
                return ResponseResultDto.ok();
            } else {
                return ResponseResultDto.ServiceError("机构已存在");
            }
        } else {
            sysOrganizationMapper.updateById(sysOrganizationDto);
            return ResponseResultDto.ok();
        }
    }

    private List<SysOrganizationDto> checkIsEnd(SysOrganizationDto organizationDto) {
        if (organizationDto != null) {
            QueryWrapper<SysOrganizationDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("up_id", organizationDto.getId());
            List<SysOrganizationDto> reList = sysOrganizationMapper.selectList(queryWrapper);
            if (reList != null && reList.size() > 0) {
                return reList;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 递归查询所有子阶段并赋予属性结构
     */
    private void getAllChildren(List<SysOrganizationDto> sysOrganizationDtos) {
        if (sysOrganizationDtos != null && sysOrganizationDtos.size() > 0) {
            for (SysOrganizationDto arg : sysOrganizationDtos) {
                List<SysOrganizationDto> tmpList = checkIsEnd(arg);
                if (tmpList != null) {
                    arg.setChildren(tmpList);
                    getAllChildren(tmpList);
                }
            }
        }
    }

    private void getAllChildrenIds(Integer organizationId, List<Integer> lists) {
        QueryWrapper<SysOrganizationDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("up_id", organizationId);
        List<SysOrganizationDto> reList = sysOrganizationMapper.selectList(queryWrapper);
        if (reList != null && reList.size() > 0) {
            for (SysOrganizationDto tmp : reList) {
                lists.add(tmp.getId());
                getAllChildrenIds(tmp.getId(), lists);
            }
        } else {
            lists.add(organizationId);
        }
    }

    @Override
    public ResponseResultDto getTreeOrganization(HttpServletRequest request, SysOrganizationDto sysOrganizationDto) {
        QueryWrapper<SysOrganizationDto> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("up_id", 0);
        List<SysOrganizationDto> sysOrganizations = sysOrganizationMapper.selectList(queryWrapper);
        List<SysOrganizationTreeDto> showLists = new ArrayList<>();
        if (sysOrganizations != null && sysOrganizations.size() > 0) {
            for (SysOrganizationDto arg : sysOrganizations) {
                SysOrganizationTreeDto tmp = new SysOrganizationTreeDto();
                tmp.setId(arg.getId());
                tmp.setPId(arg.getUpId());
                tmp.setTitle(arg.getOrganizationName());
                tmp.setValue(arg.getOrganizationNo());
                showLists.add(tmp);
            }
        }
        return ResponseResultDto.ok().data("data", showLists);
    }

    @Override
    public ResponseResultDto getSysOrganizationByPage(HttpServletRequest request, SysOrganizationDto sysOrganizationDto, int current, int pageSize) {
        SysUserDto userDto = userService.getUserByLoginToken(request);
        boolean b = userService.checkUserIsAdmin(userDto.getLoginName(), userDto.getUserType());
        QueryWrapper<SysOrganizationDto> queryWrapper = new QueryWrapper<>();
        if (!b) {
            SysOrganizationDto organizationDto = sysOrganizationMapper.selectById(userDto.getOrganizationId());
            if (organizationDto != null) {
                queryWrapper.eq("up_lvl" + organizationDto.getOrganizationLvl(), organizationDto.getOrganizationNo());
            } else {
                return ResponseResultDto.ServiceError("当前用户无任何机构查看权限");
            }
        }
        if (StringUtil.isNotNullOrEmpty(sysOrganizationDto.getOrganizationNo())) {
            queryWrapper.eq("organization_no", sysOrganizationDto.getOrganizationNo());
        }
        if (StringUtil.isNotNullOrEmpty(sysOrganizationDto.getOrganizationName())) {
            queryWrapper.like("organization_name", sysOrganizationDto.getOrganizationName());
        }
        if (StringUtil.isNotNullOrEmpty(sysOrganizationDto.getUpOrganizationNo())) {
            queryWrapper.eq("up_organization_no", sysOrganizationDto.getUpOrganizationNo());
        }
        Page<SysOrganizationDto> page = new Page<>(current, pageSize);
        if (sysOrganizationDto.getIsUse() != null) {
            queryWrapper.eq("is_use", 1);
        }
        IPage<SysOrganizationDto> results = sysOrganizationMapper.selectPage(page, queryWrapper);
        return ResponseResultDto.ok().data("data", results.getRecords()).data("total", results.getTotal());
    }

    @Override
    public List<Integer> getSysOrganizationIdsByLoginName(String loginName, String userType) {
        List<Integer> resultList = new ArrayList<>();
        try {
            QueryWrapper<SysUserDto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("login_name", loginName);
            if (StringUtil.isNotNullOrEmpty(userType) && !"null".equals(userType)) {
                queryWrapper.eq("user_type", userType);
            }
            SysUserDto userDto = userService.getOne(queryWrapper);
            if (userDto != null) {
                int organizationId = userDto.getOrganizationId();
                getAllChildrenIds(organizationId, resultList);
                if (!resultList.contains(organizationId)) {
                    resultList.add(organizationId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    @Override
    public ResponseResultDto deleteSysOrganization(Integer id) {
        if (id != null) {
            sysOrganizationMapper.deleteById(id);
            return ResponseResultDto.ok();
        } else {
            return ResponseResultDto.ServiceError("机构Id不可为空");
        }
    }

    private void getOrganizationUpByLvl(SysOrganizationDto upOrganizationDto, SysOrganizationDto sysOrganizationDto, Integer up) {
        Integer lvl = upOrganizationDto.getOrganizationLvl();
        if (lvl == null || lvl == 0) {
            sysOrganizationDto.setUpLvl0(upOrganizationDto.getOrganizationNo());
            sysOrganizationDto.setUpLvl1(sysOrganizationDto.getOrganizationNo());
        } else if (lvl == 1) {
            // lvl 1
            int upId = upOrganizationDto.getUpId();
            sysOrganizationDto.setUpLvl2(sysOrganizationDto.getOrganizationNo());
            sysOrganizationDto.setUpLvl1(upOrganizationDto.getOrganizationNo());
            // lvl 0
            SysOrganizationDto upLv0Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl0(upLv0Dto.getOrganizationNo());
        } else if (lvl == 2) {
            // lvl 2
            int upId = upOrganizationDto.getUpId();
            sysOrganizationDto.setUpLvl2(upOrganizationDto.getOrganizationNo());
            sysOrganizationDto.setUpLvl3(sysOrganizationDto.getOrganizationNo());
            // lvl 1
            SysOrganizationDto upLv1Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl1(upLv1Dto.getOrganizationNo());
            // lvl 0
            upId = upLv1Dto.getUpId();
            SysOrganizationDto upLv0Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl0(upLv0Dto.getOrganizationNo());
        } else if (lvl == 3) {
            // lvl 3
            int upId = upOrganizationDto.getUpId();
            sysOrganizationDto.setUpLvl3(upOrganizationDto.getOrganizationNo());
            // lvl 2
            SysOrganizationDto upLv2Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl2(upLv2Dto.getOrganizationNo());
            // lvl 1
            upId = upLv2Dto.getUpId();
            SysOrganizationDto upLv1Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl1(upLv1Dto.getOrganizationNo());
            // lvl 0
            upId = upLv1Dto.getUpId();
            SysOrganizationDto upLv0Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl0(upLv0Dto.getOrganizationNo());
        } else if (lvl == 4) {
            // lvl 4
            int upId = upOrganizationDto.getUpId();
            sysOrganizationDto.setUpLvl4(upOrganizationDto.getOrganizationNo());
            // lvl 3
            upId = upOrganizationDto.getUpId();
            SysOrganizationDto upLv3Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl3(upLv3Dto.getOrganizationNo());
            // lvl 2
            upId = upLv3Dto.getUpId();
            SysOrganizationDto upLv2Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl2(upLv2Dto.getOrganizationNo());
            // lvl 1
            upId = upLv2Dto.getUpId();
            SysOrganizationDto upLv1Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl1(upLv1Dto.getOrganizationNo());
            // lvl 0
            upId = upLv1Dto.getUpId();
            SysOrganizationDto upLv0Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl0(upLv0Dto.getOrganizationNo());
        } else if (lvl == 5) {
            // lvl 5
            int upId = upOrganizationDto.getUpId();
            sysOrganizationDto.setUpLvl5(upOrganizationDto.getOrganizationNo());
            // lvl 4
            upId = upOrganizationDto.getUpId();
            SysOrganizationDto upLv4Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl4(upLv4Dto.getOrganizationNo());
            // lvl 3
            upId = upLv4Dto.getUpId();
            SysOrganizationDto upLv3Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl3(upLv3Dto.getOrganizationNo());
            // lvl 2
            upId = upLv3Dto.getUpId();
            SysOrganizationDto upLv2Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl2(upLv2Dto.getOrganizationNo());
            // lvl 1
            upId = upLv2Dto.getUpId();
            SysOrganizationDto upLv1Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl1(upLv1Dto.getOrganizationNo());
            // lvl 0
            upId = upLv1Dto.getUpId();
            SysOrganizationDto upLv0Dto = sysOrganizationMapper.selectById(upId);
            sysOrganizationDto.setUpLvl0(upLv0Dto.getOrganizationNo());
        }
        sysOrganizationDto.setOrganizationLvl(lvl + 1);
    }
}
