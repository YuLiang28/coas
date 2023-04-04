package org.example.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.service.SysUserRoleService;
import org.example.common.result.Result;
import org.example.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "用户角色管理")
@RestController
@RequestMapping("/admin/system/sysUserRole")
public class SysUserRoleController {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/findRoleByUserId/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysUserRoleService.findRoleByUserId(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/assignUserRole")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysUserRoleService.assignUserRole(assginRoleVo);
        return Result.ok();
    }

}
