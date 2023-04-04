<template>
    <div class="app-container">
        <!--查询表单-->
        <div class="search-div">
            <el-form label-width="70px" size="small">
                <el-row>
                <el-col :span="24">
                    <el-form-item label="角色名称">
                    <el-input style="width: 100%" v-model="searchObj.roleName" placeholder="角色名称"></el-input>
                    </el-form-item>
                </el-col>
                </el-row>
                <el-row style="display:flex">
                <el-button type="primary" icon="el-icon-search" size="mini" :loading="loading" @click="getPageList">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button>
                <el-button type="success" icon="el-icon-plus" size="mini" @click="add">添加</el-button>
                <el-button class="btn-add" size="mini" @click="batchRemove()" >批量删除</el-button>
                </el-row>
            </el-form>
        </div>
        <!-- 表格 -->
        <el-table v-loading="listLoading" :data="list" stripe border style="width: 100%; margin-top: 10px"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" />

            <el-table-column label="序号" width="70" align="center">
                <template slot-scope="scope">
                    {{ (page - 1) * limit + scope.$index + 1 }}
                </template>
            </el-table-column>

            <el-table-column prop="roleName" label="角色名称" />
            <el-table-column prop="roleCode" label="角色编码" />
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column label="操作" width="200" align="center">
                <template slot-scope="scope">
                    <el-button type="primary" icon="el-icon-edit" size="mini" @click="edit(scope.row.id)" title="修改" />
                    <el-button type="danger" icon="el-icon-delete" size="mini" @click="delById(scope.row.id)"
                        title="删除" />
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <el-pagination
            :current-page="page"
            :total="total"
            :page-size="limit"
            style="padding: 30px 0; text-align: center;"
            layout="total, prev, pager, next, jumper"
            @current-change="getPageList"
        />
        <el-dialog title="添加/修改" :visible.sync="dialogVisible" width="40%" >
            <el-form ref="dataForm" :model="sysRole" label-width="150px" size="small" style="padding-right: 40px;">
                <el-form-item label="角色名称">
                <el-input v-model="sysRole.roleName"/>
                </el-form-item>
                <el-form-item label="角色编码">
                <el-input v-model="sysRole.roleCode"/>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false" size="small" icon="el-icon-refresh-right">取消</el-button>
                <el-button type="primary" icon="el-icon-check" @click="saveOrUpdate()" size="small">确定</el-button>
            </span>
        </el-dialog>
    </div>
    
</template>

<script>
import api from "@/api/system/sysRole.js";
export default {
    // 初始值
    data() {
        return {
            list: [], //角色列表
            page: 1, //当前页
            limit: 10, //每页记录数
            total: 0, //初始为0
            searchObj: {}, //条件对象
            sysRole: {}, //待添加的角色对象
            dialogVisible: false, // 弹窗显示框
            saveBtnDisabled: false, // 添加角色的保存按钮是否启用
            selectRecords: [] // 被选中的记录数
        };
    },
    created() {
        this.getPageList();
    },
    methods: {
        getPageList(page = 1) {
            this.page = page;
            api.getPageList(this.page, this.limit, this.searchObj)
                .then((response) => {
                    this.list = response.data.records;
                    this.total = response.data.total;
                });
        },
        // 重置表单
        resetData() {
            this.searchObj = {}
            this.getPageList()
        },
        delById(id){
            this.$confirm(`此操作将永久删除ID为${id}的记录, 是否继续?`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => { // promise
                // 点击确定，远程调用ajax
                return api.delById(id)
            }).then((response) => {
                this.getPageList(this.page)
                this.$message.success(response.message || `${id} 删除成功`)
            })
        },
        add(){
            this.dialogVisible = true
        },
        saveOrUpdate() {
            this.saveBtnDisabled = true // 防止表单重复提交
            if (!this.sysRole.id) {
                this.saveData()
            } else {
                this.updateData()
            }
        },
        // 新增
        saveData() {
            api.save(this.sysRole).then(response => {
                this.$message.success(response.message || '添加成功')
                this.dialogVisible = false
                this.getPageList(this.page)
            })
        },
        updateData() {
            api.update(this.sysRole).then(response => {
                this.$message.success(response.message || '修改成功')
                this.dialogVisible = false
                this.getPageList(this.page)
            })
        },
        edit(id) {
            this.getById(id)
            this.dialogVisible = true
        },
        getById(id) {
            api.getById(id).then(response => {
                this.sysRole = response.data
            })
        },
        handleSelectionChange(selection) {
            this.selectRecords = selection
        },
        batchRemove(){
            if(this.selectRecords.length == 0 ){
                this.$message.warning('请选择要删除的记录！')
                return
            }
            
            var ids = []
            this.selectRecords.forEach(item => {
                ids.push(item.id)
            })

            this.$confirm(`此操作将永久删除以下ID的记录, 是否继续?\n${ids.toString()}`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => { // promise
                // 点击确定，远程调用ajax
                return api.batchDel(ids)
            }).then((response) => {
                this.getPageList(this.page)
                this.$message.success(response.message || `${id} 删除成功`)
            })
        }
    }
};
</script>
