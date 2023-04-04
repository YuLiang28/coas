package org.example.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Api(tags="后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @ApiOperation("登录")
    @PostMapping("login")
    public Result login(){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("token","admin");
        return Result.ok(hashMap);
    }

    @ApiOperation("详情")
    @GetMapping("info")
    public Result info(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name","admin");
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        return Result.ok(map);
    }

    @ApiOperation("登出")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
