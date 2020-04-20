<template>
  <div>
    <theme-picker class="pick" @change="themeChange"/>
  </div>
</template>

<script>
  import ThemePicker from '@/components/ThemePicker'

  export default {
    components: {ThemePicker},
    data() {
      return {}
    },
    computed: {},
    methods: {
      themeChange(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'theme',
          value: val
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .pick{
    display: inline-block;
    vertical-align: middle;
  }
</style>
