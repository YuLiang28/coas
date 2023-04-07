package org.example.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.service.SysUserService;
import org.example.common.result.Result;
import org.example.common.security.SecurityUtils;
import org.example.common.utils.MD5;
import org.example.model.system.SysUser;
import org.example.vo.system.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    // 注入Service
    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("查询所有用户")
    @GetMapping("findAll")
    public Result findAll(){
        List<SysUser> sysUsers = sysUserService.list();
        return Result.ok(sysUsers);
    }

    // page 当前页
    // limit 每页显示记录数
    // sysUserQueryVo 条件对象
    @ApiOperation("条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result pageQueryRole(@PathVariable Long page,
                                @PathVariable Long limit,
                                SysUserQueryVo sysUserQueryVo){

        Page<SysUser> pageParam = new Page<>(page, limit);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        if(!StringUtils.isEmpty(keyword)){
            wrapper.or(w->w.like(SysUser::getUsername,keyword))
                    .or(w->w.like(SysUser::getPhone,keyword))
                    .or(w->w.like(SysUser::getName,keyword));
        }
        // 大于等于
        if(!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        // 小于等于
        if(!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime,createTimeEnd);
        }
        IPage<SysUser> pageModel = sysUserService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user){

        // 对密码进行加密存储
        user.setPassword(SecurityUtils.encodePassword(user.getPassword()));

        boolean isSuccess = sysUserService.save(user);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation("根据 ID 查询用户")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody SysUser user){
        if(user.getPassword()!=null){
            // 对密码进行加密存储
            user.setPassword(SecurityUtils.encodePassword(user.getPassword()));
        }
        boolean isSuccess = sysUserService.updateById(user);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation("根据 ID 删除用户")
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Long id){
        boolean isSuccess = sysUserService.removeById(id);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation("批量删除")
    @DeleteMapping("batchDelete")
    public Result batchDelete(@RequestBody List<Long> ids){
        boolean isSuccess = sysUserService.removeByIds(ids);
        return isSuccess ? Result.ok() : Result.fail();
    }

    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }
}
