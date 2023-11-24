<script setup>
import {get} from "@/net"
import {ref} from "vue"
import router from "@/router/index.js";
import LightCard from "@/components/LightCard.vue";
import TopicTag from "@/components/home/TopicTag.vue";
import {ElMessage} from "element-plus";
/*定义属性，外部传进来*/
defineProps({
  show: Boolean /*控制组件是否展示*/
})

const emit = defineEmits(['close']) /*配合收藏列表抽屉@close，关闭抽屉时触发*/
const list = ref([])

//给到@open，在收藏列表抽屉打开时调用，获取收藏列表
function init(){
  get('/api/forum/collects', data=>list.value = data)
}

//取消收藏
function deleteCollect(index, tid){
  get(`/api/forum/interact?tid=${tid}&type=collect&state=false`,()=>{
    ElMessage.success("已取消收藏！")
    list.value.splice(index, 1) //前端展示也要删除，从index处开始删除1个，也就是被删的那个
  })
}

</script>

<template>
  <el-drawer :model-value="show" @close="emit('close')" @open="init" title = "我的帖子收藏列表">
    <div class="collect-list">
      <light-card v-for="(item, index) in list" class="collect-card" @click="router.push(`/home/topic-detail/${item.id}`)">
        <topic-tag :type="item.type"/>
        <div class="title">
          <b>{{item.title}}</b>
        </div>
        <el-link type="danger" @click.stop="deleteCollect(index, item.id)">删除</el-link>
      </light-card>

    </div>
  </el-drawer>
</template>

<style lang="less" scoped>
.collect-list{
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.collect-card{
  background-color: rgba(128, 128, 128, 0.2);
  transition: .3s;
  display: flex;
  justify-content: space-between;

  .title{
    margin-left: 5px;
    font-size: 14px;
    flex: 1;
    white-space: nowrap; /*文本不会换行，直到遇到</br>*/
    display: block;
    overflow: hidden;
    text-overflow: ellipsis; /*装不下的字用省略号*/
  }
  
  &:hover{
    scale: 1.02;
    cursor: pointer;
  }
}

</style>