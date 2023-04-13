package org.example.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.auth.mapper.SysMenuMapper;
import org.example.auth.service.SysMenuService;
import org.example.auth.service.SysUserRoleService;
import org.example.auth.utils.MenuHelper;
import org.example.common.config.exception.OAException;
import org.example.model.system.SysMenu;
import org.example.vo.system.MetaVo;
import org.example.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> findNodes() {
        //全部权限列表
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        if (CollectionUtils.isEmpty(sysMenuList)) return null;

        //构建树形数据
        return MenuHelper.buildTree(sysMenuList);
    }

    @Override
    public void removeMenuById(Long id) {
        // 判断当前菜单是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count>0){
            throw new OAException(201,"当前菜单存在子菜单，无法删除。");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {

        // 判断用户是否为管理员，是管理员则显示所有菜单，不是管理员，则查询出对应的菜单
        boolean isAdmin = sysUserRoleService.isAdminByUserId(userId);
        // 通过UserId获取用户角色
        List<SysMenu> menuList;

        if(isAdmin){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            // 选取状态为可用的菜单，并且排序
            wrapper.eq(SysMenu::getStatus,1).orderByAsc(SysMenu::getSortValue);
            menuList = baseMapper.selectList(wrapper);
        }else{
            menuList = baseMapper.findMenuListByUserId(userId);
        }
        //首先构建成树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(menuList);
        //再进行转换
        return this.buildRouter(sysMenuTreeList);
    }

    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        ArrayList<RouterVo> routers = new ArrayList<>();

        // 遍历 menus
        for (SysMenu menu:menus) {
            RouterVo routerVo = new RouterVo();
            routerVo.setHidden(false);
            routerVo.setAlwaysShow(false);
            routerVo.setPath(getRouterPath(menu));
            routerVo.setComponent(menu.getComponent());
            routerVo.setMeta(new MetaVo(menu.getName(),menu.getIcon()));
            // 下一层数据
            List<SysMenu> children = menu.getChildren();

            // 判断是否存在隐藏路由
            if (menu.getType() == 1) {
                // 加载隐藏路由
                List<SysMenu> hideMenuList = children.stream()
                        .filter(
                                x -> !StringUtils.isEmpty(x.getComponent())
                        ).collect(Collectors.toList());
                for (SysMenu hide : hideMenuList) {
                    RouterVo hideRouterVo = new RouterVo();

                    // 隐藏路由
                    hideRouterVo.setHidden(true);
                    hideRouterVo.setAlwaysShow(false);
                    hideRouterVo.setPath(getRouterPath(hide));
                    hideRouterVo.setComponent(hide.getComponent());
                    hideRouterVo.setMeta(new MetaVo(hide.getName(),hide.getIcon()));

                    routers.add(hideRouterVo);
                }
            }else {
                if (!CollectionUtils.isEmpty(children)) {
                    if (children.size() > 0) {
                        routerVo.setAlwaysShow(true);
                    }
                    // 递归 添加子菜单
                    routerVo.setChildren(buildRouter(children));
                }
            }
            routers.add(routerVo);
        }
        return routers;
    }

    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<String> findUserPermsListByUserId(Long userId) {
        // 判断是否为管理员，管理员拥有全部权限
        boolean isAdmin = sysUserRoleService.isAdminByUserId(userId);

        List<SysMenu> btnList;

        if (isAdmin) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            // 选取状态为可用的按钮，并且排序
            wrapper.eq(SysMenu::getStatus, 1).orderByAsc(SysMenu::getSortValue);
            btnList = baseMapper.selectList(wrapper);
        } else {
            btnList = sysMenuMapper.findMenuListByUserId(userId);
        }
        return btnList.stream()
                .filter(x -> x.getType() == 2)
                .map(x -> x.getPerms())
                .collect(Collectors.toList());
    }
}
