import axios from 'axios'
import {ElMessage} from "element-plus";

//将请求的基地址设置为后端服务器地址
axios.defaults.baseURL = "http://localhost:8080"

//存储在storage中的名字
const authItemName = "access_token"

//拿到完整的Authorization请求头
const accessHeader = ()=>{
    return{
        'Authorization': `Bearer ${takeAccessToken()}`
    }
}

const defaultFailure = (url, code, message) =>{
    console.warn(`请求地址：${url}，状态码：${code}，错误信息：${message}`)
    ElMessage.warning(message)
}

const defaultError = (err) =>{
    console.error(err)
    ElMessage.error('发生了一些错误，请联系管理员')
}

//往客户端存储token令牌
function storeAccessToken(remember, token, expire){
    const authObj = {token: token, expire: expire}//将token和过期时间一起存到客户端
    const str = JSON.stringify(authObj)//storage中存的是字符串，因此先做一个转换
    if(remember)//如果勾选“记住我”，那么就把token存到localStorage中
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)//如果没有勾选“记住我”，那么就把token存到sessionStorage中
}

//拿出存储在客户端中的token
function takeAccessToken(){
    //如果客户端有存token，那就是在localStorage或sessionStorage中，都试着拿一下
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if (!str) return null //两个地方都没有存，说明用户没有登录
    const authObj = JSON.parse(str) //拿到，则重新转换为JSON格式
    if (authObj.expire <= new Date()){//如果token过期，需要把token删除，并提醒用户
        deleteAccessToken()
        ElMessage.warning('登录状态已过期，请重新登录')
        return null
    }
    return authObj.token
}

//删除客户端存储的token，由于不知道存在哪，两个地方都删一次就行
function deleteAccessToken(){
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

//自定义内部Post请求，参数包括：请求url，请求携带的数据，请求头，成功/失败/错误的回调函数
//.then()里的那个data是响应得到的数据，后端定义了统一的格式，这个data包含code，data，message
function internalPost(url, data, header, success, failure, error = defaultError){
    axios.post(url, data, {headers: header}).then(({data}) =>{
        if (data.code === 200){
            success(data.data)
        }else{
            failure(url, data.code, data.message)
        }
    }).catch(err => error(err))
}

//自定义内部Get请求，参数包括：请求url，请求头，成功/失败/错误的回调函数
//.then()里的那个data是响应得到的数据，后端定义了统一的格式，这个data包含code，data，message
function internalGet(url, header, success, failure, error = defaultError){
    axios.get(url, {headers: header}).then(({data}) =>{
        if (data.code === 200){
            success(data.data)
        }else{
            failure(url, data.code, data.message)
        }
    }).catch(err => error(err))
}

//封装登录请求
function login(username, password, remember, success, failure = defaultFailure){
    internalPost('/api/auth/login', {
        username: username,
        password: password
    },{
        //axios默认以JSON格式提交数据，但SpringSecurity只支持表单登录，因此请求头中将类型设置为表单提交
        'Content-Type': 'application/x-www-form-urlencoded'
    },(data) =>{
        //登录成功后，客户端存储token令牌
        storeAccessToken(remember, data.token, data.expire)
        ElMessage.success(`登录成功，欢迎 ${data.username} 来到我们的系统`)
        success(data)
    }, failure)
}

//封装退出登录请求,注意需要携带Authorization请求头
function logout(success, failure = defaultFailure){
    get('/api/auth/logout', ()=>{
        deleteAccessToken()
        ElMessage.success("退出登录成功")
        success()
    },failure)
}

//封装暴露出去Post请求。前端所有的post请求都用这个，Authorization请求头携带token
function post(url, data, success, failure = defaultFailure){
    internalPost(url, data, accessHeader(), success, failure)
}

//封装暴露出去的Get请求。前端所有的get请求都用这个，Authorization请求头携带token
function get(url, success, failure = defaultFailure){
    internalGet(url, accessHeader(), success, failure)
}

//判断用户是否未授权
function unauthorized(){
    //拿token，如果未授权，就会拿到null，返回!null表示用户未授权
    return !takeAccessToken()
}

export {login, logout, get, post, unauthorized, accessHeader}