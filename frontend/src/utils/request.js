import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const request = axios.create({
    baseURL: 'http://localhost:9999',
    timeout:30000 // 后台接口请求时间
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    // 在vue 的request 拦截器里面加上统一的请求头 token
    let user = JSON.parse(localStorage.getItem('code_user') || '{}')
    config.headers['token'] = user.token
    return config;
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res == 'string') {
            res = res ? JSON.parse(res) : res
        }
        if (res.code === '400') {
            ElMessage.error(res.msg)
            router.push('/login')
        } else {
            return res;
        }

    },
    error => {
        if (!error.response) {
            ElMessage.error('网络连接失败，请检查后端服务是否启动')
            return Promise.reject(error)
        }
        if (error.response.status === 404) {
            ElMessage.error('未找到请求接口')
        } else if (error.response.status === 500) {
            ElMessage.error('系统异常，请查看后端控制台报错')
        } else {
            console.error(error.message)
        }
        return Promise.reject(error)
    }
)

export default request