import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const request = axios.create({
    baseURL: 'http://localhost:9999',
    timeout:30000 // 后台接口请求时间
})

// 验证token格式
const isValidToken = (token) => {
    if (!token) return false;
    return token.match(/^[A-Za-z0-9-_=]+\.[A-Za-z0-9-_=]+\.?[A-Za-z0-9-_.+/=]*$/);
}

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';

    let token = localStorage.getItem('token');
    if (token) {
        token = sessionStorage.getItem('token');
    }
    
    if (token) {
        if (!isValidToken(token)) {
            ElMessage.error('token格式不正确，请重新登录');
            localStorage.removeItem('token');
            sessionStorage.removeItem('token');
            router.push('/login');
            return Promise.reject('token格式不正确');
        }
        // 确保token被正确添加到请求头
        config.headers['token'] = token;
    } else {
        // 如果没有token，且不是登录或注册请求，则跳转到登录页面
        const isLoginOrRegister = config.url.includes('/login') || config.url.includes('/register');
        if (!isLoginOrRegister) {
            ElMessage.error('请先登录');
            router.push('/login');
            return Promise.reject('未登录');
        }
    }
    return config;
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        // 检查是否有新的token
        const newToken = response.headers['new-token'];
        if (newToken) {
            if (isValidToken(newToken)) {
                localStorage.setItem('token', newToken);
                sessionStorage.setItem('token', newToken);
            } else {
                ElMessage.error('收到的新token格式不正确');
            }
        }
        
        let res = response.data;
        if (typeof res == 'string') {
            res = res ? JSON.parse(res) : res
        }
        
        // 处理登录成功的情况
        if (res.code === '200' && (response.config.url.includes('/login') || response.config.url.includes('/register'))) {
            if (res.data && res.data.token) {
                localStorage.setItem('token', res.data.token);
                sessionStorage.setItem('token', res.data.token);
            }
        }
        
        if (res.code === '400') {
            ElMessage.error(res.msg || '请求失败')
            if (res.msg && (res.msg.includes('token') || res.msg.includes('登录'))) {
                localStorage.removeItem('token');
                sessionStorage.removeItem('token');
                router.push('/login')
            }
            return Promise.reject(res)
        } else {
            return res;
        }
    },
    error => {
        if (!error.response) {
            ElMessage.error('网络连接失败，请检查后端服务是否启动')
            return Promise.reject(error)
        }
        
        const errorMsg = error.response.data?.msg || error.message || '未知错误'
        
        if (error.response.status === 404) {
            ElMessage.error('未找到请求接口')
        } else if (error.response.status === 500) {
            ElMessage.error('系统异常，请查看后端控制台报错')
        } else if (error.response.status === 401) {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            sessionStorage.removeItem('token')
            router.push('/login')
        } else {
            ElMessage.error(errorMsg)
        }
        return Promise.reject(error)
    }
)

export default request