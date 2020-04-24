<template>
  <div class="app-container">
    <el-card class="box-card">
      <div class='organization-index-top'>
        <div class='main-top-title'>
          <el-tabs v-model="activeName">
            <el-tab-pane label="组织结构" name="first"></el-tab-pane>
          </el-tabs>
          <div class="el-tabs-report">
            <el-button type="primary">导入</el-button>
            <el-button type="primary">导出</el-button>
          </div>
        </div>
      </div>

      <div class="treBox">
        <div class="treeCon clearfix">
          <div class="tree-item-name">
            <i class="fa fa-university" aria-hidden="true"></i>
            <span><strong>{{companyWithDept.companyName}}</strong></span>
          </div>
          <div class="tree-item-do-tope">
            <span>{{companyWithDept.companyManage}}</span>
            <span style="margin-left: 230px">在职(100/100)</span>
            <span style="margin-left: 45px">
              <el-dropdown>
                <span class="el-dropdown-link">
                  操作<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item icon="el-icon-circle-plus-outline">
                    <el-button type="text" @click="handlAdd('')">添加子部门</el-button>
                  </el-dropdown-item>
                  <!--<el-dropdown-item icon="el-icon-check">
                    <el-button type="text">查看待分配员工</el-button>
                  </el-dropdown-item>-->
                </el-dropdown-menu>
              </el-dropdown>
            </span>
          </div>
        </div>

        <el-tree :data="deptCategory" node-key="id" default-expand-all :expand-on-click-node="false">
          <span class="custom-tree-node" slot-scope="{ node, data }">
            <div class="tree-item-name">
              <i v-if="node.isLeaf" class="fa fa-male"></i>
              <i v-else :class="node.expanded?'fa fa-minus-square-o':'fa fa-plus-square-o'"></i>
              <span>{{ data.name }}</span>
            </div>
            <div class="tree-item-do">
              <span>{{data.manager}}</span>
              <span style="margin-left: 230px">在职(100/100)</span>
              <span style="margin-left: 45px">
                <el-dropdown>
                  <span class="el-dropdown-link">
                    操作<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-plus">
                      <el-button type="text" @click="handlAdd(data.id)">添加子部门</el-button>
                    </el-dropdown-item>
                    <el-dropdown-item icon="el-icon-check">
                      <el-button type="text" @click="handlUpdate(data.id)">查看部门</el-button>
                    </el-dropdown-item>
                    <!--<el-dropdown-item icon="el-icon-circle-plus-outline">查看待分配员工</el-dropdown-item>-->
                    <el-dropdown-item icon="el-icon-circle-check">
                      <el-button type="text" @click="handlDelete(data.id)">删除部门</el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </span>
            </div>
          </span>
        </el-tree>
      </div>
    </el-card>
    <dept-info ref="deptRef" @refreshDept = "deptList"/>
  </div>
</template>

<script>
  import { organList, detail, remove, saveOrUpdate } from '@/api/department'
  import { getAll } from '@/api/user'
  import DeptInfo from './deptInfo'

  export default {
    data() {
      return {
        activeName: 'first',
        listLoading: true,
        companyWithDept: {},
        deptCategory: []
      }
    },
    components: {
      DeptInfo
    },
    created() {
      this.deptList()
      setTimeout(()=>{
        this.getAllUser()
      },2000)
    },
    methods: {
      deptList() {
        this.listLoading = true
        organList().then(response => {
          this.companyWithDept = response.data
          this.deptCategory = this.companyWithDept.deptCategory
          this.listLoading = false
        })
      },
      handlAdd(id) {
        this.$refs.deptRef.parentId = id
        this.$refs.deptRef.dialogFormVisible = true
      },
      handlUpdate(id) {
        detail(id).then(res => {
          this.$refs.deptRef.dept = res.data
          this.$refs.deptRef.parentId = res.data.pid
          this.$refs.deptRef.dialogFormVisible = true
        })
      },
      handlDelete(id) {
        this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          remove(id).then(res => {
            this.$message({
              type: res.success ? 'success' : 'error',
              message: res.message
            })
            this.deptList()
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
      },
      getAllUser(){
        getAll().then(res=>{
          this.$refs.deptRef.users = res.data
        })
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss">
  .main-top-title {
    padding-left: 20px;
    padding-top: 20px;
    text-align: left;
  }

  .organization-index-top {
    position: relative;
  }

  .organization-index-top .el-tabs-report {
    position: absolute;
    top: 20px;
    right: 15px;
    z-index: 999;
  }

  .treBox {
    padding: 30px 120px 0;
    position: relative;
  }

  .treeCon {
    border-bottom: 1px solid #cfcfcf;
    padding: 10px 0;
    margin-bottom: 10px;
  }

  .tree-item-name {
    float: left;
  }

  .tree-item-do {
    float: right;
    position: absolute;
    right: 25px;
  }

  .tree-item-do-tope {
    float: right;
    position: absolute;
    right: 145px;
  }


</style>
