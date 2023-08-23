<script setup>

</script>

<template>
  <div class="top-container">
    <div class="logo">
      <img src="https://element-plus.org/images/element-plus-logo.svg">
    </div>
    <div class="user-info">
      <div class="profile">
        <div style="font-size: 18px;font-weight: bold;">用户名</div>
        <div style="font-size: 12px;color: grey">电子邮件</div>
      </div>
      <div class="avatar">
        <el-avatar
            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
        />
      </div>
    </div>

  </div>
</template>

<style lang="less" scoped>
.top-container{
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .logo{
    width: 180px;
    height: 32px;
  }
  .user-info{
    display: flex;
    .profile{
      margin-right: 15px;
      margin-top: 15px;
      text-align: right;
    }
    .avatar{
      margin-top: 10px;
    }
  }
}

</style>
