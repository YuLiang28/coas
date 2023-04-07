package org.example.auth.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.auth.mapper.SysRoleMenuMapper;
import org.example.auth.service.SysMenuService;
import org.example.auth.service.SysRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.auth.utils.MenuHelper;
import org.example.model.system.SysMenu;
import org.example.model.system.SysRoleMenu;
import org.example.vo.system.AssginMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色菜单 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-04
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {


    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //获取菜单状态为可用的列表
        List<SysMenu> allSysMenuList = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));

        //根据角色id，查询菜单id
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        //转换给角色id与角色权限对应Map对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());

        allSysMenuList.forEach(permission -> {
            if (menuIdList.contains(permission.getId())) {
                permission.setSelect(true);
            } else {
                permission.setSelect(false);
            }
        });

        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    @Override
    public void assignRoleMenu(AssginMenuVo assignMenuVo) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId()));

        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId)) continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuMapper.insert(rolePermission);
        }
    }
}
