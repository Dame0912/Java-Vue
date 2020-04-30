<template>
  <div class="dashboard-container">
    <div class="dashboard-text">欢迎来到后台管理平台</div>
  </div>
</template>

<script>
  import { mapGetters } from 'vuex'

  export default {
    name: 'Dashboard',
    computed: {
      ...mapGetters([
        'name'
      ])
    }
  }
</script>

<style lang="scss" scoped>
  .dashboard {
    &-container {
      margin: 30px;
    }

    &-text {
      margin: 20px;
      font-size: 30px;
      line-height: 46px;
    }
  }
</style>
