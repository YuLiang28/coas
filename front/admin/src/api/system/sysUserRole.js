import request from '@/utils/request'

const api_name = '/admin/system/sysUserRole'

export default {
  getRoles(adminId) {
    return request({
      url: `${api_name}/findRoleByUserId/${adminId}`,
      method: 'get'
    })
  },
  assignRoles(assginRoleVo) {
    return request({
      url: `${api_name}/assignUserRole`,
      method: 'post',
      data: assginRoleVo
    })
  }
}
