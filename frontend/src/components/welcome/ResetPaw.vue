<script setup>
import {reactive, ref} from "vue";
import {Lock, Message, EditPen} from "@element-plus/icons-vue";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";

const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
})

//步骤条进行到第几步
const active = ref(0)

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const rules = {
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须在6~16个字符之间', trigger: ['blur','change'] }
  ],
  password_repeat: [
    { validator: validatePassword, trigger: ['blur','change'] },
  ],
  email:[
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入合法的电子邮箱地址', trigger: ['blur', 'change'] }
  ],
  code: [
    { required: true, message: '请输入获取的验证码', trigger: 'blur' },
    {min: 6, max: 6, message: '请输入6位验证码', trigger: ['blur', 'change']}
  ]
}

//定义对整个表单进行响应的变量
const formRef = ref()
//判断邮箱地址是否有效（默认无效），有效才能激发“获取验证码”按钮
const isEmailValid = ref(false)
//发送验证码的冷却时间
const coldTime = ref(0)

const onValidate= (prop, isValid) =>{
  //如果更新的属性是email
  if(prop === 'email')
    isEmailValid.value = isValid//isVaild返回的是该属性是否校验通过
}

//发送验证码
const validateEmail = ()=>{
  //按钮点击后立即冷却60秒，防止点太快，重复发
  coldTime.value = 60
  get(`/api/auth/ask-code?email=${form.email}&type=register`, ()=>{
    ElMessage.success(`验证码已发送到邮箱: ${form.email}，请注意查收`)
    setInterval(() => coldTime.value--, 1000)//每一秒，冷却时间减1
  },undefined, (message)=>{
    ElMessage.warning(message)
    coldTime.value = 0 //如果出现了些问题，冷却时间直接清空
  })
}

//重置密码第一步：认证邮箱验证码
const startReset = ()=>{
  formRef.value.validate((isValid) =>{
    //只有整个el-form表单完整无误，才能向后端发送请求
    if(isValid){
      post("/api/auth/reset-confirm",{
        email: form.email,
        code: form.code
      },()=>{
        //如果邮箱验证成功，进入第二步
        active.value++
      })
    }else{//填写表单有误，不能向后端发送post请求
      ElMessage.warning('请填写电子邮箱地址和验证码')
    }
  })
}

//重置密码第二步：重置密码
const doReset = ()=>{
  formRef.value.validate((isValid) =>{
    //只有整个el-form表单完整无误，才能向后端发送请求
    if(isValid){
      post("/api/auth/reset-password",{
        email: form.email,
        code: form.code,
        password: form.password,
      },(message)=>{
        //如果重置密码成功，页面切换到登录界面
        ElMessage.success("重置密码成功，请重新登录")
        router.push('/')
      })
    }else{//填写表单有误，不能向后端发送post请求
      ElMessage.warning('请填写新的密码并确认两次填写是否一致')
    }
  })
}
</script>

<template>
  <div>
    <div style="margin: 40px 20px;">
      <!--搞个步骤条，active绑定进行到第几步-->
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮箱" />
        <el-step title="重新设置密码" />
      </el-steps>
    </div>
    <!--active为0的时候显示验证电子邮箱的界面-->
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 0">
        <div style="margin-top: 150px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color:grey">请输入需要重置密码的电子邮箱地址</div>
        </div>
        <div>
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="email">
              <el-input v-model="form.email" type="email" style="margin-top: 40px" placeholder="电子邮箱地址">
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="code">
              <div style="margin-top: 20px">
                <el-row :gutter="10" style="width: 100%">
                  <el-col :span="18">
                    <el-input v-model="form.code" type="text" placeholder="请输入验证码">
                      <template #prefix>
                        <el-icon><EditPen /></el-icon>
                      </template>
                    </el-input>
                  </el-col>
                  <el-col :span="6">
                    <el-button type="success" :maxlength="6" @click="validateEmail"
                               :disabled="!isEmailValid || coldTime > 0" >
                      {{coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码'}}</el-button>
                  </el-col>
                </el-row>
              </div>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button style="width: 270px;" type="danger" plain @click="startReset">开始重置密码</el-button>
        </div>
      </div>
    </transition>
    <!--active为1的时候，显示重置密码界面-->
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 1">
        <div style="margin-top: 150px">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color:grey">请填写您的新密码</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="password">
              <el-input v-model="form.password" :maxlength="16" type="password" style="margin-top: 20px" placeholder="新密码">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" :maxlength="16" type="password" style="margin-top: 20px" placeholder="再次输入新密码">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button style="width: 270px;" type="danger" plain @click="doReset">立即重置密码</el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>

</style>
