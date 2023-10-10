<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, Link} from "@element-plus/icons-vue";
import {computed} from "vue";

//利用js内置的api得到当前日期
const today = computed(()=>{
  const date = new Date()
  return `${date.getFullYear()}年${date.getMonth()}月${date.getDay()}日`
})
</script>

<template>
  <div class="container">
    <!--左侧主要展示帖子列表-->
    <div class="left">
      <light-card style="height: 2000px"></light-card>
    </div>

    <!--右侧装一些卡片丰富一下-->
    <div class="right">
      <div class="card-container">
        <!--论坛公告卡片-->
        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag/></el-icon>
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
              <el-icon><Calendar/></el-icon>
              天气信息
            </div>
          </div>
          <el-divider style="margin: 10px 0"></el-divider>
          <!--天气详情单独封装为一个组件-->
          <weather/>
        </light-card>
        <!--展示日期及ip地址卡片-->
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <!--展示友链-->
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <el-icon><Link/></el-icon>
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
  </div>
</template>

<style scoped>
.container{
  display: flex;
  margin: 20px auto;
  gap: 20px;
  max-width: 1300px;
}

.left{
  flex: 1;
  /*height: 2000px;*/
}

.right{
  width: 25%;
}

.card-container{
  position: sticky; /*实现左侧帖子列表滚动，右侧卡片固定*/
  top: 20px;
}

.info-text{
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 16px;
}

.link-container{
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-gap: 10px;margin-top:10px
}

.friend-link{
  border-radius: 5px;
  overflow: hidden;
}
</style>