<script setup>
import {useStore} from "@/store";

const store = useStore()//用户信息存储在这个全局变量中
import {Select, User, Message, Setting, Switch, Lock} from "@element-plus/icons-vue";
import {onMounted, reactive, ref, computed} from "vue";
import {post, get} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const privacyForm = reactive({
  email: false,
  phone: false,
  qq: false,
  wx: false,
  blog: false,
  sex: false
})

const save = ()=>{
  post('/api/user/save-privacy', privacyForm,()=>ElMessage.success("隐私设置保存成功！"),'json')
}
//展示现在有的隐私
onMounted(()=>{
  get('/api/user/privacy', message=>{
    privacyForm.email = message.email
    privacyForm.phone = message.phone
    privacyForm.qq = message.qq
    privacyForm.wx = message.wx
    privacyForm.blog = message.blog
    privacyForm.sex = message.sex
  })
})

const securityForm = reactive({
  email: '',
  password_old: '',
  password_new: '',
  password_new_repeat: ''
})

const emailForm = ref()
const saveEmail = ()=>{
  emailForm.value.validate((isValid)=>{
    if(isValid){
      post('/api/user/save-email',{email: securityForm.email},
          ()=>{
            //邮箱修改成功后，重新获取用户信息，使得新邮箱马上能呈现在页面上
            get('/api/user/me',(message)=>{
              //获取成功，就将用户信息存储在前端，然后才跳转到index

              localStorage.setItem("user", JSON.stringify(message))//存在localStorage永久存储
            },()=>{

            })
            ElMessage.success("保存成功！")
          })
    }else{
      ElMessage.warning("邮箱格式有误，请正确填写")
    }
  })
}

const passwordForm = ref()
const changePassword = ()=>{
  passwordForm.value.validate((isValid)=>{
    if(isValid){
      post('/api/user/change-password',{
        old: securityForm.password_old,
        new: securityForm.password_new
      },()=>{
        ElMessage.success("密码修改成功，请重新登录")
        //修改成功后，会执行退出登录的操作
        get('/api/auth/logout',(message) =>{//退出登录成功
          // 先把用户的登录状态清空，才能成功返回到登录页面（配置了路由守卫，如果不清空，前端还是认为你是登录状态，回不到登录页面）
          ElMessage.success(message)
          localStorage.removeItem('user')//将localStorage存储的用户信息也删掉
          router.push('/')//跳转回登录页面
        })
      })
    }else{
      ElMessage.warning("密码校验失败，请检查是否正确填写")
    }
  })
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== securityForm.password_new) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};
//制定校验规则
const rules ={
  password_old: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur','change'] }
  ],
  password_new: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur','change'] }
  ],
  password_new_repeat: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: ['blur','change'] },
  ],
  email:[
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入合法的电子邮箱地址', trigger: ['blur', 'change'] }
  ],
}


</script>

<template>
  <div class="page-container">
    <!--隐私设置卡片-->
    <el-card class="privacy-card">
      <div class="card-header">
        <div style="font-size: 18px;font-weight: bold;"><el-icon><Setting/></el-icon>隐私设置</div>
        <div style="font-size: 14px;color: grey;margin-top: 5px">在此处设置哪些内容可以被其他人看到，请注重您的个人隐私</div>
      </div>
      <el-divider style="margin: 10px 0 0 0"></el-divider>
      <div class="card-bottom">
        <div class="checkbox-list">
          <el-checkbox v-model="privacyForm.email" label="公开展示我的邮箱" size="large" />
          <el-checkbox v-model="privacyForm.sex" label="公开展示我的性别" size="large" />
          <el-checkbox v-model="privacyForm.phone" label="公开展示我的电话号码" size="large" />
          <el-checkbox v-model="privacyForm.qq" label="公开展示我的QQ号" size="large" />
          <el-checkbox v-model="privacyForm.wx" label="公开展示我的微信号" size="large" />
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
            ref="passwordForm"
            :rules="rules"
            label-position="left"
            label-width="100px"
            :model="securityForm"
            style="margin-top: 10px"
        >
          <el-form-item label="原密码" prop="password_old">
            <el-input type="password" :prefix-icon="Lock" placeholder="原密码" show-password v-model="securityForm.password_old"  maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="password_new">
            <el-input  type="password" :prefix-icon="Lock" placeholder="新密码" show-password v-model="securityForm.password_new" maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="password_new_repeat">
            <el-input  type="password" :prefix-icon="Lock" placeholder="重复新密码" show-password v-model="securityForm.password_new_repeat" maxlength="16" />
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
//:deep(.el-card__body){
//  padding: 0;
//}

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
