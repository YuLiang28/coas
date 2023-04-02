package org.example.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.service.SysRoleService;
import org.example.common.result.Result;
import org.example.model.system.SysRole;
import org.example.vo.system.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    // 注入Service
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("查询所有角色")
    @GetMapping("findAll")
    public Result findAll(){
        List<SysRole> sysRoles = sysRoleService.list();
        return Result.ok(sysRoles);
    }

    // page 当前页
    // limit 每页显示记录数
    // sysRoleQueryVo 条件对象
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysRoleQueryVo sysRoleQueryVo){

        Page<SysRole> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName,roleName);
        }

        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role){
        boolean isSuccess = sysRoleService.save(role);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation("根据 ID 查询角色")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        boolean isSuccess = sysRoleService.updateById(role);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation("根据 ID 删除角色")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Long id){
        boolean isSuccess = sysRoleService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation("批量删除")
    @DeleteMapping("batchDelete")
    public Result batchDelete(@RequestBody List<Long> ids){
        boolean isSuccess = sysRoleService.removeByIds(ids);
        return isSuccess ? Result.ok() : Result.fail();
    }
}
