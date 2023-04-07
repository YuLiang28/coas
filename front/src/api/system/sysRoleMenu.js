import request from '@/utils/request'

const api_name = '/admin/system/sysRoleMenu'

export default {
    /*
    查看某个角色的权限列表
    */
    findMenuByRoleId(roleId) {
        return request({
            url: `${api_name}/findMenuByRoleId/${roleId}`,
            method: 'get'
        })
    },

    /*
    给某个角色授权
    */
    assignRoleMenu(assginMenuVo) {
        return request({
            url: `${api_name}/assignRoleMenu`,
            method: "post",
            data: assginMenuVo
        })
    }
}