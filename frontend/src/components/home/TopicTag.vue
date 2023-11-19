<script setup>
import {useStore} from "@/store/index.js";

const store = useStore()
//帖子类型需要外部传进来
defineProps({
  type: Number
})

</script>

<template>
  <div class="preview-type"
       :style="{
                     color: store.findTypeById(type)?.color + 'EE',
                    'border-color': store.findTypeById(type)?.color + '77',
                    'background': store.findTypeById(type)?.color + '33',
               }">
    {{store.findTypeById(type)?.name}}
  </div>

</template>

<style>
/*预览帖子类型和标题*/
.preview-type{
  display: inline-block;
  border: solid 0.5px grey;
  border-radius: 5px;
  font-size: 13px;
  padding: 0 5px;
  height: 18px;
  margin-top: 5px;
}

</style>