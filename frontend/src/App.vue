<script setup>
import { useDark, useToggle } from '@vueuse/core'
import {Sunny, Moon} from "@element-plus/icons-vue";

const isDark = useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})

const toggleDark = useToggle(isDark)

</script>

<template>
  <el-button :icon="isDark?Moon:Sunny"  @click="toggleDark()" circle
             size="large" style="position: absolute;left: 50px;top: 50px;"/>
  <router-view></router-view>
</template>

<style scoped>

</style>
