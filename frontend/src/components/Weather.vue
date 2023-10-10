<script setup>

</script>

<template>
  <div>
    <!--上方：显示当前天气，所在地区-->
    <div class="content-1">
      <div style="font-size: 45px;color: grey">
        <i class="qi-300-fill"></i>
      </div>
      <div style="font-weight: bold;text-align: center">
        <div style="font-size: 25px">20℃</div>
        <div style="font-size: 15px">多云</div>
      </div>
      <div style="margin-top: 13px">
        <div style="font-size: 15px">中国 广东省</div>
        <div style="font-size: 14px;color: grey">广州市 荔湾区</div>
      </div>
    </div>
    <el-divider style="margin: 10px 0"></el-divider>
    <!--下方：显示未来五小时内的天气-->
    <div class="content-2">
      <div v-for="item in 5">
        <div style="font-size: 13px">{{ item }}时</div>
        <div style="font-size: 23px">
          <i class="qi-302-fill"></i>
        </div>
        <div style="font-size: 12px">20℃</div>
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