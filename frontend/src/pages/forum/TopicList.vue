<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  ArrowRightBold,
  Calendar, CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone,
  Picture, Star
} from "@element-plus/icons-vue";
import {computed, ref, reactive, watch} from "vue";
import {get} from "@/net/index.js"
import TopicEditor from "@/components/TopicEditor.vue";
import Weather from "@/components/Weather.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store/index.js";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/home/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";

const store = useStore()

//控制“编辑文章”的卡片是否弹出
let editor = ref(false)

//帖子列表相关属性
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  top_list: [],
  end: false //是否到最后了
})

//根据查询条件查询帖子列表，并且可以无限滚动，直到查到最后一个数据
function updateList(){
  if(topics.end) return
  get(`/api/forum/list-topic?page=${topics.page}&type=${topics.type}`, data => {
    if(data){
      data.forEach(d => topics.list.push(d))
      topics.page++
      topics.list = data
    }
    if(!data || data.length < 10)
      topics.end = true
  })
}

//重置帖子列表(但保留选择的类型)，并重新查询一次
function resetThenUpdateList(){
  topics.page = 0
  topics.list = []
  topics.end = false
  updateList()
}

//新帖子发布后，关闭TopicEditor组件，重置帖子列表(但保留选择的类型)，并重新查询一次
function onTopicCreate(){
  editor.value = false
  resetThenUpdateList()
}

//监听要查询的帖子列表的类型，一旦变化了，就重置帖子列表(但保留选择的类型)，并重新查询一次
//immediate表示一开始先获取一次
watch(() => topics.type, () => resetThenUpdateList(), {immediate: true})

//一加载到页面就先获取一次帖子列表
updateList()
//一加载到页面就先获取到置顶帖子
get('/api/forum/top-topic', data => topics.top_list = data)

//利用js内置的api得到当前日期
const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
})

//获取后端返回的天气信息
const weather = reactive({
  location: {},
  now_weather: {},
  hourly: [],
  success: false
})

//通过浏览器异步查询当前的经纬度信息，拿到后就去请求后端接口，获取天气信息
//谷歌浏览器的位置获取功能似乎被墙掉了，但其他浏览器都是可以的
navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data) //拷贝到weather变量
    weather.success = true
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时，请检测网络设置')
  //拿不到本地区的天气信息，就拿个北京的天气信息展示一下就行
  get(`/api/forum/weather?longitude=116.40529&latitude=39.90499`, data => {
    Object.assign(weather, data) //拷贝到weather变量
    weather.success = true
  })
}, {
  timeout: 5000, //5秒钟获取不到，就不获取了，报错,去拿默认的天气信息
  enableHighAccuracy: true //开启高精度
})

const collects = ref(false)//控制收藏列表是否展示
</script>

<template>
  <div class="container">
    <!--左侧主要展示帖子列表-->
    <div class="left">
      <!--最上面来个可以发表帖子的显示框-->
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon>
            <EditPen></EditPen>
          </el-icon>
          点击发表主题...
        </div>
        <div style="margin-top: 10px;display: flex;gap:13px;font-size: 18px;color: grey">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </light-card>
      <!--展示置顶的帖子-->
      <light-card style="margin-top: 10px;height: 50px;display: flex;flex-direction: column;gap: 10px">
        <div v-for="item in topics.top_list" class="topping-topic" @click="router.push(`/home/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleDateString()}}</div>
        </div>
      </light-card>
      <!--选择展示什么类型的帖子-->
      <light-card style="margin-top: 10px;display: flex;gap: 10px">
        <div v-for="item in store.forum.types"
            :class="`type-select ${topics.type === item.id ? 'active' : ''}`"
              @click="topics.type = item.id">
          <color-dot :color="item.color"/>
          <span>{{item.name}}</span>
        </div>
      </light-card>
      <!--剩下的空间展示帖子列表-->
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div class="topic-list-container" v-infinite-scroll="updateList">
            <light-card class="preview-card" v-for="item in topics.list" @click="router.push(`/home/topic-detail/${item.id}`)">
              <!--卡片上半部分展示些信息：头像，作者名字，创建时间，帖子类型，帖子标题-->
              <div>
                <!--头像 + 作者名字 + 创建时间-->
                <div style="display: flex">
                  <!--头像-->
                  <div>
                    <el-avatar :size="32" :src="`${axios.defaults.baseURL}/images${item.avatar}`"/>
                  </div>
                  <!--作者名字+创建时间-->
                  <div style="margin-left: 8px;transform: translateY(-1px)">
                    <div style="font-size: 14px;font-weight: bold">{{item.username}}</div>
                    <div style="font-size: 13px;color: grey">
                      <el-icon><Clock/></el-icon>
                      <span style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                    {{new Date(item.time).toLocaleString()}}</span>
                    </div>
                  </div>
                </div>
                <!--帖子类型 + 帖子标题-->
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold;margin-left: 7px;font-size: 18px">{{item.title}}</span>
              </div>
              <!--卡片下半部分展示：帖子内容预览 + 图片预览 + 点赞量收藏量展示-->
              <div class="preview-text">
                {{item.text}}
              </div>
              <div class="preview-image-container">
                <el-image class="preview-image" v-for="img in item.images" :src="img" fit="cover"></el-image>
              </div>
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px">
                <div>
                  <el-icon style="vertical-align: middle"><CircleCheck/></el-icon>{{item.like}}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><Star/></el-icon>{{item.collect}}收藏
                </div>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>

    <!--右侧装一些卡片丰富一下-->
    <div class="right">
      <div class="card-container">
        <!--我的收藏列表卡片-->
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened/></el-icon>查看我的收藏</span>
            <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
          </div>
        </light-card>
        <!--论坛公告卡片-->
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <div style="font-size: 14px;margin: 10px;color: grey">
            hfwojfiownvwvnowjvoiw,fewfwgebw
            wfewvevewovnweojigefffffffhoijavnojajpv
            afasfasafwefdv fafav
            faeegggggggggggggggggggggggggggggeeee
          </div>
        </light-card>
        <!--天气卡片-->
        <light-card style="margin-top: 10px">
          <div>
            <div style="font-weight: bold">
              <el-icon>
                <Calendar/>
              </el-icon>
              天气信息
            </div>
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <!--天气详情单独封装为一个组件-->
          <weather :data="weather"/>
        </light-card>
        <!--展示日期及ip地址卡片-->
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{ today }}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <!--展示友链-->
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <el-icon>
            <Link/>
          </el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div class="link-container">
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/js-design-banner.jpg"></el-image>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/vform-banner.png"></el-image>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"></el-image>
          </div>
        </div>
      </div>
    </div>
    <!--发表主题卡片，通过editor变量，控制它是否弹出-->
    <topic-editor :show="editor" @success="onTopicCreate" @close="editor = false"></topic-editor>
    <!--收藏列表篇卡片，通过collects控制它是否弹出-->
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>
</template>

