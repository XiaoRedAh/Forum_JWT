<script setup>
import {useStore} from "@/store";

const store = useStore()//用户信息存储在这个全局变量中
import {Select, User} from "@element-plus/icons-vue";
import {onMounted, reactive, ref, computed} from "vue";
import {post, get} from "@/net";
import {ElMessage} from "element-plus";

const registerTime = computed(() => new Date(store.user.registerTime).toLocaleString())

//校验“用户名输入框”不能有特殊字符
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'));
  } else {
    //用正则表达式判断更方便。（以下正则表达式含义：包含中文英文的用户名，不能有特殊字符）
    if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
      callback(new Error('用户名不能包含特殊字符，只能是中文/英文'));
    } else {
      callback();
    }
  }
};

const validatePhone = (rule, value, callback) => {
  if (value !== '' && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式错误'));
  } else {
    callback();
  }
};

//制定校验规则
const rules = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 8, message: '用户名长度必须在2~8个字符之间', trigger: ['blur', 'change']}
  ],
  phone: [
    {validator: validatePhone, trigger: ['blur', 'change']},
    {min: 11, max: 11, message: '手机号只能是11位', trigger: ['blur', 'change']}
  ],
  qq: [
    {min: 8, max: 11, message: 'qq号只能是8~11位', trigger: ['blur', 'change']}
  ],
  wx: [
    {min: 5, max: 30, message: '微信号只能是5~30位', trigger: ['blur', 'change']}
  ],
  desc: [
    {max: 500, message: '个人简介不能超过500字', trigger: ['blur', 'change']}
  ]
}

//用户信息卡片中的简介
const desc = ref('')
const baseFormRef = ref()

/*"账号信息设置"表单的各个参数*/
const baseForm = reactive({
  username: null,
  gender: 0,
  phone: '',
  qq: '',
  wx: '',
  desc: '',
})

//加载该页面时，获取用户详细信息
onMounted(() => {
  if (baseForm.username == null){
    get('/api/user/details', (data) =>{
      baseForm.username = store.user.username
      baseForm.gender = data.gender
      baseForm.phone = data.phone
      baseForm.qq = data.qq
      baseForm.wx = data.wx
      baseForm.desc = data.desc
      desc.value = data.desc
    })
  }
})

//保存用户详细信息
const save = () => {
  baseFormRef.value.validate((isValid) => {
    if (isValid) {
      post('/api/user/save-details', baseForm, ()=>{
        //成功修改了用户详细信息，那就需要重新获取它们，更新到前端
        get('/api/user/details', (data) =>{
          baseForm.username = store.user.username
          baseForm.gender = data.gender
          baseForm.phone = data.phone
          baseForm.qq = data.qq
          baseForm.wx = data.wx
          baseForm.desc = data.desc
        })
        //还需要将store中存的用户名也更新
        store.user.username =  baseForm.username
        //将另一个卡片的desc也同步更新
        desc.value = baseForm.desc
        ElMessage.success('账号信息修改成功')
      })
    } else {
      ElMessage.warning('表单内容有误，请重新检查表单内容')
    }
  })
}

</script>

<template>
  <div class="page-container">
    <div class="left">
      <el-card class="setting-card">
        <template #header>
          <div class="card-header">
            <div style="font-size: 18px;font-weight: bold;">
              <el-icon>
                <user/>
              </el-icon>
              账号信息设置
            </div>
            <div style="font-size: 14px;color: grey;margin-top: 5px">
              在这里编辑您的个人信息，您可以在隐私设置中选择是否展示这些信息
            </div>
          </div>
        </template>
        <div class="card-bottom">
          <el-form
              ref="baseFormRef"
              :rules="rules"
              label-position="top"
              label-width="100px"
              :model="baseForm"
              style="max-width: 800px"
          >
            <el-form-item prop="username" label="用户名">
              <el-input :maxlength="8" v-model="baseForm.username"/>
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="baseForm.gender" class="ml-4">
                <el-radio :label="0" size="large">男</el-radio>
                <el-radio :label="1" size="large">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="phone" label="手机号">
              <el-input :maxlength="11" v-model="baseForm.phone"/>
            </el-form-item>
            <el-form-item label="QQ号" prop="qq">
              <el-input :maxlength="11" v-model="baseForm.qq"/>
            </el-form-item>
            <el-form-item label="微信号" prop="wx">
              <el-input :maxlength="30" v-model="baseForm.wx"/>
            </el-form-item>
            <el-form-item label="个人简介" prop="desc">
              <el-input :maxlength="500" type="textarea" v-model="baseForm.desc" :rows="6"/>
            </el-form-item>
          </el-form>
          <el-button type="success" :icon="Select" @click="save">保存个人信息设置</el-button>
        </div>
      </el-card>
    </div>
    <div class="right">
      <el-card class="desc-card">

          <div class="card-header">
            <div style="font-size: 18px;font-weight: bold;">
              <el-avatar
                  size="large"
                  src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
              />
              <div>你好，{{store.user.username}}</div>
            </div>
          </div>
        <el-divider style="margin: 10px 0 0 0"></el-divider>
        <div class="card-bottom">
          {{desc || '这个用户很懒，没有填写个人简介~'}}
        </div>
      </el-card>
      <el-card class="registerTime-card">
        <div style="font-size: 16px;font-weight: bold;">
          账号注册时间：{{registerTime}}
        </div>
        <div style="font-size: 12px;color: grey;margin-top: 5px">欢迎加入我们的论坛</div>
      </el-card>
    </div>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-card__body){
  padding: 0;
}

.page-container {
  display: flex;
  .left {
    flex: 1;
    .setting-card {
      margin-top: 30px;
      margin-left: 30px;
      border-radius: 10px;
      border: 1px solid var(--el-border-color);
      min-height: 20px;
      box-sizing: border-box;
      .card-header {
        padding: 10px 10px 5px 3px;
      }
      .card-bottom{
        padding: var(--el-card-padding);
      }
    }
  }

  .right {
    position: sticky;
    top: 20px;
    display: flex;
    flex-direction: column;
    width: 350px;
    margin-left: 60px;
    margin-right: 60px;

    .desc-card {
      margin-top: 30px;
      border-radius: 10px;
      border: 1px solid var(--el-border-color);
      .card-header{
        text-align: center;
        padding-top: 10px;
      }
      .card-bottom{
        color: grey;
        padding: 15px;
      }
    }

    .registerTime-card {
      border-radius: 10px;
      border: 1px solid var(--el-border-color);
      padding: var(--el-card-padding);
      margin-top: 20px;
    }
  }
}

</style>
