import {defineStore} from 'pinia'
import axios from "axios";

export const useStore = defineStore('general', {
    state: () => {
        return{
            user: {
                username: '',
                email: '',
                role: '',
                avatar: '',
                registerTime: null
            },
            forum: {
                types: []
            }
        }
    },getters:{
        avatarUrl(){
            if(this.user.avatar)
                return `${axios.defaults.baseURL}/images${this.user.avatar}`
            else return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
    },actions: {
        //根据id返回type，包含它的各个字段，方便后续使用
        findTypeById(id){
            for(let type of this.forum.types){
                if(type.id === id)
                    return type
            }
        }
    }
})