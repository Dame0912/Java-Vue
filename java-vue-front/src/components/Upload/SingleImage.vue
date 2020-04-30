<template> 
  <div>
    <el-upload
      :action="dataObj.host"
      :data="dataObj"
      list-type="picture"
      :multiple="false"
      :show-file-list="true"
      :file-list="fileList"
      :on-remove="handleRemove"
      :before-upload="beforeUpload"
      :on-success="handleUploadSuccess">
      <el-button size="small" type="primary">点击上传</el-button>
      <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible" v-if="fileList.length>0">
      <img width="100%" :src="fileList[0].url" alt="">
    </el-dialog>
  </div>
</template>
<script>
  import {upload_oss_policy} from '@/api/oss'

  export default {
    name: 'singleUpload',
    data() {
      return {
        dataObj: {
          policy: '',
          signature: '',
          OSSAccessKeyId: '',
          key: '',
          host: '',
          dir: '',
        },
        dialogVisible: false,
        fileList:[]
      };
    },
    methods: {
      emitInput(val) {
        this.$emit('input', val)
      },
      handleRemove(file, fileList) {
        this.emitInput('');
      },
      beforeUpload(file) {
        let _self = this;
        return new Promise((resolve, reject) => {
          upload_oss_policy().then(response => {
            _self.dataObj.policy = response.data.policy;
            _self.dataObj.signature = response.data.signature;
            _self.dataObj.OSSAccessKeyId = response.data.accessKeyId;
            _self.dataObj.key = response.data.dir + '/${filename}';
            _self.dataObj.dir = response.data.dir;
            _self.dataObj.host = response.data.host;
            resolve(true)
          }).catch(err => {
            console.log(err)
            reject(false)
          })
        })
      },
      handleUploadSuccess(res, file) {
        //this.fileList.pop();
        this.fileList.push({name: file.name, url: this.dataObj.host + '/' + this.dataObj.dir + '/' + file.name});
        this.emitInput(this.fileList[0].url);
      }
    }
  }
</script>
<style>

</style>


