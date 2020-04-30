<template>
  <div class="app-container" style="margin-left: 50px">
    <h3>OSS后台方式上传</h3><br><br>
    <!-- 头衔缩略图 -->
    <pan-thumb :image="avatarUrl"/>
    <!-- 文件上传按钮 -->
    <el-button type="primary" icon="el-icon-upload" @click="imageCropperShow=true">更换头像</el-button>
    <image-cropper
      v-show="imageCropperShow"
      :width="300"
      :height="300"
      :key="imageCropperKey"
      :url="uploadURL"
      field="avatarFile"
      @close="close"
      @crop-upload-success="cropSuccess"/>
  </div>
</template>

<script>
  import ImageCropper from '@/components/ImageCropper'
  import PanThumb from '@/components/PanThumb'

  export default {
    name: "ossBackUpload",
    data() {
      return {
        uploadURL: process.env.VUE_APP_BASE_API + '/upload/oss/back',
        imageCropperShow: false, // 是否显示上传组件
        imageCropperKey: 0, // 上传组件id
        avatarUrl: ''
      }
    },
    components: {ImageCropper, PanThumb},
    methods: {
      // 关闭上传组件
      close() {
        this.imageCropperShow = false
        // 上传失败后，重新打开上传组件时初始化组件，否则显示上一次的上传结果
        this.imageCropperKey = this.imageCropperKey + 1
      },
      cropSuccess(data) {
        this.imageCropperShow = false
        // data为后台返回的内容
        this.avatarUrl = data
        // 上传成功后，重新打开上传组件时初始化组件，否则显示上一次的上传结果
        this.imageCropperKey = this.imageCropperKey + 1
      }
    }
  }
</script>

<style scoped>

</style>
