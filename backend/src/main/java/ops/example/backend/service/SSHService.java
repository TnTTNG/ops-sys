package ops.example.backend.service;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import ops.example.backend.config.SSHConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SSHService {
    private final Map<String, Session> sshSessions = new ConcurrentHashMap<>();
    private final Map<String, StringBuilder> commandBuffers = new ConcurrentHashMap<>();
    
    @Autowired
    private SSHConfig sshConfig;
    
    /**
     * 连接到SSH服务器
     * @param sessionId 会话ID
     * @param host 主机地址
     * @throws JSchException SSH连接异常
     */
    public void connect(String sessionId, String host) throws JSchException {
        log.info("正在连接SSH服务器: {}@{}:22, sessionId={}", sshConfig.getUsername(), host, sessionId);
        log.debug("SSH连接配置：username={}, password={}", sshConfig.getUsername(), 
                 sshConfig.getPassword() != null ? "已设置(长度:" + sshConfig.getPassword().length() + ")" : "未设置");
        
        try {
            JSch jsch = new JSch();
            
            // 显示JSch版本
            log.debug("JSch版本: {}", JSch.VERSION);
            
            Session session = jsch.getSession(sshConfig.getUsername(), host, 22);
            session.setPassword(sshConfig.getPassword());
            
            // 设置SSH配置
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "password,keyboard-interactive");
            config.put("MaxAuthTries", "3");
            session.setConfig(config);
            
            log.info("正在尝试连接到SSH服务器，超时时间为30秒...");
            session.connect(30000);
            
            // 检查连接状态
            if (session.isConnected()) {
                log.info("SSH连接成功: sessionId={}, serverVersion={}", sessionId, session.getServerVersion());
                sshSessions.put(sessionId, session);
                commandBuffers.put(sessionId, new StringBuilder());
                
                // 创建一个初始Shell
                try {
                    initializeShell(sessionId);
                } catch (Exception e) {
                    log.warn("初始化Shell失败，但连接已建立: {}", e.getMessage());
                }
            } else {
                log.error("SSH连接失败: 会话未连接");
                throw new JSchException("会话未连接");
            }
        } catch (JSchException e) {
            log.error("SSH连接失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // 存储Shell通道
    private final Map<String, ChannelShell> shellChannels = new ConcurrentHashMap<>();
    
    /**
     * 初始化Shell通道
     * @param sessionId 会话ID
     * @throws Exception 执行异常
     */
    private void initializeShell(String sessionId) throws Exception {
        Session session = sshSessions.get(sessionId);
        if (session == null || !session.isConnected()) {
            throw new Exception("SSH会话不存在或已断开");
        }
        
        try {
            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(true);
            channel.connect(5000);
            
            shellChannels.put(sessionId, channel);
            log.info("SSH Shell通道初始化成功: sessionId={}", sessionId);
        } catch (Exception e) {
            log.error("初始化Shell通道失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 执行SSH命令
     * @param sessionId 会话ID
     * @param input 用户输入
     * @param outputStream 输出流
     * @throws Exception 执行异常
     */
    public void executeCommand(String sessionId, String input, ByteArrayOutputStream outputStream) throws Exception {
        log.info("处理SSH输入: sessionId={}, input={}", sessionId, input);
        
        Session session = sshSessions.get(sessionId);
        if (session == null || !session.isConnected()) {
            throw new Exception("SSH会话不存在或已断开");
        }
        
        // 获取Shell通道
        ChannelShell channel = shellChannels.get(sessionId);
        if (channel == null || !channel.isConnected()) {
            log.info("Shell通道不存在或已断开，尝试重新初始化");
            try {
                initializeShell(sessionId);
                channel = shellChannels.get(sessionId);
            } catch (Exception e) {
                log.error("重新初始化Shell通道失败: {}", e.getMessage(), e);
                throw new Exception("无法建立Shell通道: " + e.getMessage());
            }
        }
        
        // 发送用户输入到Shell
        try {
            java.io.OutputStream shellOutputStream = channel.getOutputStream();
            shellOutputStream.write(input.getBytes());
            shellOutputStream.flush();
            
            // 获取Shell输出
            java.io.InputStream shellInputStream = channel.getInputStream();
            
            // 短暂等待输出
            Thread.sleep(500);
            
            // 读取输出
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            
            while (shellInputStream.available() > 0) {
                int i = shellInputStream.read(buffer, 0, 1024);
                if (i < 0) break;
                outputStream.write(buffer, 0, i);
                bytesRead += i;
            }
            
            log.info("SSH命令执行完成，读取到 {} 字节输出", bytesRead);
            
            if (bytesRead == 0 && input.equals("\r")) {
                // 如果是回车且没有输出，添加一个提示符
                outputStream.write("$ ".getBytes());
            }
        } catch (Exception e) {
            log.error("执行命令失败: {}", e.getMessage(), e);
            outputStream.write(("\n执行失败: " + e.getMessage() + "\n$ ").getBytes());
            throw e;
        }
    }
    
    /**
     * 断开SSH连接
     * @param sessionId 会话ID
     */
    public void disconnect(String sessionId) {
        log.info("断开SSH连接: sessionId={}", sessionId);
        
        // 关闭Shell通道
        ChannelShell channel = shellChannels.remove(sessionId);
        if (channel != null && channel.isConnected()) {
            try {
                channel.disconnect();
                log.info("SSH Shell通道已断开: sessionId={}", sessionId);
            } catch (Exception e) {
                log.error("断开Shell通道失败: {}", e.getMessage(), e);
            }
        }
        
        // 关闭SSH会话
        Session session = sshSessions.remove(sessionId);
        commandBuffers.remove(sessionId);
        
        if (session != null && session.isConnected()) {
            try {
                session.disconnect();
                log.info("SSH连接已断开: sessionId={}", sessionId);
            } catch (Exception e) {
                log.error("断开SSH连接失败: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * 检查会话是否存在
     * @param sessionId 会话ID
     * @return 是否存在
     */
    public boolean hasSession(String sessionId) {
        Session session = sshSessions.get(sessionId);
        return session != null && session.isConnected();
    }
    
    /**
     * 获取当前命令缓冲区内容
     * @param sessionId 会话ID
     * @return 当前命令
     */
    public String getCurrentCommand(String sessionId) {
        StringBuilder commandBuffer = commandBuffers.get(sessionId);
        if (commandBuffer == null) {
            return "";
        }
        return commandBuffer.toString().trim();
    }
} 