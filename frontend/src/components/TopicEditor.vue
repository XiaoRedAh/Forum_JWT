<script setup>
import {reactive} from "vue";
import {Document, Check} from "@element-plus/icons-vue";
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import {Quill, QuillEditor} from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from "axios";
import {get} from "@/net/index.js"
import {accessHeader} from "@/net/index.js";
import {ElMessage} from "element-plus";
import ColorDot from "@/components/ColorDot.vue";

defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

//帖子文章相关字段
const article = reactive({
  type: null,
  title: '',
  text: '',
  loading: false
})

//页面加载后，第一时间先去把帖子类型拉取下来
let types = []
get('/api/forum/types', data => types = data)


//将这两个模块注册进来
Quill.register('modules/ImageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)
//富文本编辑器的配置，详细的去看官方文档
const editorOption = {
  modules: {
    toolbar: {
      container: [
        'bold', 'italic', 'underline', 'strike', 'clean',
        'blockquote', 'code-block', 'link', 'image',
        {'color': []}, {'background': []},
        {'size': ['small', false, 'large', 'huge']},
        {'header': [1, 2, 3, 4, 5, 6, false]},
        {'list': 'ordered'}, {'list': 'bullet'},
        {'align': []},
        {'header': [1, 2, 3, 4, 5, 6, false]},
        {'indent': '-1'}, {'indent': '+1'},
      ],
      //用于拦截，劫持原来的图片点击事件
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      },
    },
    ImageResize: {
      modules: ['Resize', 'DisplaySize']
    },
    ImageExtend: {
      action: axios.defaults.baseURL + '/api/image/cache',  // 上传图片的请求地址, 如果action为空，则采用base64插入图片
      name: 'file',  // 图片参数名
      size: 5,  // 可选参数 图片大小，单位为M，1M = 1024kb
      loading: true, //显示上传的状态
      accept: 'image/png, image/jpeg', //允许上传的图片类型

      // response 为一个函数用来获取具体图片地址
      // 例如：上传图片后，服务器返回{ code: 200, data: "/cache/20231014/eb840ff2563a46089ed525ab8181b0dc", message: "请求成功" }
      // 则使用 res.data，就可以拿到存储在minio服务器的图片的名字了
      response: (resp) => {
        if (resp.data) {
          //从minio服务器获取图片
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: (xhr) => { //需要在header中携带JWT
        xhr.setRequestHeader('Authorization', accessHeader().Authorization)
      },  // 可选参数 设置请求头部
      start: () => {// 可选参数 自定义开始上传触发事件
        //主要是为了在上传图片的过程中，不要让用户继续编辑（在这期间编辑，可能会使图片显示的时候出现些bug）
        article.loading = true
      },
      error: () => { // 可选参数 上传失败触发的事件
        ElMessage.warning('图片上传失败，请联系管理员')
        //设置为false，用户可以继续编辑了
        article.loading = false
      },
      success: () => { // 可选参数  上传成功触发的事件
        ElMessage.success('图片上传成功！')
        //设置为false，用户可以继续编辑了
        article.loading = false
      },
    }
  }
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" direction="btt"
               :size="650" :close-on-click-modal="false"
               @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的主题</div>
          <div style="font-size: 13px">发表主题之前，请仔细阅读相关法律法规，不要出现违禁词，不文明等违规行为</div>
        </div>
      </template>
      <!--主体类型 + 标题-->
      <div style="display: flex;gap: 10px">
        <!--选择主题类型-->
        <div>
          <el-select placeholder="选择主题类型..." value-key="id" v-model="article.type" :disabled="!types.length">
            <el-option v-for="item in types" :value="item" :label="item.name">
              <div>
                <color-dot :color="item.color"></color-dot>
                <span style="margin-left: 10px">{{item.name}}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <!--填写标题-->
        <div style="flex: 1">
          <el-input v-model="article.title" placeholder="请输入帖子标题..." :prefix-icon="Document"></el-input>
        </div>
      </div>
      <!--展示选择的帖子类型描述-->
      <div style="margin-top: 10px;font-size: 13px;color: grey">
        <color-dot :color="article.type.color"></color-dot>
        <span style="margin-left: 8px">{{article.type ? article.type.desc : '请选择帖子类型'}}</span>
      </div>
      <!--富文本编辑器，这里使用quill-->
      <div style="margin-top: 15px;height: 78%;overflow: hidden" v-loading="article.loading" element-loading-text="正在上传图片，请稍后">
        <quill-editor v-model="article.text" style="height: calc(100% - 41px)" content-type="delta"
                      placeholder="今天想分享点什么呢？" :options="editorOption"></quill-editor>

      </div>
      <!--字数统计 + 发表按钮-->
      <div style="display: flex;justify-content: space-between;margin-top: 10px">
        <div style="color: grey;font-size: 13px">
          当前字数 425（最大支持20000字）
        </div>
        <div>
          <el-button type="success" :icon="Check" plain>立即发表帖子</el-button>
        </div>
      </div>
    </el-drawer>
  </div>

</template>

<style scoped>
:deep(.el-drawer) {
  width: 50%;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  font-style: normal;
}

:deep(.ql-editor) {
  font-size: 14px;
}
</style>