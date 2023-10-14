<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import {computed, ref, reactive} from "vue";
import {get} from "@/net/index.js"
import TopicEditor from "@/components/TopicEditor.vue";
import Weather from "@/components/Weather.vue";
import {ElMessage} from "element-plus";

//控制“编辑文章”的卡片是否弹出
const editor = ref(false)

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
      </light-card>
      <!--接下来分出一个框展示置顶的帖子-->
      <light-card class="topping-topic">

      </light-card>
      <!--剩下的空间展示帖子列表-->
      <div class="topic-list-container">
        <light-card style="height: 100px" v-for="item in 10">

        </light-card>
      </div>
    </div>

    <!--右侧装一些卡片丰富一下-->
    <div class="right">
      <div class="card-container">
        <!--论坛公告卡片-->
        <light-card>
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
    <topic-editor :show="editor" @close="editor = false"></topic-editor>
  </div>
</template>

<style scoped>
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
}

.create-topic:hover {
  cursor: pointer;
}

.topping-topic {
  margin-top: 10px;
  height: 50px
}

.topic-list-container {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 15px
}

.card-container {
  position: sticky; /*实现左侧帖子列表滚动，右侧卡片固定*/
  top: 20px;
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