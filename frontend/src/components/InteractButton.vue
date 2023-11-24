<script setup>
/*定义三个属性，外部传进来*/
defineProps({
  name: String, /*按钮未选中时的文本内容*/
  checkName: String, /*按钮选中时的文本内容*/
  color: String,
  check: Boolean /*该按钮是否被选中*/
})

const emit = defineEmits(['check']) /*配合到图标里的@click，点击图标时触发*/
</script>

<template>
  <div class="interact-button">
    <span class="icon" :style="{'color': check ? color : 'unset'}" @click="emit('check')">
      <slot/>
    </span>
    <span class="name" :style="{'color':color}">
      {{check ? checkName : name}}
    </span>
  </div>
</template>

<style lang="less" scoped>
.interact-button{
  display: inline-block;
  height: 20px;

  .name{
    font-size: 13px;
    margin-left: 5px;
    opacity: 0.7; /*给点透明度，字体不要太亮*/
  }

  .icon{
    vertical-align: middle;
    transition: .3s;

    &:hover{
      cursor: pointer;
      font-size: 18px; /*摸到图标，渐变变大，提示一下*/
     }
  }
}

</style>