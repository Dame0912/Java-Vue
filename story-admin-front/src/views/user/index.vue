<template>
  <div class="app-container">
    <el-card class="box-card">

      <div class="filter-container">
        <el-form :inline="true" :model="listQuery" class="demo-form-inline">
          <el-form-item label="手机号">
            <el-input v-model="listQuery.mobile" placeholder="手机号"></el-input>
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="listQuery.username" placeholder="用户名"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="listQuery.status" placeholder="员工状态">
              <el-option label="正常" :value="1"></el-option>
              <el-option label="冻结" :value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button v-if="checkPoint('settings-user-select')" type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
            <el-button type="default" icon="el-icon-refresh" @click="onReset">重置</el-button>
            <el-button v-if="checkPoint('settings-user-add')" type="primary" icon="el-icon-edit" @click="dialogFormVisible = true">新增</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table v-loading="listLoading" :data="dataList" style="width: 100%" border highlight-current-row>
        <el-table-column label="序号" align="center" width="80">
          <template slot-scope="scope">
            <span>{{(listQuery.page - 1) * listQuery.size + scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column label="ID" prop="id" align="center" width="180"></el-table-column>
        <el-table-column align="center" prop="mobile" label="手机号码" width="150"></el-table-column>
        <el-table-column align="center" prop="username" label="用户名称" width="180"></el-table-column>
        <el-table-column align="center" prop="departmentName" label="部门名称" width="120"></el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status | statusTagFilter">{{ scope.row.status | statusFilter }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" width="160">
          <template slot-scope="scope">
            <span>{{ scope.row.createdTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-if="checkPoint('settings-user-edit')" type="primary" @click="modifyUser(scope.row.id)">编辑</el-button>
            <el-button v-if="checkPoint('settings-user-role')" @click="modifyUserRole(scope.row.id)">角色</el-button>
            <el-button v-if="checkPoint('settings-user-delete')" type="danger" @click="dropRowUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total"
                  :page.sync="listQuery.page"
                  :limit.sync="listQuery.size"
                  @pagination="fetchData"
                  layout="total, prev, pager, next, jumper"
                  style="float: right; margin-top: 5px" />
    </el-card>


    <el-dialog title="用户编辑" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="userForm" label-width="70px" label-position="left" style="width: 600px; margin-left:50px;">
        <el-form-item label="手机号">
          <el-input v-model="userForm.mobile" placeholder="手机号"/>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" placeholder="用户名"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="userForm.password" placeholder="密码" :disabled="userForm.id != null"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="userForm.status" active-color="#13ce66" inactive-color="#ff4949" :active-value="1" :inactive-value="0"/>
        </el-form-item>
        <el-form-item label="部门">
          <el-input placeholder="请选择" v-model="userForm.departmentName" class="inputW"
                    @click.native="isShowSelect = !isShowSelect"></el-input>
          <input v-model="userForm.departmentId" type="hidden"/>
          <el-tree v-if="isShowSelect"
                   :expand-on-click-node="false"
                   :data="deptCategory"
                   :props="{label:'name'}"
                   default-expand-all
                   @node-click="handleNodeClick"
                   class="objectTree"
                   ref="tree2">
          </el-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false; userForm={}">取消</el-button>
        <el-button type="primary" @click="submitUserInfoForm()">确认</el-button>
      </div>
    </el-dialog>

    <el-dialog title="角色分配" :visible.sync="dialogRoleVisible" width="500">
      <el-checkbox-group class="custom-check" v-model="checkRoleList">
        <el-checkbox :label="item.id" v-for="(item, index) in roleList" :key="index">
          <span>{{item.name}}</span>
        </el-checkbox>
      </el-checkbox-group>

      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelAssign()">取消</el-button>
        <el-button type="primary" @click="assignRole()">确认</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import * as UserApi from '@/api/user'
  import * as RoleApi from '@/api/role'
  import { hasPermissionPoint } from '@/utils/permissionUtil'
  import {organList} from '@/api/department'
  import Pagination from '@/components/Pagination'

  export default {
    name: 'User',
    components: {Pagination},
    filters: {
      statusFilter(status) {
        if (status) {
          return "正常";
        } else {
          return "冻结";
        }
      },
      statusTagFilter(status) {
        if (status) {
          return "success";
        } else {
          return "danger";
        }
      }
    },
    data() {
      return {
        listLoading: true,
        dialogFormVisible: false,
        dialogRoleVisible: false,
        deptCategory: [],
        isShowSelect: false,
        listQuery: {
          page: 1,
          size: 10,
          mobile: '',
          username: '',
          status: null
        },
        dataList: [],
        total: 0,
        userForm: {
          id: null,
          mobile: '',
          username: '',
          password: '',
          status: '',
          departmentId: '',
          departmentName: ''
        },
        roleList: [],
        checkRoleList:[],
        userId: null
      }
    },
    created() {
      this.getBaseData()
    },
    methods: {
      checkPoint(point){
        return hasPermissionPoint(point);
      },
      assignRole(){
        UserApi.assignRoles({'userId': this.userId, 'roleIds': this.checkRoleList}).then(res=>{
          this.$message({message: res.message, type: res.success ? 'success' : 'error'})
          this.checkRoleList = []
          this.userId = null
          this.dialogRoleVisible = false
        })
      },
      cancelAssign(){
        this.dialogRoleVisible = false
        this.checkRoleList=[]
        this.userId = null
      },
      modifyUserRole(userId) {
        RoleApi.findAll().then(res=>{
          this.userId = userId
          this.roleList = res.data
          UserApi.findUserRole(userId).then(res => {
            this.checkRoleList = res.data
            this.dialogRoleVisible = true
          })
        })
      },

      dropRowUser(userId) {
        this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          UserApi.remove(userId).then(res => {
            this.$message({message: res.message, type: res.success ? 'success' : 'error'})
            this.fetchData()
          })
        }).catch(() => {
          this.$message({type: 'info', message: '已取消删除'})
        })
      },
      modifyUser(userId) {
        UserApi.findById(userId).then(res => {
          if(res.success){
            this.dialogFormVisible = true
            this.userForm = res.data
          }
        })
      },

      submitUserInfoForm() {
        UserApi.saveOrUpdate(this.userForm).then(res => {
          this.$message({message: res.message, type: res.success ? 'success' : 'error'})
          if (res.success) {
            this.dialogFormVisible = false
            this.userForm = {}
            this.fetchData()
          }
        })
      },
      onReset() {
        this.listQuery.mobile = ''
        this.listQuery.username = ''
        this.listQuery.status = null
      },
      fetchData() {
        this.listLoading = true
        UserApi.findAllPage(this.listQuery).then(res => {
          this.total = res.data.total
          this.dataList = res.data.rows
          this.listLoading = false
        })
      },
      deptList() {
        organList().then(response => {
          this.deptCategory = response.data.deptCategory
        })
      },
      getBaseData(){
        this.listLoading = true
        UserApi.findAllPage(this.listQuery).then(res => {
          this.total = res.data.total
          this.dataList = res.data.rows
          organList().then(response => {
            this.deptCategory = response.data.deptCategory
            this.listLoading = false
          })
        })
      },
      handleNodeClick(data) {
        this.userForm.departmentName = data.name
        this.userForm.departmentId = data.id
        this.isShowSelect = false
      }
    }
  }
</script>

<style scoped>

  .custom-check {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    padding-left: 30px;
  }
  .custom-check .el-checkbox {
    flex: 1;
    width: 25%;
    min-width: 25%;
    max-width: 25%;
    margin: 10px;
  }
</style>
