<script setup>

import {get} from "@/net/index.js"
import {useStore} from "@/store/index.js";

//由于帖子类型用到的地方较多，因此一加载到页面就先拿到所有的类型，并存入全局变量里
const store = useStore()
get('/api/forum/types', data => {
  const array = []
  array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})

</script>

<template>
  <div>
    <router-view v-slot="{ Component }">
      <transition name="el-fade-in-linear" mode="out-in">
        <!--keep-alive TopicList组件中的信息(帖子列表，天气信息...)，使得在看完帖子详情，返回列表时，不用重新加载一次，优化性能-->
        <keep-alive include="TopicList">
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <!--一键返回顶部：控制HomeIndex的class为main的那个div中的el-scrollbar-->
    <el-backtop target=".main .el-scrollbar__wrap" :right="20" :bottom="70"/>
  </div>

</template>