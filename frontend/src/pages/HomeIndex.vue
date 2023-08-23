<script setup>

import {logout} from "@/net/index.js";
import router from "@/router/index.js";

const userLogout = ()=>{
  logout(()=>router.push('/'))
}
</script>

<template>
  <div class="common-layout">
    <el-container class="container">
      <el-header class="top-banner">Header</el-header>
      <el-container class="main-body">
        <el-aside class="aside">Aside</el-aside>
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
      background-color: red;
      height: 65px;
    }
    .main-body{

      .aside{
        background-color: blue;
        width: 250px;
      }
      .main{
        background-color: grey;

      }
    }
  }
}

</style>
