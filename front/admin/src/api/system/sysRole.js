/*
角色管理相关的API请求函数
*/
import request from '@/utils/request'

const api_name = '/admin/system/sysRole'
export default {
  // 获取所有角色
  findAll() {
    return request({
      url: `${api_name}/findAll`,
      method: 'get'
    })
  },
  // 条件分页查询角色
  getPageList(page, limit, searchObj) {
    return request({
      url: `${api_name}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },
  // 添加角色
  save(roleData) {
    return request({
      url: `${api_name}/save`,
      method: 'post',
      data: roleData
    })
  },
  // 根据 ID 查询角色
  getById(id) {
    return request({
      url: `${api_name}/get/${id}`,
      method: 'get'
    })
  },
  // 修改角色
  update(roleData) {
    return request({
      url: `${api_name}/update`,
      method: 'put',
      data: roleData
    })
  },
  // 根据 ID 删除角色
  delById(id) {
    return request({
      url: `${api_name}/delete/${id}`,
      method: 'delete'
    })
  },
  // 批量删除角色
  batchDel(ids) {
    return request({
      url: `${api_name}/batchDelete`,
      method: 'delete',
      data: ids
    })
  }
}
