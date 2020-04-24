<template>
  <div class="app-container">
    <el-dialog title="编辑部门" :visible.sync="dialogFormVisible">
      <el-form :model="dept" label-width="120px">
        <el-form-item label="部门名称">
          <el-input v-model="dept.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input v-model="dept.code" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="部门负责人">
          <el-select v-model="dept.managerId" filterable placeholder="请选择">
            <el-option
              v-for="item in users"
              :key="item.id"
              :label="item.username"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部门介绍">
          <el-input v-model="dept.introduce" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveDept">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { saveOrUpdate } from '@/api/department'

  export default {
    name: 'deptInfo',
    data() {
      return {
        dialogFormVisible: false,
        formLabelWidth: '120px',
        parentId: '',
        dept: {},
        users:[]
      }
    },
    methods: {
      saveDept() {
        this.dept.pid = this.parentId
        this.dept.manager = this.users.find(user=>user.id === this.dept.managerId).username
        saveOrUpdate(this.dept).then(res => {
          this.$message({
            type: res.success ? 'success' : 'error',
            message: res.message
          })
          this.dialogFormVisible = false
          this.dept = {}
          this.$emit('refreshDept');
        })
      }
    }
  }
</script>

<style scoped>

</style>
