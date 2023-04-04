package org.example.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.system.SysUser;
import org.springframework.stereotype.Service;

@Service
public interface SysUserService extends IService<SysUser> {
    void updateStatus(Long id, Integer status);
}
