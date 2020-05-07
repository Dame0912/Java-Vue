<template>
  <div class="app-container">
    <el-card class="box-card">

      <el-form :inline="true" style="float: right; margin-right: 10px">
        <el-form-item>
          <el-button type="primary" plain>
            <a :href="importTemplateURL" download="ExcelUser导入数据.xlsx" target="_self">导入模板下载</a>
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-upload
            :action="importURL"
            :headers="tokenHeader"
            :on-success="fileUploadSuccess"
            :on-error="fileUploadError"
            :limit="1"
            name="file"
            :show-file-list="false"
            accept="application/vnd.ms-excel">
            <el-button type="primary">导入</el-button>
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
  import {getToken} from '@/utils/auth'

  export default {
    name: 'Normal',
    components: {Pagination},
    data() {
      return {
        listLoading: true,
        importTemplateURL: 'https://java-vue.oss-cn-hangzhou.aliyuncs.com/excel/template/ExcelUser%E5%AF%BC%E5%85%A5%E6%95%B0%E6%8D%AE.xlsx',
        importURL: process.env.VUE_APP_BASE_API + "/excel/user/import",
        tokenHeader: {'Authorization': 'Bearer ' + getToken()},
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
        ExcelApi.exportExcel()
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
        this.$message({message: res.message, type: res.success ? 'success' : 'error'})
        if (res.success) {
          setTimeout(() => {
            location.reload()
          }, 1500)
        }
      },
      fileUploadError() {
        this.$message({message: '导入失败', type: 'error'})
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
