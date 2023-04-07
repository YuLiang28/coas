package org.example.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.auth.mapper.SysRoleMapper;
import org.example.auth.mapper.SysUserRoleMapper;
import org.example.auth.service.SysRoleService;
import org.example.auth.service.SysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.model.system.SysRole;
import org.example.model.system.SysUserRole;
import org.example.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户角色 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-03
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {


    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleService sysRoleService;
    // 根据用户id查找该用户角色
    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {
        //查询所有的角色
        List<SysRole> allRolesList = sysRoleService.list();

        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        List<SysUserRole> existUserRoleList = sysUserRoleMapper.selectList(wrapper.eq(SysUserRole::getUserId, userId).select(SysUserRole::getRoleId));
        List<Long> existRoleIdList = existUserRoleList.stream().map(c->c.getRoleId()).collect(Collectors.toList());

        //对角色进行分类
        List<SysRole> assginRoleList = new ArrayList<>();
        for (SysRole role : allRolesList) {
            //已分配
            if(existRoleIdList.contains(role.getId())) {
                assginRoleList.add(role);
            }
        }
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assginRoleList);
        roleMap.put("allRolesList", allRolesList);
        return roleMap;
    }
    //修改用户
    @Override
    public void assignUserRole(AssginRoleVo assginRoleVo) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        sysUserRoleMapper.delete(wrapper.eq(SysUserRole::getUserId, assginRoleVo.getUserId()));
        for(Long roleId : assginRoleVo.getRoleIdList()) {
            if(StringUtils.isEmpty(roleId)) continue;
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }

    @Override
    public boolean isAdminByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRole::getUserId,userId);
        SysUserRole sysUserRole = sysUserRoleMapper.selectOne(userRoleWrapper);
        LambdaQueryWrapper<SysRole> roleWarpper = new LambdaQueryWrapper<>();
        roleWarpper.eq(SysRole::getId,sysUserRole.getRoleId());
        SysRole role = sysRoleMapper.selectOne(roleWarpper);
        return role.getRoleCode()=="SYSTEM"? true : false;
    }
}
