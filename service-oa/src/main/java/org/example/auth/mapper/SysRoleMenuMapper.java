package org.example.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.system.SysRoleMenu;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 角色菜单 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2023-04-04
 */
@Repository
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

}
