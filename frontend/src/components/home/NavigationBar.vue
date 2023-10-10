<script setup>
import {
  School,
  ChatLineRound,
  Bell,
  Guide,
  MagicStick,
  Position,
  Document,
  Files,
  DataLine,
  Notebook,
  Place,
  Setting, Lock, Compass, User
} from '@element-plus/icons-vue';

</script>

<template>
  <div class="nav-container">
    <el-menu
        router
        :default-active="$route.path"
        class="menu"
    >
      <el-sub-menu index="1">
        <template #title>
          <el-icon>
            <School/>
          </el-icon>
          <span>校园论坛</span>
        </template>
        <el-menu-item index="/home">
          <el-icon>
            <ChatLineRound/>
          </el-icon>
          <span>帖子广场</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <Bell/>
          </el-icon>
          <span>失物招领</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <Guide/>
          </el-icon>
          <span>校园活动</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <MagicStick/>
          </el-icon>
          <span>表白墙</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <compass/>
          </el-icon>
          <span>牛马考研</span>
          <el-tag style="margin-left: 10px">合作机构</el-tag>
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="2">
        <template #title>
          <el-icon>
            <Position/>
          </el-icon>
          <span>探索与发现</span>
        </template>
        <el-menu-item>
          <el-icon>
            <Document/>
          </el-icon>
          <span>成绩查询</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <Files/>
          </el-icon>
          <span>班级课程表</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <DataLine/>
          </el-icon>
          <span>教务通知</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <Notebook/>
          </el-icon>
          <span>在线图书馆</span>
        </el-menu-item>
        <el-menu-item>
          <el-icon>
            <Place/>
          </el-icon>
          <span>预约教室</span>
        </el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="3">
        <template #title>
          <el-icon>
            <setting/>
          </el-icon>
          <span>个人设置</span>
        </template>
        <el-menu-item index="/home/user-setting">
          <el-icon>
            <user/>
          </el-icon>
          <span>个人信息设置</span>
        </el-menu-item>
        <el-menu-item index="/home/privacy-setting">
          <el-icon>
            <lock/>
          </el-icon>
          <span>账号安全设置</span>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>

</template>

<style lang="less" scoped>
.nav-container {
  height: 100%;

  .menu {
    height: 100%;
  }

}

</style>
