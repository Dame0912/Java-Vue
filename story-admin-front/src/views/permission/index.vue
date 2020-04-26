<template>
  <div class="app-container">
    <el-card class="box-card" size="small" style="height: 80%">
      <el-container>
      <el-header>
        <el-input placeholder="输入关键字进行过滤" v-model="filterText"></el-input>
      </el-header>
      <el-container>
        <el-aside width="300px" v-loading="treeLoading" style="background-color: white;">
          <el-tree
            v-loading="treeLoading"
            class="filter-tree"
            :data="permNodes"
            default-expand-all
            :filter-node-method="filterNode"
            :expand-on-click-node="false"
            @node-click="selectNode"
            ref="tree">
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span v-if="data.id === 0"><svg-icon icon-class="tree"/> {{ data.name }}</span>
              <span v-if="data.id !== 0 && data.type === 1"><svg-icon icon-class="list"/> {{ data.name }}</span>
              <span v-if="data.id !== 0 && data.type === 2"><svg-icon icon-class="star"/> {{ data.name }}</span>
              <span>
                <el-button v-show="data.id===0" type="text" @click.stop="fetchData({'type': '0'})">
                  <i class="el-icon-refresh"/>
                </el-button>
                <el-button type="text" @click.stop="append(data)">
                  <i class="el-icon-plus"/>
                </el-button>
                <el-button v-if="data.id!==0" type="text" @click="remove(data)">
                  <i class="el-icon-close"/>
                </el-button>
              </span>
            </span>
          </el-tree>
        </el-aside>

        <el-main>
          <transition name="el-fade-in">
            <el-form ref="permForm" :rules="rules" v-show="permFormVisible" :model="permForm" label-width="100px"
                     style="width: 400px;">
              <el-radio-group v-model="permType">
                <el-radio-button label="1">菜单权限</el-radio-button>
                <el-radio-button label="2">按钮权限</el-radio-button>
              </el-radio-group>
              <div style="margin: 20px;"></div>
              <el-form-item label="权限名称" prop="name">
                <el-input v-model.trim="permForm.name" placeholder="权限名称"></el-input>
              </el-form-item>
              <el-form-item label="权限编码" prop="code">
                <el-input v-model.trim="permForm.code" placeholder="权限编码"></el-input>
              </el-form-item>
              <el-form-item label="权限描述" prop="description">
                <el-input v-model.trim="permForm.description" :autosize="{ minRows: 2, maxRows: 4}" type="textarea" placeholder="权限描述"></el-input>
              </el-form-item>
              <el-form-item style="text-align: center">
                <el-button @click="permFormVisible = false; permForm={}">取消</el-button>
                <el-button type="primary" @click="submitPermForm()">保存</el-button>
              </el-form-item>
            </el-form>
          </transition>
        </el-main>
        <el-main>
          <transition name="el-fade-in" >
              <div style="width: 460px;" v-show="apiTableVisible">
              <el-button type="primary" @click="dialogApiFormVisible = true">新增API权限</el-button>
              <br/><br/>
              <el-table :data="tableApiData" stripe border highlight-current-row style="width: 100%">
                <el-table-column label="权限名称" prop="name" align="center"></el-table-column>
                <el-table-column label="权限编码" prop="code" align="center"></el-table-column>
                <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
                  <template slot-scope="scope">
                    <el-button type="primary" @click="modifyApiPerm(scope.row.id)">编辑</el-button>
                    <el-button type="danger" @click="remove(scope.row, scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              </div>
          </transition>
        </el-main>
      </el-container>
    </el-container>

      <el-dialog title="API权限编辑" :visible.sync="dialogApiFormVisible" width="30%">
      <el-form ref="apiPermForm" :rules="rules" :model="apiPermForm" label-position="left" label-width="80px" style="margin:5%;">
        <el-form-item label="权限名称">
          <el-input v-model.trim="apiPermForm.name" placeholder="权限名称"/>
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model.trim="apiPermForm.code" placeholder="权限编码"/>
        </el-form-item>
        <el-form-item label="权限描述" prop="description">
          <el-input v-model.trim="apiPermForm.description" :autosize="{ minRows: 2, maxRows: 4}" type="textarea" placeholder="权限描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogApiFormVisible = false; apiPermForm={}">取消</el-button>
        <el-button type="primary" @click="submitApiForm()">确认</el-button>
      </div>
    </el-dialog>
    </el-card>
  </div>
