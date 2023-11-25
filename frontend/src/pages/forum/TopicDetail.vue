<script setup>

import {
  ArrowLeft, CircleCheck,
  Female, Male, Plus, Star,
} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {get} from "@/net/index.js"
import { QuillDeltaToHtmlConverter} from 'quill-delta-to-html';

import router from "@/router/index.js";
import axios from "axios";
import {useRoute} from "vue-router";
import TopicTag from "@/components/home/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import TopicCommentEditor from "@/components/TopicCommentEditor.vue";

const route = useRoute()

const tid = route.params.tid

const topic = reactive({
  data: null,
  like:false,
  collect:false,
  comments:null,
  pageNum: 1
})

//加载帖子列表以及评论
function init(){
  get(`api/forum/topic?tid=${tid}`, data=>{
    topic.data = data
    topic.like = data.interact.like
    topic.collect = data.interact.collect
    loadComment(1)
  })
}
init()


//把接收到的content【可以是帖子内容，评论内容,...】，通过插件转换为html代码
function convertToHtml(content){
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true});
  return converter.convert();
}

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

const comment = reactive({
  show: false, //控制评论编辑器展示
  text: '', //评论内容
  quote: -1 //默认没有引用其他评论
})

//根据页号加载评论，后端页号从0开始，因此请求时page-1
function loadComment(pageNum){
  topic.comments = null //重置评论
  topic.pageNum = pageNum
  get(`/api/forum/comments?tid=${tid}&pageNum=${pageNum-1}`, data=>topic.comments = data)
}

//发表评论时触发
function onCommentAdd(){
  comment.show = false //评论编辑器关闭
  //加评论后，默认到新评论那一页，也就是最后一页；向下取整再加1，而且这里的评论数还是原来的，需要++
  loadComment(Math.floor(++topic.data.comments/10) + 1)
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
        <div class="topic-content" v-html="convertToHtml(topic.data.content)"></div>
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
    <!--评论展示区-->
    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if="topic.comments">
        <!--评论列表-->
        <div class="topic-main" style="margin-top: 10px" v-for="item in topic.comments">
          <!--评论卡片左部展示评论者信息-->
          <div class="topic-main-left">
            <el-avatar :src="axios.defaults.baseURL + '/images' + item.user.avatar" :size="60"/>
            <div>
              <div style="font-size: 18px;font-weight: bold">
                {{topic.data.user.username}}
              </div>
              <span style="color: hotpink" v-if="item.user.gender === 1"><el-icon><Female/></el-icon></span>
              <span style="color: dodgerblue" v-if="item.user.gender === 0"><el-icon><Male/></el-icon></span>
              <div class="desc">{{item.user.email}}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div style="text-align: left;margin: 0 5px">
              <div class="desc">微信号：{{item.user.wx || '已隐藏或未填写'}}</div>
              <div class="desc">QQ号：{{item.user.qq || '已隐藏或未填写'}}</div>
              <div class="desc">手机号：{{item.user.phone || '已隐藏或未填写'}}</div>
            </div>
          </div>
          <!--评论卡片右部展示评论日期和内容-->
          <div class="topic-main-right">
            <div style="font-size: 13px;color:grey;">
              <div>评论时间：{{new Date(item.time).toLocaleString()}}</div>
            </div>
            <div class="topic-content" v-html="convertToHtml(item.content)"></div>
          </div>
        </div>
        <!--评论分页器-->
        <div style="width: fit-content;margin: 20px auto">
          <el-pagination background layout="prev, pager, next"
                         v-model:current-page="topic.pageNum" @current-change="loadComment"
                        :total="topic.data.comments" :page-size="10"/>
        </div>
      </div>
    </transition>
    <!--发表评论组件-->
    <topic-comment-editor :show="comment.show" @close="comment.show=false" @comment="onCommentAdd"
                          :tid="tid" :quote="comment.quote"/>
    <!--控制发表评论编辑器展示的按钮-->
    <div class="add-comment" @click=" comment.show= true">
      <el-icon><Plus/></el-icon>
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

.add-comment{
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: var(--el-color-primary);
  text-align: center;
  line-height: 45px;
  background: var(--el-bg-color-overlay);
  background: car(--el-box-shadow-lighter);

  &:hover{
    cursor: pointer;
    background: var(--el-border-color-extra-light);
  }
}
</style>