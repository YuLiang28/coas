package org.example.security.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import org.example.common.jwt.JwtHelper;
import org.example.common.result.Result;
import org.example.common.result.ResultCodeEnum;
import org.example.common.utils.ResponseUtil;
import org.example.model.system.SysUser;
import org.example.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        logger.info("uri:"+request.getRequestURI());
        //如果是登录接口，直接放行
        if("/admin/system/index/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, Result.build(null, ResultCodeEnum.LOGIN_ERROR));
        }
    }

    // 判断请求头中是否有 token
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            return null;
        }


        String username = JwtHelper.getUsername(token);
        Long userId = JwtHelper.getUserId(token);
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(userId)) {
            return null;
        }

        //通过ThreadLocal记录当前登录人信息
        LoginUserInfoHelper.setUserId(userId);
        LoginUserInfoHelper.setUserName(username);


        // 通过username获取权限
        String authJson =(String) redisTemplate.opsForValue().get(username);
        if(StringUtils.isEmpty(authJson)){
            return new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    Collections.emptyList()
            );
        }

        // 如果redis中存在权限数据
        List<Map> maps = JSON.parseArray(authJson, Map.class);

        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        for(Map map:maps){
            String authority = (String) map.get("authority");
            authList.add(new SimpleGrantedAuthority(authority));
        }
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                authList
        );
    }
}




