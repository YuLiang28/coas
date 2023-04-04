package org.example.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.system.SysUserRole;
import org.example.vo.system.AssginRoleVo;

import java.util.Map;

/**
 * <p>
 * 用户角色 服务类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-03
 */
public interface SysUserRoleService extends IService<SysUserRole> {
    /**
     * 根据用户获取角色数据
     * @param userId
     * @return
     */
    Map<String, Object> findRoleByUserId(Long userId);

    /**
     * 分配角色
     * @param assginRoleVo
     */
    void assignUserRole(AssginRoleVo assginRoleVo);
}