<style lang="less" scoped>
.container {
  display: flex;
  margin: 20px auto;
  gap: 20px;
  max-width: 1300px;
}

.left {
  flex: 1;
}

.right {
  width: 25%;
}

.create-topic {
  background-color: #efefef;
  color: grey;
  border-radius: 5px;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}

.dark .create-topic {
  background-color: #232323;
}

.topping-topic {
  display: flex;
  &:hover{
    cursor: pointer;
  }
  /*第一个div盒子：置顶帖子的标题*/
  div:first-of-type{
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8; /*给标题来点不透明度*/
    transition: color .3s; /*标题颜色发生变化时，会有0.3s的渐变时间*/
    &:hover{
      /*配合上面的transition，鼠标移动到标题上，标题变为灰色，有一个0.3s的渐变时间*/
      color: grey;
    }
  }
  /*第二个div盒子：置顶帖子的创建时间*/
  div:nth-of-type(2){
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right; /*行内容右对齐，把时间显示在最右侧*/
  }
}

/*针对每一个可供选择的帖子类型盒子*/
.type-select{
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 5px;
  box-sizing: border-box;
  transition: background-color .3s; /*背景颜色变化时，会有0.3s的渐变时间*/

  /*被选中的类型盒子，边框有颜色做区分
  &.active 是一个类选择器，表示活动状态下的样式。要和&:active区分*/
  &.active{
    border: solid 2px #ead4c4;
  }

  &:hover{
    cursor: pointer;
    /*配上上面的transition，鼠标放在类型上面时，背景颜色变，且有0.3s的渐变时间*/
    background-color: #dadada;
  }
}

.dark .type-select{
  background-color: #282828;
  /*被选中的类型盒子，边框有颜色做区分
  &.active 是一个类选择器，表示活动状态下的样式。要和&:active区分*/
  &.active{
    border: solid 2px #64594b;
  }

  &:hover{
    /*配上上面的transition，鼠标放在类型上面时，背景颜色变，且有0.3s的渐变时间*/
    background-color: #5e5e5e;
  }
}

.topic-list-container {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 15px
}

.preview-card{
  padding: 15px;
  transition: scale .3s; /*卡片发生伸缩时，会有0.3s的过渡时间*/

  &:hover{
    scale: 1.016; /*配合上面的transition，鼠标移到卡片上，卡片会有伸缩效果，过渡时间0.3s*/
    cursor: pointer;
  }
  /*预览帖子内容*/
  .preview-text{
    font-size: 14px;
    color: grey;
    margin: 5px 0;
    /*最多展示三行，超出的用省略号代替*/
    /*-webkit-前缀是为了兼容旧版WebKit浏览器（如旧版Chrome和Safari）*/
    display: -webkit-box; /*设置为基于Flexbox布局的容器*/
    -webkit-box-orient: vertical; /*设置布局容器的主轴方向为垂直方向*/
    -webkit-line-clamp: 3; /*最大展示3行*/
    overflow: hidden; /*超出行数的文本将被隐藏*/
    text-overflow: ellipsis; /*当文本被截断时，用省略号代替*/
  }
  /*预览图片，最多展示三个图片*/
  .preview-image-container{
    display: grid;
    grid-template-columns: repeat(3, 1fr); /*网格划分为3列，并且每列的宽度都平均分配*/
    grid-gap: 10px;
    .preview-image{
      width: 100%;
      height: 100%;
      max-height: 110px;
      border-radius: 5px;
    }
  }
}

.card-container {
  position: sticky; /*实现左侧帖子列表滚动，右侧卡片固定*/
  top: 20px;
}

.collect-list-button{
  font-size: 15px;
  display: flex;
  justify-content: space-between;
  transition: .3s;
  
  &:hover{
    cursor: pointer;
    opacity: 0.6; /*触摸到时，颜色变暗淡一点*/
  }

}
.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 16px;
}

.link-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-gap: 10px;
  margin-top: 10px
}

.friend-link {
  border-radius: 5px;
  overflow: hidden;
}
</style>