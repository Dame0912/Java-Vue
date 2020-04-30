<template>
  <div class="app-container">
    <el-card class="box-card" size="small">

      <div class="filter-container">
        <el-button type="primary" icon="el-icon-edit" @click="dialogFormVisible = true">新增角色</el-button>
      </div>

      <el-table v-loading="listLoading" :data="dataList" style="width: 100%" border highlight-current-row>
        <el-table-column label="序号" align="center" width="80">
          <template slot-scope="scope">
            <span>{{(listQuery.page - 1) * listQuery.size + scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column label="ID" prop="id" align="center" width="180"></el-table-column>
        <el-table-column align="center" prop="name" label="角色名称" width="150"></el-table-column>
        <el-table-column align="center" prop="description" label="角色描述"></el-table-column>
        <el-table-column label="创建时间" align="center" width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.createdTime | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button type="primary" @click="modifyRole(scope.row.id)">编辑</el-button>
            <el-button @click="modifyRolePerm(scope.row)">权限</el-button>
            <el-button type="danger" @click="dropRowRole(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total>0" :total="total"
                  :page.sync="listQuery.page"
                  :limit.sync="listQuery.size"
                  @pagination="fetchData"
                  layout="total, prev, pager, next, jumper"
                  style="float: right; margin-top: 5px"/>
    </el-card>

    <el-dialog title="角色编辑" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :model="roleForm" label-width="70px" label-position="left"
               style="width: 600px; margin-left:50px;">
        <el-form-item label="角色名称">
          <el-input v-model="roleForm.name" placeholder="角色名称"/>
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model.trim="roleForm.description" :autosize="{ minRows: 2, maxRows: 4}" type="textarea"
                    placeholder="角色描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false; roleForm={}">取消</el-button>
        <el-button type="primary" @click="submitRoleInfoForm()">确认</el-button>
      </div>
    </el-dialog>

    <el-dialog title="权限分配" :visible.sync="permFormVisible" style="hight:100px;line-height:1px">
      <el-tree
        :data="permNodes"
        default-expand-all
        show-checkbox
        node-key="id"
        ref="tree"
        highlight-current
        :default-checked-keys="checkNodes"
        :expand-on-click-node="false"
        :props="{label:'name'}">
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span v-if="data.id !== 0 && data.type === 1"><svg-icon icon-class="list"/> {{ data.name }}</span>
          <span v-if="data.id !== 0 && data.type === 2"><svg-icon icon-class="star"/> {{ data.name }}</span>
          <span>
            <el-button type="text">
              <el-checkbox-group v-model="checkApiList">
                <el-checkbox :label="item.id" v-for="(item, index) in apiPermNodes[data.id]" :key="index"
                             @change="(checked)=>boxChange(item,checked)">
                  <span><svg-icon icon-class="icon"/> {{ item.name }}</span>
                </el-checkbox>
              </el-checkbox-group>
            </el-button>
          </span>
        </span>
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelPerm">取 消</el-button>
        <el-button type="primary" @click="assignPerm">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import * as RoleApi from '@/api/role'
  import * as PermApi from '@/api/permission'
  import TreeUtil from '@/utils/tree'
  import Pagination from '@/components/Pagination'

  export default {
    name: "Role",
    components: {Pagination},
    data() {
      return {
        listLoading: true,
        dialogFormVisible: false,
        permFormVisible: false,
        listQuery: {
          page: 1,
          size: 10
        },
        dataList: [],
        total: 0,
        roleForm: {
          id: null,
          name: '',
          description: ''
        },
        originalPermNodes: [],
        permCheck: {},
        permNodes: [],
        apiPermNodes: [],
        checkNodes: [],
        checkApiList: [],
        roleId: null
      }
    },
    created() {
      this.fetchData()
    },
    methods: {

      assignPerm() {
        let keys = this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys()).concat(this.checkApiList)
        RoleApi.assignPerm({'roleId': this.roleId, 'permIds': keys}).then(res => {
          this.$message({message: res.message, type: res.success ? 'success' : 'error'})
          this.permFormVisible = false
          this.roleId = null
          this.permCheck = {}
          this.checkApiList = []
          this.fetchData()
        })
      },
      cancelPerm() {
        this.permFormVisible = false
        this.roleId = null
        this.permCheck = {}
        this.checkApiList = []
      },
      modifyRolePerm(data) {
        this.roleId = data.id
        PermApi.findAssignPerms({'roleId': this.roleId}).then(res => {
          this.originalPermNodes = res.data.permList;
          this.permNodes = TreeUtil.transformTozTreeFormat(res.data.permList)
          this.apiPermNodes = res.data.apiPermMap

          let noApiIds = res.data.noApiIds // 树节点选中的ids
          let needDelArr = []
          noApiIds.map((id, index) => {
            this.getUnFullCheckParent(this.permNodes, id, needDelArr)
          });
          noApiIds = noApiIds.filter(item => !needDelArr.includes(item));
          this.checkNodes = noApiIds;

          this.permFormVisible = true

          let apiCheck = res.data.apiCheckPerms // API选中的权限
          apiCheck.forEach(item => this.checkApiList.push(item.id))
          setTimeout(() => {
            apiCheck.forEach(item => this.boxChange(item, true))
          }, 500)
        })
      },
      boxChange(item, checked) {
        let node = this.originalPermNodes.find(perm => perm.id === item.pid)
        if (checked) {
          this.permCheck[item.pid] = 1 + (this.permCheck[item.pid] === undefined ? 0 : this.permCheck[item.pid])
        } else {
          this.permCheck[item.pid] = this.permCheck[item.pid] - 1
        }
        this.$refs.tree.setChecked(node, this.permCheck[item.pid] > 0, true)
      },
      getUnFullCheckParent(nodeTree, id, needDelArr) {
        //递归找出半选中的数据
        nodeTree.map((item, index) => {
          if (item.id === id && item.children) {
            needDelArr.push(id);
            this.getUnFullCheckParent(item.children, id, needDelArr);
          } else if (item.id !== id && item.children) {
            this.getUnFullCheckParent(item.children, id, needDelArr);
          }
        });
      },

      dropRowRole(roleId) {
        this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          RoleApi.remove(roleId).then(res => {
            this.$message({message: res.message, type: res.success ? 'success' : 'error'})
            this.fetchData()
          })
        }).catch(() => {
          this.$message({type: 'info', message: '已取消删除'})
        })
      },
      modifyRole(roleId) {
        RoleApi.detail(roleId).then(res => {
          if (res.success) {
            this.dialogFormVisible = true
            this.roleForm = res.data
          }
        })
      },
      submitRoleInfoForm() {
        RoleApi.saveOrUpdate(this.roleForm).then(res => {
          this.$message({message: res.message, type: res.success ? 'success' : 'error'})
          if (res.success) {
            this.dialogFormVisible = false
            this.roleForm = {}
            this.fetchData()
          }
        })
      },
      fetchData() {
        this.listLoading = true
        RoleApi.list(this.listQuery).then(res => {
          this.total = res.data.total
          this.dataList = res.data.rows
          this.listLoading = false
        })
      },

    }
  }
</script>

<style scoped>

  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 50px;
  }

  .el-tree-node__content .el-button {
    display: inline-block;
  }
</style>
