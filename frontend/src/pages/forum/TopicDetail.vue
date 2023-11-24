<script setup>

import {
  ArrowLeft, CircleCheck,
  Female, Male, Star,
} from "@element-plus/icons-vue";
import {computed, reactive} from "vue";
import {get} from "@/net/index.js"
import { QuillDeltaToHtmlConverter} from 'quill-delta-to-html';

import router from "@/router/index.js";
import axios from "axios";
import {useRoute} from "vue-router";
import TopicTag from "@/components/home/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";

const route = useRoute()

const tid = route.params.tid

const topic = reactive({
  data: null,
  like:false,
  collect:false,
  comments:[]
})

get(`api/forum/topic?tid=${tid}`, data=>{
  topic.data = data
  topic.like = data.interact.like
  topic.collect = data.interact.collect
})


//把从后端拿到的文章内容，通过插件转换为html代码
const content = computed(()=>{
  const ops = JSON.parse(topic.data.content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true});
  return converter.convert();
})

//点赞/取消点赞/收藏/取消收藏
function interact(type, message){
  get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`, ()=>{
    topic[type] = !topic[type]
    if(topic[type])
      ElMessage.success(`${message}成功！`)
    else
      ElMessage.success(`已取消${message}！`)
  })
}
</script>

<template>
  <!--查询到帖子列表，才加载-->
  <div class="topic-page" v-if="topic.data">
    <!--顶部固定布局，展示“返回列表”按钮+帖子标题+帖子类型-->
    <div class="topic-main" style="position: sticky;top: 0;z-index: 10">
      <el-card style="display: flex;width: 100%">
        <el-button :icon="ArrowLeft" type="info" size="small"
        plain round @click="router.push('/home')">返回列表</el-button>
        <div style="text-align: center;flex: 1">
          <topic-tag :type="topic.data.type"></topic-tag>
          <span style="font-weight: bold;margin-left: 5px;font-size: 20px">{{topic.data.title}}</span>
          <div style="margin-top: 5px;font-size: 13px;color: grey">
            发帖时间：{{new Date(topic.data.time).toLocaleString()}}
          </div>
        </div>
      </el-card>
    </div>
    <!--主体部分分为左右两侧，左侧展示作者的信息，右侧展示帖子信息-->
    <div class="topic-main">
      <!--左侧：作者信息-->
      <div class="topic-main-left">
        <el-avatar :src="axios.defaults.baseURL + '/images' + topic.data.user.avatar" :size="60"/>
        <div>
          <div style="font-size: 18px;font-weight: bold">
            {{topic.data.user.username}}
          </div>
          <span style="color: hotpink" v-if="topic.data.user.gender === 1">
            <el-icon><Female/></el-icon>
          </span>
          <span style="color: dodgerblue" v-if="topic.data.user.gender === 0">
            <el-icon><Male/></el-icon>
          </span>
          <div class="desc">{{topic.data.user.email}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="text-align: left;margin: 0 5px">
          <div class="desc">微信号：{{topic.data.user.wx || '已隐藏或未填写'}}</div>
          <div class="desc">QQ号：{{topic.data.user.qq || '已隐藏或未填写'}}</div>
          <div class="desc">手机号：{{topic.data.user.phone || '已隐藏或未填写'}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="desc" style="margin: 0 5px">{{topic.data.user.desc}}</div>
      </div>
      <!--右侧：主要是展示帖子主体，附带一些零碎的小功能-->
      <div class="topic-main-right">
        <!--利用插件将后端的帖子内容转换为html格式的content，通过v-html展示-->
        <div class="topic-content" v-html="content"></div>
        <!--点赞，收藏按钮-->
        <div style="text-align: right; margin-top: 30px">
          <interact-button name="点个赞吧" check-name="已点赞" color="pink" :check="topic.like"
                           @check="interact('like', '点赞')">
            <el-icon><CircleCheck/></el-icon>
          </interact-button>
          <interact-button name="收藏本贴" check-name="已收藏" color="orange" :check="topic.collect"
                           @check="interact('collect', '收藏')">
            <el-icon><Star/></el-icon>
          </interact-button>
        </div>
      </div>
    </div>
    <div>

    </div>
  </div>

</template>

<style lang="less" scoped>

:deep(.el-card__body) {
  display: flex;
  align-items: center;
  background-color: var(--el-bg-color);
  width: 100%;
  padding: 20px 10px;
}

.topic-page{
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}
.topic-main{
  display: flex;
  border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 1200px;

  .topic-main-left{
    width: 20%;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);

    .desc{
      font-size: 12px;
      color: grey;
    }
  }
  .topic-main-right{
    width: 80%;
    padding: 10px 20px;

    .topic-content{
      font-size: 14px;
      line-height: 20px;
      opacity: 0.8; //字体太亮了，给加一点透明度
    }
  }
}
</style>