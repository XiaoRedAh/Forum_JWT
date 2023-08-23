<script setup>

import {logout} from "@/net/index.js";
import router from "@/router/index.js";
import TopBanner from "@/components/home/TopBanner.vue"
import NavigationBar from "@/components/home/NavigationBar.vue"

const userLogout = ()=>{
  logout(()=>router.push('/'))
}
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
        <el-main class="main">Main</el-main>
      </el-container>
    </el-container>
  </div>
<!--  <div>-->
<!--    <el-button @click="userLogout" type="danger">退出登录</el-button>-->
<!--  </div>-->
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
      .main{


      }
    }
  }
}

</style>
