package org.example.auth.service.impl;

import org.example.auth.service.SysMenuService;
import org.example.auth.service.SysUserService;
import org.example.common.config.exception.OAException;
import org.example.model.system.SysUser;
import org.example.security.custom.CustomUser;
import org.example.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getUserByUsername(username);
        if (sysUser == null || sysUser.getStatus().intValue() == 0) {
            throw new OAException(201, "用户不存在或用户被禁用");
        }

        // 查询用户操作权限数据
        List<String> userPermsList = sysMenuService.findUserPermsListByUserId(sysUser.getId());

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();


        for (String perms : userPermsList) {
            authorityList.add(new SimpleGrantedAuthority(perms));
        }

        return new CustomUser(sysUser, authorityList);
    }
}

