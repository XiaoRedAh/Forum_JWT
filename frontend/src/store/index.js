import {defineStore} from 'pinia'

export const useStore = defineStore('general', {
    state: () => {
        return{
            user: {
                username: '',
                email: '',
                role: '',
                registerTime: null
            }
        }
    }
})