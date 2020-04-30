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
          <!-- 文件上传按钮 -->
          <el-button type="primary" icon="el-icon-upload" @click="avatarChange">更换头像</el-button>
          <image-cropper
            :params="uploadData"
            v-show="imageCropperShow"
            :width="300"
            :height="300"
            :key="imageCropperKey"
            :url="uploadURL"
            field="file"
            :imgName="imgName"
            :imgFormat="imgFormat"
            @close="close"
            @crop-upload-success="cropSuccess"/>
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
  import ImageCropper from '@/components/ImageCropper'
  import store from '@/store'
  import * as UserAPi from '@/api/user'
  import {upload_oss_policy} from '@/api/oss'

  export default {
    name: 'Profile',
    components: {PanThumb, ImageCropper},
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
        uploadData: {
          OSSAccessKeyId: '',
          policy: '',
          signature: '',
          key: '',
          dir: '',
          success_action_status: 200
        },
        uploadURL: '',
        imageCropperShow: false, // 是否显示上传组件
        imageCropperKey: 0, // 上传组件id
        imgName: '',
        imgFormat: 'png',

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
      avatarChange() {
        let _self = this;
        return new Promise((resolve, reject) => {
          upload_oss_policy().then(response => {
            _self.uploadData.policy = response.data.policy
            _self.uploadData.signature = response.data.signature
            _self.uploadData.OSSAccessKeyId = response.data.accessKeyId
            _self.uploadData.key = response.data.dir + '/${filename}'
            _self.uploadData.dir = response.data.dir
            _self.uploadURL = response.data.host
            this.imgName = this.getUID();
            this.imageCropperShow = true
            resolve(true)
          }).catch(err => {
            console.log(err)
            reject(false)
          })
        })
      },
      // 关闭上传组件
      close() {
        this.imageCropperShow = false
        // 上传失败后，重新打开上传组件时初始化组件，否则显示上一次的上传结果
        this.imageCropperKey = this.imageCropperKey + 1
      },
      cropSuccess(data) {
        this.imageCropperShow = false
        // data为后台返回的内容
        this.userInfo.avatar = this.uploadURL + '/' + this.uploadData.dir + '/' + this.imgName + '.' + this.imgFormat;
        // 上传成功后，重新打开上传组件时初始化组件，否则显示上一次的上传结果
        this.imageCropperKey = this.imageCropperKey + 1
        UserAPi.editAvatar({'avatar': this.userInfo.avatar}).then(res => {
          this.$message({message: res.message, type: res.success ? 'success' : 'error'})
        })
      },
      getUID() { // 获取唯一值
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
          let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
          return v.toString(16);
        });
      },

    }
  }
</script>

<style lang="scss">

</style>

