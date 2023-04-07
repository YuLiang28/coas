package org.example.auth.utils;

import org.example.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        ArrayList<SysMenu> trees = new ArrayList<>();
        for (SysMenu menu:sysMenuList) {
            if(menu.getParentId().longValue()==0)
            {
                trees.add(getChildren(menu,sysMenuList));
            }
        }
        return trees;
    }

    private static SysMenu getChildren(SysMenu menu, List<SysMenu> sysMenuList) {
        menu.setChildren(new ArrayList<SysMenu>());
        for(SysMenu it:sysMenuList){
            if(menu.getId().longValue()==it.getParentId().longValue()){
                menu.getChildren().add(getChildren(it,sysMenuList));
            }
        }
        return menu;
    }

}
