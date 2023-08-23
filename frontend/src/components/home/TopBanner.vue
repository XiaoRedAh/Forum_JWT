<script setup>
import {SwitchButton, User} from '@element-plus/icons-vue';
import {get, logout} from "@/net/index.js";
import router from "@/router/index.js";
import {useStore} from "@/store/index.js";

const userLogout = ()=>{
  logout(()=>router.push('/'))
}

const store = useStore()

get('/api/user/info', (data)=>{
  store.user = data
})

</script>

<template>
  <div class="top-container">
    <div class="logo">
      <img src="https://element-plus.org/images/element-plus-logo.svg">
    </div>
    <div class="user-info">
      <div class="profile">
        <div style="font-size: 20px;font-weight: bold;">{{store.user.username}}</div>
        <div style="font-size: 14px;color: grey">{{store.user.email}}</div>
      </div>
      <div class="avatar">
        <el-dropdown>
          <el-avatar
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
          />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="User">个人信息</el-dropdown-item>
              <el-dropdown-item :icon="SwitchButton" @click="userLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
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
    margin-right: 25px;
    display: flex;
    .profile{
      margin-right: 15px;
      margin-top: 15px;
      text-align: right;
    }
    .avatar{
      margin-top: 10px;
      :hover{
        cursor: pointer;
      }
    }
  }
}

</style>
