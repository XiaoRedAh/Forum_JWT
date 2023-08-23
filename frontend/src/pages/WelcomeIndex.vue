<script setup>

</script>

<template>
  <div class="container">
    <!--左侧壁纸固定-->
    <div class="left">
      <img style="width: 100%;height: 100%" fit="cover" src="@/assets/img/login.png"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到xiaoRed的网站</div>
      <div style="margin-top: 10px">水一段话啦啦啦啦啦啦</div>
      <div style="margin-top: 5px">再水一段话啦啦啦啦啦啦</div>
    </div>
    <!--右侧：根据路由展示登录/注册/重置密码组件-->
    <div class="right">
      <router-view v-slot="{Component}">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"></component>
        </transition>
      </router-view>
    </div>

  </div>
</template>


<style scoped>
.container {
  height: 100vh;
  overflow: hidden;
  display: flex;
}

.left {
  flex: 1;
}

.right {
  width: 450px;
  text-align: center;
  background-color: var(--el-bg-color);
  z-index: 1
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>
