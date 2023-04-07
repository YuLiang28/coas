package org.example.auth.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.mapper.SysRoleMenuMapper;
import org.example.auth.service.SysRoleMenuService;
import org.example.common.result.Result;
import org.example.model.system.SysMenu;
import org.example.vo.system.AssginMenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色菜单 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-04-04
 */
@Api(tags = "角色菜单管理")
@RestController
@RequestMapping("/admin/system/sysRoleMenu")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @ApiOperation(value = "根据角色 ID 获取菜单")
    @GetMapping("findMenuByRoleId/{roleId}")
    public Result findMenuByRoleId(@PathVariable Long roleId) {
        List<SysMenu> list = sysRoleMenuService.findSysMenuByRoleId(roleId);
        return Result.ok(list);
    }

    @ApiOperation(value = "给角色分配权限")
    @PostMapping("/assignRoleMenu")
    public Result assignRoleMenu(@RequestBody AssginMenuVo assignMenuVo) {
        sysRoleMenuService.assignRoleMenu(assignMenuVo);
        return Result.ok();
    }
}

