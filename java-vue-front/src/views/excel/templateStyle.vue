<template>
  <div class="app-container">
    <el-card class="box-card">
      <el-form :inline="true" style="float: right; margin-right: 10px">
        <el-form-item>
          <el-button type="primary" plain>
            <a :href="importTemplateURL" download="ExcelUser样式模板.xlsx" target="_self">样式模板下载</a>
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-upload
            :action="importURL"
            :data="uploadData"
            name="file"
            :before-upload="fileBeforeUpload"
            :on-success="fileUploadSuccess"
            :on-error="fileUploadError"
            :limit="1"
            :show-file-list="false"
            accept="application/vnd.ms-excel">
            <el-button type="primary">上传样式模板</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="success" plain @click="excelExport">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="listLoading" :data="dataList" style="width: 100%" border highlight-current-row>
        <el-table-column label="序号" align="center" width="80">
          <template slot-scope="scope">
            <span>{{(listQuery.page - 1) * listQuery.size + scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column label="ID" prop="id" align="center" width="180"></el-table-column>
        <el-table-column align="center" prop="username" label="姓名" width="150"></el-table-column>
        <el-table-column align="center" prop="mobile" label="手机号" width="180"></el-table-column>
        <el-table-column align="center" prop="departmentName" label="部门名称" width="120"></el-table-column>
        <el-table-column align="center" prop="sex" label="性别" width="120"></el-table-column>
        <el-table-column align="center" prop="nativePlace" label="籍贯"></el-table-column>
        <el-table-column label="出生日期" align="center" width="160">
          <template slot-scope="scope">
            <span>{{ scope.row.dateOfBirth | parseTime('{y}-{m}-{d} {h}:{i}') }}</span>
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
  </div>
</template>

<script>
  import * as ExcelApi from '@/api/excel'
  import Pagination from '@/components/Pagination'

  export default {
    name: "templateStyle",
    components: {Pagination},
    data() {
      return {
        listLoading: true,
        importTemplateURL: 'https://java-vue.oss-cn-hangzhou.aliyuncs.com/excel/template/ExcelUser%E6%A0%B7%E5%BC%8F%E6%A8%A1%E6%9D%BF.xlsx',
        importURL: '',
        exportURL: process.env.VUE_APP_BASE_API + "/excel/user/style/export",
        uploadData: {
          OSSAccessKeyId: '',
          policy: '',
          signature: '',
          key: '',
          dir: '',
          success_action_status: 200
        },
        listQuery: {
          page: 1,
          size: 10
        },
        dataList: [],
        total: 0,
      }
    },
    created() {
      this.fetchData()
    },
    methods: {
      // Excel下载
      excelExport() {
        ExcelApi.exportStyleExcel()
          .then(res => {
            const blob = new Blob([res.data])
            const fileName = decodeURIComponent(res.headers['filename'])
            const eLink = document.createElement('a')
            eLink.download = fileName
            eLink.style.display = 'none'
            eLink.href = URL.createObjectURL(blob)
            document.body.appendChild(eLink)
            eLink.click()
            URL.revokeObjectURL(eLink.href)
            document.body.removeChild(eLink)
          })
          .catch(error => {
            console.log(error);
            this.$message.error('导出报表失败！')
          })

      },
      fileUploadSuccess(res) {
        this.$message.success('上传成功')
        setTimeout(() => {
          location.reload()
        }, 1500)
      },
      fileUploadError() {
        this.$message({message: '上传失败', type: 'error'})
      },
      // 获取上传的数据
      fileBeforeUpload() {
        let _self = this
        const fileName = 'ExcelUser样式模板.xlsx'
        return new Promise((resolve, reject) => {
          ExcelApi.upload_oss_excel_policy()
            .then(res => {
              _self.uploadData.policy = res.data.policy
              _self.uploadData.signature = res.data.signature
              _self.uploadData.OSSAccessKeyId = res.data.accessKeyId
              _self.uploadData.key = `${res.data.dir}/${fileName}`
              _self.uploadData.dir = res.data.dir
              _self.importURL = res.data.host
              resolve(true)
            })
            .catch(err => {
              console.log(err)
              reject(false)
            })
        })
      },
      fetchData() {
        this.listLoading = true
        ExcelApi.findAllPage(this.listQuery).then(res => {
          this.total = res.data.total
          this.dataList = res.data.rows
          this.listLoading = false
        })
      }
    }
  }
</script>

<style scoped>

</style>
