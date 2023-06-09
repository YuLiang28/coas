package org.example.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.system.SysUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface SysUserService extends IService<SysUser> {
    void updateStatus(Long id, Integer status);

    SysUser getUserByUsername(String username);

    Map<String, Object> getCurrentUser();
}
