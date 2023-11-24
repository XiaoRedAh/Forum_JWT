<script setup>
import {
 Check
} from "@element-plus/icons-vue";
import {QuillEditor} from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {post} from "@/net"
import {ref} from "vue"
import {ElMessage} from "element-plus";

//这三个属性需要外部传进来
const props = defineProps({
  show: Boolean,
  tid: Number,
  quote: Number
})
const emit = defineEmits(['close'])
const content = ref() //评论内容

//发表评论
function submitComment(){
  post('/api/forum/add-comment', {
    tid: props.tid,
    quote: props.quote,
    content: JSON.stringify(content.value) //转成string
  }, ()=>{
    ElMessage.success('发表评论成功')
    emit('close') //自动关闭
  })
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" @close="emit('close')" direction="btt" :size="270"
               :close-on-click-modal="false" title="发表评论">
      <div>
        <div>
          <quill-editor style="height: 120px" v-model:content="content"
                        placeholder="请发表友善的评论，注意文明用语"/>
        </div>
        <div style="margin-top: 10px;text-align: right">
          <el-button type="success" :icon="Check" @click="submitComment" plain>发表评论</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer) {
  width: 50%;
  margin: 20px auto;
  border-radius: 10px;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.el-drawer__body) {
  padding: 10px;
}
</style>