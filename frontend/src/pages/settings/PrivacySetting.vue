<script setup>
import {useStore} from "@/store";

import {Setting, Switch, Lock} from "@element-plus/icons-vue";
import {onMounted, reactive, ref, computed} from "vue";
import {post, get, logout} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

//隐私设置
const privacy = reactive({
  email: false,
  phone: false,
  qq: false,
  wx: false,
  gender: false
})

const saving = ref(true)

//展示现在有的隐私
onMounted(()=>{
  get('/api/user/privacy', message=>{
    privacy.email = message.email
    privacy.phone = message.phone
    privacy.qq = message.qq
    privacy.wx = message.wx
    privacy.gender = message.gender
    saving.value = false
  })
})


function savePrivacy(type, status){
  saving.value = true
  post('/api/user/save-privacy', {
    type: type,
    status: status
  },()=>{
    ElMessage.success("隐私设置保存成功！")
    saving.value = false
  }
  )
}

const formRef = ref()
const valid = ref(false)
const onValidate = (prop, isValid) => valid.value = isValid
const changePawForm = reactive({
  password_old: '',
  password_new: '',
  password_new_repeat: ''
})


const changePassword = ()=>{
  formRef.value.validate((valid)=>{
    if(valid){
      post('/api/user/change-password',{
        password_old: changePawForm.password_old,
        password_new: changePawForm.password_new
      },()=>{
        ElMessage.success("密码修改成功，请重新登录")
        //直接使用分装好的logout函数退出登录
        logout(()=>router.push('/'))
      })
    }else{
      ElMessage.warning("密码校验失败，请检查是否正确填写")
    }
  })
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== changePawForm.password_new) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};
//制定校验规则
const rules ={
  password_old: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur'] }
  ],
  password_new: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur'] }
  ],
  password_new_repeat: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: ['blur','change'] },
  ],
}
</script>

<template>
  <div class="page-container">
    <!--隐私设置卡片-->
    <el-card class="privacy-card" v-loading="saving">
      <div class="card-header">
        <div style="font-size: 18px;font-weight: bold;"><el-icon><Setting/></el-icon>隐私设置</div>
        <div style="font-size: 14px;color: grey;margin-top: 5px">在此处设置哪些内容可以被其他人看到，请注重您的个人隐私</div>
      </div>
      <el-divider style="margin: 10px 0 0 0"></el-divider>
      <div class="card-bottom">
        <div class="checkbox-list">
          <el-checkbox v-model="privacy.email" label="公开展示我的邮箱" @change="savePrivacy('email', privacy.email)"/>
          <el-checkbox v-model="privacy.gender" label="公开展示我的性别" @change="savePrivacy('gender', privacy.gender)"/>
          <el-checkbox v-model="privacy.phone" label="公开展示我的电话号码" @change="savePrivacy('phone', privacy.phone)"/>
          <el-checkbox v-model="privacy.qq" label="公开展示我的QQ号" @change="savePrivacy('qq', privacy.qq)"/>
          <el-checkbox v-model="privacy.wx" label="公开展示我的微信号" @change="savePrivacy('wx', privacy.wx)"/>
        </div>
      </div>
    </el-card>
    <!--重置密码设置-->
    <el-card class="reset-card">
      <div class="card-header">
        <div style="font-size: 18px;font-weight: bold;"><el-icon><Setting/></el-icon>密码设置</div>
        <div style="font-size: 14px;color: grey;margin-top: 5px">修改密码请在此处操作，请务必牢记您的密码</div>
      </div>
      <el-divider style="margin: 10px 0 0 0"></el-divider>
      <div class="card-bottom">
        <el-form
            ref="formRef"
            :rules="rules"
            label-position="left"
            label-width="100px"
            :model="changePawForm"
            @validate="onValidate"
            style="margin-top: 10px"
        >
          <el-form-item label="原密码" prop="password_old">
            <el-input type="password" :prefix-icon="Lock" placeholder="原密码" show-password v-model="changePawForm.password_old"  maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="password_new">
            <el-input  type="password" :prefix-icon="Lock" placeholder="新密码" show-password v-model="changePawForm.password_new" maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="password_new_repeat">
            <el-input  type="password" :prefix-icon="Lock" placeholder="重复新密码" show-password v-model="changePawForm.password_new_repeat" maxlength="16" />
          </el-form-item>
        </el-form>
        <div class="reset-button">
          <el-button type="success" :icon="Switch" @click="changePassword">重置密码</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style lang="less" scoped>

.page-container {
  margin: auto;
  max-width: 650px;
  .privacy-card{
    margin-top: 30px;
    border-radius: 10px;
    border: 1px solid var(--el-border-color);
    .card-bottom{
      .checkbox-list{
        display: flex;
        flex-direction: column;
      }
    }
  }
  .reset-card{
    margin-top: 20px;
    border-radius: 10px;
    border: 1px solid var(--el-border-color);
    .card-bottom{
      .reset-button{
        text-align: center
      }
    }
  }

}

</style>
