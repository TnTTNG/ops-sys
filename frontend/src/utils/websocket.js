import { ElMessage } from 'element-plus';
import router from "@/router/index.js";

class WebSocketClient {
    constructor() {
        this.ws = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 3000;
        this.messageHandlers = new Set();
    }

    connect() {
        const user = JSON.parse(localStorage.getItem('code_user') || '{}');
        if (!user.token) {
            console.error('WebSocket连接失败: 未找到token');
            router.push('/login');
            ElMessage.error('WebSocket连接失败: 请先登录');
            return;
        }

        try {
            const wsUrl = `ws://localhost:9999/websocket/user?token=${encodeURIComponent(user.token)}`;
            console.log('正在连接WebSocket:', wsUrl);
            
            this.ws = new WebSocket(wsUrl);
            
            this.ws.onopen = () => {
                console.log('WebSocket连接成功');
                this.reconnectAttempts = 0;
            };

            this.ws.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data);
                    console.log('收到WebSocket消息:', message);
                    this.messageHandlers.forEach(handler => handler(message));
                } catch (error) {
                    console.error('处理WebSocket消息失败:', error);
                }
            };

            this.ws.onclose = (event) => {
                console.log('WebSocket连接关闭:', event.code);
                if (event.code === 1006 && this.reconnectAttempts < this.maxReconnectAttempts) {
                    this.reconnect();
                } else {
                    ElMessage.error('WebSocket连接已断开，请刷新页面重试');
                }
            };

            this.ws.onerror = (error) => {
                console.error('WebSocket错误:', error);
                ElMessage.error('WebSocket连接错误，请刷新页面重试');
            };
        } catch (error) {
            console.error('创建WebSocket连接失败:', error);
            ElMessage.error('创建WebSocket连接失败，请刷新页面重试');
        }
    }

    reconnect() {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.error('WebSocket重连次数超过最大限制');
            ElMessage.error('WebSocket连接失败，请刷新页面重试');
            return;
        }

        this.reconnectAttempts++;
        console.log(`尝试重新连接 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
        
        setTimeout(() => {
            this.connect();
        }, this.reconnectInterval);
    }

    disconnect() {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }

    addMessageHandler(handler) {
        this.messageHandlers.add(handler);
    }

    removeMessageHandler(handler) {
        this.messageHandlers.delete(handler);
    }
}

export const websocketClient = new WebSocketClient();