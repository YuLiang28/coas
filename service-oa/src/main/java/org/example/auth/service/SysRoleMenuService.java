package org.example.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.system.SysMenu;
import org.example.model.system.SysRoleMenu;
import org.example.vo.system.AssginMenuVo;

import java.util.List;

/**
 * <p>
 * 角色菜单 服务类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-04
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void assignRoleMenu(AssginMenuVo assignMenuVo);
}
