<script setup>
//定义一个data属性，使得TopicList组件可以通过这个属性将weather数据传到该组件中
defineProps({
  data: Object
})

</script>

<template>
  <div v-loading="!data.success" element-loading-text="正在加载天气信息...">
    <!--上方：显示当前天气，所在地区-->
    <div class="content-1" v-if="data.success">
      <div style="font-size: 45px;color: grey">
        <i :class="`qi-${data.now_weather.icon}-fill`"></i>
      </div>
      <div style="font-weight: bold;text-align: center">
        <div style="font-size: 25px">{{data.now_weather.temp}}℃</div>
        <div style="font-size: 15px">{{data.now_weather.text}}</div>
      </div>
      <div style="margin-top: 13px">
        <div style="font-size: 15px">{{`${data.location.country} ${data.location.adm1}`}}</div>
        <div style="font-size: 14px;color: grey">{{`${data.location.adm2} ${data.location.name}区`}}</div>
      </div>
    </div>
    <el-divider style="margin: 10px 0"></el-divider>
    <!--下方：显示未来五小时内的天气-->
    <div class="content-2">
      <div v-for="item in data.hourly">
        <div style="font-size: 13px">{{ new Date(item.fxTime).getHours() }}时</div>
        <div style="font-size: 23px">
          <i :class="`qi-${item.icon}-fill`"></i>
        </div>
        <div style="font-size: 12px">{{item.temp}}℃</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.content-1{
  display: flex;
  justify-content: space-between;
  margin: 10px 20px
}

.content-2{
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  text-align: center
}
</style>