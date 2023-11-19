import {createRouter, createWebHistory} from "vue-router";
import {unauthorized} from "@/net/index.js";

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: ()=>import('@/pages/WelcomeIndex.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: ()=>import('@/components/welcome/Login.vue'),
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: ()=>import('@/components/welcome/Register.vue'),
                },
                {
                    path: 'reset',
                    name: 'welcome-reset',
                    component: ()=>import('@/components/welcome/ResetPaw.vue'),
                }
            ],
        },
        {
            path: '/home',
            name: 'home',
            component: ()=>import('@/pages/HomeIndex.vue'),
            children: [
                { //默认展示帖子广场
                    path: '',
                    name: 'topics',
                    component: ()=>import('@/pages/forum/Forum.vue'),
                    children: [
                        { //默认展示帖子列表
                            path: '',
                            name: 'topic-list',
                            component: ()=>import('@/pages/forum/TopicList.vue')
                        },
                        { //展示帖子详情
                            path: 'topic-detail/:tid',
                            name: 'topic-detail',
                            component: ()=>import('@/pages/forum/TopicDetail.vue')
                        },
                    ]
                },
                {
                    path: 'user-setting',
                    name: 'user-setting',
                    component: ()=>import('@/pages/settings/UserSetting.vue')
                },
                {
                    path: 'privacy-setting',
                    name: 'privacy-setting',
                    component: ()=>import('@/pages/settings/PrivacySetting.vue')
                }
            ]
        }
    ]

})

//配置路由守卫
router.beforeEach((to, from, next) =>{
    const isUnauthorized = unauthorized()
    //如果路由的目的地址的名字是welcome开头（想去欢迎页面）的，且已登录，那么强制跳转到主页，不能回到欢迎页面
    if(to.name.startsWith('welcome') && !isUnauthorized) {
        next('/home')
    }
    //如果路由的目的地址的路径是home开头（想去主页）的，但未登录，那么强制跳转到登录页面
    else if(to.fullPath.startsWith('/home') && isUnauthorized) {
        next('/')
    } else {//正常情况下，放行
        next()
    }
})

export default router