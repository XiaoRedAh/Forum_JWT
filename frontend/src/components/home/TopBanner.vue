<script setup>
import {SwitchButton, User, Search} from '@element-plus/icons-vue';
import {get, logout} from "@/net/index.js";
import router from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {reactive} from "vue";

const userLogout = ()=>{
  logout(()=>router.push('/'))
}

//加载用户信息
const store = useStore()

get('/api/user/info', (data)=>{
  store.user = data
})

//搜索框内容
const searchInput = reactive({
  type: '1',
  text: ''
})

</script>

<template>
  <div class="top-container">
    <div class="logo">
      <img src="https://element-plus.org/images/element-plus-logo.svg">
    </div>
    <div class="search-container">
      <el-input
          placeholder="搜索论坛相关内容..."
          class="search-input"
      >
        <template #prepend>
          <el-select v-model="searchInput.type" style="width: 115px">
            <el-option label="帖子广场" value="1" />
            <el-option label="校园活动" value="2" />
            <el-option label="表白墙" value="3" />
            <el-option label="教务通知" value="4" />
          </el-select>
        </template>
        <template #append>
          <el-button :icon="Search" />
        </template>
      </el-input>
    </div>
    <div class="user-info">
      <div class="profile">
        <div style="font-size: 20px;font-weight: bold;">{{store.user.username}}</div>
        <div style="font-size: 14px;color: grey">{{store.user.email}}</div>
      </div>
      <div class="avatar">
        <el-dropdown>
<!--          <el-avatar-->
<!--              :src="store.avatarUrl"-->
<!--          />-->
          <div>

          </div>
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
  .search-container{
    width: 550px;
    height: 100%;
    .search-input{
      width: 100%;
      height: 34px;
      margin-top: 15px;
    }
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