</template>

<script>
  import * as PermApi from '@/api/permission'
  import TreeUtil from '@/utils/tree'

  export default {
    name: "Permission",
    data() {
      return {
        listLoading: true,
        treeLoading: false,
        permFormVisible: false,
        apiTableVisible: false,
        dialogApiFormVisible:false,
        filterText: '',
        permNodes: [],
        permType: 1,
        permForm: {},
        tableApiData: [],
        apiPermForm:{},
        rules: {
          name: [{ required: true, message: '权限名称必填', trigger: 'blur' }],
          code: [{ required: true, message: '权限编码必填', trigger: 'blur' }]
        },
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    created() {
      this.fetchData({'type': '0'}) // 查询菜单和按钮
    },
    methods: {
      submitApiForm(){
        this.$refs.apiPermForm.validate(valid => {
          if(valid){
            this.apiPermForm.type = 3
            this.apiPermForm.pid = this.permForm.id
            PermApi.saveOrUpdate(this.apiPermForm).then(res => {
              this.$message({message: res.message, type: res.success ? 'success' : 'error'})
              this.apiPermForm = {}
              this.findTableApiData(this.permForm.id)
              this.dialogApiFormVisible = false
            })
          }else {
            return false
          }
        })
      },
      modifyApiPerm(apiPermId) {
        PermApi.detail(apiPermId).then(res => {
          this.apiPermForm = res.data
          this.dialogApiFormVisible = true
        })
      },
      remove(data, index) {
        this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          PermApi.remove(data.id).then(res => {
            this.$message({ type: res.success ? 'success' : 'error', message: res.message })
            if(data.type !== 3){
              this.fetchData({ 'type': '0' })
            }else{
              this.tableApiData.splice(index, 1)
            }
          })
        }).catch(() => {
          this.$message({ type: 'info', message: '已取消删除' })
        })
      },
      findTableApiData(pid){
        PermApi.findAll({pid, 'type':'3'}).then(res => {
          this.tableApiData = res.data
        })
      },
      selectNode(data){
        if(data.id !== 0){
          PermApi.detail(data.id).then(res => {
            this.permForm = res.data
            this.permFormVisible = true
            this.permType = this.permForm.type
            this.findTableApiData(this.permForm.id)
            this.apiTableVisible = true
          })
        }else {
          this.reset()
        }
      },
      submitPermForm() {
        this.$refs.permForm.validate(valid => {
          if(valid){
            this.permForm.type = this.permType
            PermApi.saveOrUpdate(this.permForm).then(res => {
              this.$message({message: res.message, type: res.success ? 'success' : 'error'})
              this.permForm = {}
              location.reload()
            })
          }else{
            return false
          }
        })
      },
      append(data) {
        this.reset()
        this.permForm.pid = data.id === 0 ? null : data.id
        this.permFormVisible = true
        if(data.id !== 0){
          this.apiTableVisible = true
        }
      },
      reset(){
        this.permForm = {}
        this.tableApiData = []
        this.permType = 1
        this.apiPermForm = {}
        this.permFormVisible = false
        this.apiTableVisible = false
      },
      fetchData(data) {
        this.treeLoading = true
        this.reset()
        PermApi.findAll(data).then(res => {
          this.permNodes = [{
            id: 0,
            name: '所有权限',
            type: 1,
            children: TreeUtil.transformTozTreeFormat(res.data)
          }]
          this.treeLoading = false
        })
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      }
    }
  }
</script>

<style scoped>
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 15px;
    padding-right: 8px;
  }

  .custom-tree-node .el-button {
    color: #606266;
    display: none;
  }

  .el-tree-node__content:hover .el-button {
    display: inline-block;
  }

</style>
