<script setup>
import {reactive, ref} from "vue";
import {User,Lock} from '@element-plus/icons-vue';
import {login} from "@/net/index.js";
import router from "@/router/index.js";

const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rule = {
  username: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ]
}

function userLogin(){
  formRef.value.validate((valid)=>{
    if(valid){
      login(form.username, form.password, form.remember,
          ()=>{router.push('/home')})
    }
  })
}

</script>

<template>
  <div class="container">
    <!--标题-->
    <div style="margin-top: 200px">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color:grey">输入用户名和密码进行登录</div>
    </div>
    <!--表单-->
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" style="margin-top: 20px" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <!--记住我，忘记密码-->
    <div style="margin-top: 10px">
      <el-row>
        <el-col :span="12" style="text-align: left">
          <el-checkbox v-model="form.remember" label="记住我" size="large" />
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-link @click="router.push('/reset')">忘记密码？</el-link>
        </el-col>
      </el-row>
    </div>
    <!--登录/注册按钮-->
    <div style="margin-top: 40px">
      <el-button @click="userLogin" style="width: 270px" type="success" plain>登录</el-button>
    </div>
    <el-divider>
      <span style="color: grey;font-size: 13px">没有账号</span>
    </el-divider>
    <div>
      <el-button @click="router.push('/register')" style="width: 270px" type="warning" plain>注册账号</el-button>
    </div>
  </div>
</template>

<style scoped>
.container{
  display: block;
  text-align: center;
  margin: 0 20px
}

</style>
