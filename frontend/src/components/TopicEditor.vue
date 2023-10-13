<script setup>
import {reactive} from "vue";
import {Document, Check} from "@element-plus/icons-vue";
import {QuillEditor} from '@vueup/vue-quill'
import  '@vueup/vue-quill/dist/vue-quill.snow.css'
defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

//帖子文章相关字段
const article = reactive({
  type: null,
  title: '',
  text: ''

})

//主题类型
const types = [
  {id: 1, name: '日常闲聊', desc: '在这里分享你的日常生活'},
  {id: 2, name: '真诚交友', desc: '在校园里寻找与你志同道合的朋友'},
  {id: 3, name: '问题反馈', desc: '反馈你在学校遇到的问题'},
  {id: 4, name: '恋爱专题', desc: '向大家分享你的恋爱故事'},
  {id: 5, name: '踩坑记录', desc: '将你遇到的坑分享给大家，防止其他人再次入坑'}
]
</script>

<template>
  <div>
    <el-drawer :model-value="show" direction="btt"
               :size="650" :close-on-click-modal="false"
                @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的主题</div>
          <div style="font-size: 13px">发表主题之前，请仔细阅读相关法律法规，不要出现违禁词，不文明等违规行为</div>
        </div>
      </template>
      <!--主体类型 + 标题-->
      <div style="display: flex;gap: 10px">
        <!--选择主题类型-->
        <div>
          <el-select placeholder="选择主题类型..." v-model="article.type">
            <el-option v-for="item in types" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </div>
        <!--填写标题-->
        <div style="flex: 1">
          <el-input v-model="article.title" placeholder="请输入帖子标题..." :prefix-icon="Document"></el-input>
        </div>
      </div>
      <!--富文本编辑器，这里使用quill-->
      <div style="margin-top: 15px;height: 83%;overflow: hidden">
        <quill-editor v-model="article.text" style="height: calc(100% - 41px)" placeholder="今天想分享点什么呢？"></quill-editor>

      </div>
      <!--字数统计 + 发表按钮-->
      <div style="display: flex;justify-content: space-between;margin-top: 10px">
        <div style="color: grey;font-size: 13px">
          当前字数 425（最大支持20000字）
        </div>
        <div>
          <el-button type="success" :icon="Check" plain>立即发表帖子</el-button>
        </div>
      </div>
    </el-drawer>
  </div>

</template>

<style scoped>
:deep(.el-drawer){
  width: 50%;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header){
  margin: 0;
}

:deep(.ql-toolbar){
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container){
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor.ql-blank::before){
  color: var(--el-text-color-placeholder);
  font-style: normal;
}

:deep(.ql-editor){
  font-size: 14px;
}
</style>