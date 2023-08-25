<script setup>

import TopBanner from "@/components/home/TopBanner.vue"
import NavigationBar from "@/components/home/NavigationBar.vue"
import {get} from "@/net/index.js"
import {useStore} from "@/store/index.js";



</script>

<template>
  <div class="common-layout">
    <el-container class="container">
      <el-header class="top-banner">
        <TopBanner></TopBanner>
      </el-header>
      <el-container class="main-body">
        <el-aside class="aside">
          <NavigationBar></NavigationBar>
        </el-aside>
        <el-main class="main">
          <el-scrollbar style="height: calc(100vh - 65px)">
            <router-view v-slot="{ Component }">
              <transition name="el-fade-in-linear" mode="out-in">
                <component :is="Component" style="height: 100%"></component>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<style lang="less" scoped>
.common-layout{
  height: 100vh;
  .container{
    height: 100%;
    .top-banner{
      height: 65px;
      border-bottom: 1px solid var(--el-border-color);
    }
    .main-body{
      .aside{
        width: 250px;
      }
    }
  }
}

.main{
  padding: 0;
  background-color: #f7f8fa; //浅色模式下，主体内容的背景颜色
}
.dark .main{
  padding: 0;
  background-color: #212225; //深色模式下，主体内容的背景颜色
}

</style>
