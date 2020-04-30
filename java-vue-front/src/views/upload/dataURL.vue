<template>
  <div class="app-container" style="margin-left: 50px">
    <h3>DataURL方式上传</h3>

    <div style="width: 50px; height: 50px">
      <img v-if="imageUrl" :src="imageUrl" style="width: 50px; height: 50px"/>
    </div>

    <el-form>
      <el-form-item>
        <i>请使用 .jpg 格式图片进行测试：</i><br>
        <input type="file" @change="getFile($event)"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="dataUrl_Upload">提交</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<script>
  import * as OssApi from '@/api/oss'

  export default {
    name: "dataURL",
    data() {
      return {
        dataUrlForm: {
          avatarFile: null
        },
        imageUrl: ''
      }
    },
    methods: {
      getFile(event) {
        this.dataUrlForm.avatarFile = event.target.files[0];
      },
      dataUrl_Upload() {
        if (this.dataUrlForm.avatarFile) {
          let formData = new window.FormData();
          formData.append("avatarFile", this.dataUrlForm.avatarFile)
          OssApi.upload_dataUrl(formData).then(res => {
            this.imageUrl = res.data
          })
        } else {
          this.$message({type: 'warn', message: '请选择文件后再上传!'})
        }
      }
    }
  }
</script>

<style scoped>

</style>
