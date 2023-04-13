package org.example.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.auth.service.SysMenuService;
import org.example.auth.service.SysUserRoleService;
import org.example.auth.service.SysUserService;
import org.example.common.config.exception.OAException;
import org.example.common.jwt.JwtHelper;
import org.example.common.result.Result;
import org.example.common.security.SecurityUtils;
import org.example.model.system.SysUser;
import org.example.vo.system.LoginVo;
import org.example.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags="后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @ApiOperation("登录")
    @PostMapping("login")
    public Result login(@RequestBody  LoginVo loginVo){
        // 获取输入用户名与密码
        String usernameIn = loginVo.getUsername();
        String passwordIn = loginVo.getPassword();
        // 根据用户信息查询数据库
        SysUser sysUser = sysUserService.getUserByUsername(usernameIn);

        String passwordDb = sysUser.getPassword(); // 数据库中的密码
        boolean matchesPassword = SecurityUtils.matchesPassword(passwordIn, passwordDb);
        Integer status = sysUser.getStatus(); // 用户状态，1可用，0禁用
        if(sysUser == null || !matchesPassword || status.intValue()==0)
        {
            throw new OAException(201,"用户不存在、密码错误或用户被禁用");
        }

        // 使用JWT生成token
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());

        Map<String, Object> map = new HashMap<>();
        map.put("token",token);
        return Result.ok(map);
    }

    @ApiOperation("详情")
    @GetMapping("info")
    public Result info(HttpServletRequest request){

        // 从请求头获取用户信息（获取请求头token字符串）
        String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)){
            throw new OAException(201,"未找到token");
        }
        // 从token字符串获取用户ID
        Long userId = JwtHelper.getUserId(token);
        // 根据用户ID，在数据库中查询用户信息
        SysUser user = sysUserService.getById(userId);
        Map<String, Object> role = sysUserRoleService.findRoleByUserId(userId);
        // 查询得出用户可以操作的菜单和按钮
        List<RouterVo> menuList = sysMenuService.findUserMenuListByUserId(userId);
        List<String> btnList = sysMenuService.findUserPermsListByUserId(userId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("name",user.getUsername());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("roles",role.get("assginRoleList"));
        map.put("routers",menuList);
        map.put("buttons",btnList);
        // 返回用户可以操作的菜单和按钮
        return Result.ok(map);
    }

    @ApiOperation("登出")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
