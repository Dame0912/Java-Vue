<template>
  <div class="app-container">
    <el-card class="box-card" size="small">
      <el-form :model="userInfo" label-width="100px" class="demo-ruleForm" style="width:350px">
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="userInfo.mobile" disabled/>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userInfo.username" disabled/>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <pan-thumb :image="userInfo.avatar" :height="'100px'" :width="'100px'" :hoverable="true">
            <div>Hello</div>
            {{ userInfo.username }}
          </pan-thumb>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="userInfo.status" active-color="#13ce66" inactive-color="#ff4949" :active-value="1"
                     :inactive-value="0" disabled/>
        </el-form-item>
        <el-form-item label="部门" prop="departmentName">
          <el-input v-model="userInfo.departmentName" disabled/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="dialogPwdFormVisible=true; pwdForm={}">重置密码</el-button>
        </el-form-item>
      </el-form>

      <el-dialog title="重置密码" :visible.sync="dialogPwdFormVisible" width="500px">
        <el-form :model="pwdForm" status-icon :rules="rules" ref="rulePwdForm" label-width="120px" style="width: 350px">
          <el-form-item label="原密码" prop="originPwd">
            <el-input type="password" v-model="pwdForm.originPwd" autocomplete="off"/>
          </el-form-item>
          <el-form-item label="新密码" prop="newPwd">
            <el-input type="password" v-model="pwdForm.newPwd" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="checkPwd">
            <el-input type="password" v-model="pwdForm.checkPwd" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button @click="dialogPwdFormVisible=false; pwdForm={}">取消</el-button>
            <el-button @click="resetPwdForm('rulePwdForm')">清空</el-button>
            <el-button type="primary" @click="submitPwdForm('rulePwdForm')">提交</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
  import PanThumb from '@/components/PanThumb'
  import store from '@/store'
  import * as UserAPi from '@/api/user'

  export default {
    name: 'Profile',
    components: {PanThumb},
    data() {
      let validateCheckPwd = (rule, value, callback) => {
        if (value !== this.pwdForm.newPwd) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      }
      return {
        userInfo: {
          mobile: '',
          username: '',
          avatar: '',
          status: null,
          departmentName: '',
        },
        dialogPwdFormVisible: false,
        pwdForm: {
          originPwd: '',
          newPwd: '',
          checkPwd: ''
        },
        rules: {
          originPwd: [
            {required: true, message: '请输入原密码', trigger: 'blur'},
          ],
          newPwd: [
            {required: true, message: '请输入新密码', trigger: 'blur'},
          ],
          checkPwd: [
            {required: true, message: '请再次输入新密码', trigger: 'blur'},
            {validator: validateCheckPwd, trigger: 'blur'}
          ]
        }
      }
    },
    created() {
      this.getUser()
    },
    methods: {
      submitPwdForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            UserAPi.editPassWord(this.pwdForm).then(res => {
              this.$message({message: res.message, type: res.success ? 'success' : 'error'})
              if (res.success) {
                this.pwdForm = {}
                this.dialogPwdFormVisible = false
                setTimeout(() => {
                  store.dispatch('user/resetToken').then(() => {
                    location.reload()
                  })
                }, 2000)
              }
            })
          } else {
            return false;
          }
        });
      },
      resetPwdForm(formName) {
        this.$refs[formName].resetFields();
      },
      getUser() {
        UserAPi.profileInfo().then(res => {
          this.userInfo = res.data
        })
      },
    }
  }
</script>

<style lang="scss">

</style>

