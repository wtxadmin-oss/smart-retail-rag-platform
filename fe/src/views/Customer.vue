<template>
  <div class="customer-wrapper">
    <Navbar />
    
    <div class="page-container chat-page">
      <div class="chat-layout">
        <!-- 侧边信息栏 -->
        <div class="chat-sidebar">
          <div class="sidebar-header">
            <h3>智能咖啡师</h3>
            <p>24小时为您服务</p>
          </div>
          <div class="faq-list">
            <h4>常见问题</h4>
            <ul>
              <li @click="setInput('你们的咖啡豆是哪里产的？')">你们的咖啡豆是哪里产的？</li>
              <li @click="setInput('如何冲泡出好喝的咖啡？')">如何冲泡出好喝的咖啡？</li>
              <li @click="setInput('可以推荐一款适合初学者的咖啡吗？')">可以推荐一款适合初学者的咖啡吗？</li>
              <li @click="setInput('你们有低因咖啡吗？')">你们有低因咖啡吗？</li>
            </ul>
          </div>
        </div>

        <!-- 聊天主区域 -->
        <div class="chat-main">
          <!-- 聊天内容区 -->
          <div class="chat-container" ref="chatContainer">
            <div v-for="(msg, index) in messages" :key="index" :class="['message-wrapper', msg.role]">
              <div class="avatar">
                <el-avatar :size="40" :src="msg.role === 'service' ? '/static/picture/logo.jpg' : ''">
                  {{ msg.role === 'user' ? 'U' : 'S' }}
                </el-avatar>
              </div>
              <div class="message-content">
                <div class="message-bubble">
                  {{ msg.text }}
                </div>
                <div class="message-time">{{ msg.time }}</div>
              </div>
            </div>
            <!-- 加载状态 -->
            <div v-if="loading" class="message-wrapper service">
              <div class="avatar">
                <el-avatar :size="40" src="/static/picture/logo.jpg">S</el-avatar>
              </div>
              <div class="message-content">
                <div class="message-bubble loading-bubble">
                  <el-icon class="is-loading"><Loading /></el-icon> 正在思考中...
                </div>
              </div>
            </div>
          </div>
          
          <!-- 输入区 -->
          <div class="input-area">
            <el-input
              v-model="inputMsg"
              placeholder="询问关于咖啡的一切..."
              @keyup.enter.prevent="sendMessage"
              :rows="3"
              type="textarea"
              resize="none"
              :disabled="loading"
              class="chat-input"
            />
            <div class="input-footer">
              <span class="tip">按 Enter 发送</span>
              <el-button type="primary" class="send-btn" @click="sendMessage" :disabled="!inputMsg.trim() || loading">发送</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import axios from 'axios'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'

const router = useRouter()
const active = ref("customer")
const inputMsg = ref('')
const chatContainer = ref(null)
const loading = ref(false)

const messages = ref([
  {
    role: 'service',
    text: '您好！我是 SmartCoffee 智能客服，请问有什么可以帮您？',
    time: new Date().toLocaleTimeString()
  }
])

// 返回首页。
const goHome = () => {
  router.push('/')
}

// 点击常见问题时，把预设问题写入输入框并直接发送。
const setInput = (text) => {
  inputMsg.value = text
  sendMessage()
}

// 每次消息变化后把聊天窗口滚动到底部，保证最新消息可见。
const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// 发送用户消息到后端智能客服接口，并把回复追加到聊天记录。
const sendMessage = async () => {
  if (!inputMsg.value.trim() || loading.value) return
  
  const userText = inputMsg.value
  // 用户发送
  messages.value.push({
    role: 'user',
    text: userText,
    time: new Date().toLocaleTimeString()
  })
  
  inputMsg.value = ''
  loading.value = true
  scrollToBottom()
  
  try {
    // 调用后端 API，交给后端处理 RAG 和 AI 逻辑
    // 注意：请在后端实现 /api/chat 接口
    const response = await axios.post('/api/chat', {
      message: userText
    })
    
    messages.value.push({
      role: 'service',
      text: response.data.reply || response.data.message || '抱歉，我没能理解您的意思。',
      time: new Date().toLocaleTimeString()
    })
  } catch (error) {
    console.error('Chat API Error:', error)
    messages.value.push({
      role: 'service',
      text: '网络连接似乎有点问题，请稍后再试。',
      time: new Date().toLocaleTimeString()
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.customer-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
}

.chat-page {
  flex: 1;
  display: flex;
  padding: 40px 0;
}

.chat-layout {
  display: flex;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  height: 80vh;
  border: 1px solid var(--el-border-color-light);
}

.chat-sidebar {
  width: 280px;
  background-color: var(--el-bg-color-page);
  border-right: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 30px 20px;
  background-color: var(--el-color-primary-light-9);
  border-bottom: 1px solid var(--el-border-color-light);
  text-align: center;
}

.sidebar-header h3 {
  margin: 0;
  color: var(--el-color-primary);
  font-size: 20px;
}

.sidebar-header p {
  margin: 8px 0 0;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.faq-list {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.faq-list h4 {
  margin: 0 0 16px 0;
  color: var(--el-text-color-regular);
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.faq-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.faq-list li {
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
  font-size: 14px;
  color: var(--el-text-color-regular);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color-light);
}

.faq-list li:hover {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
  border-color: var(--el-color-primary-light-7);
  transform: translateY(-2px);
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.message-wrapper {
  display: flex;
  gap: 16px;
  max-width: 80%;
}

.message-wrapper.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-wrapper.service {
  align-self: flex-start;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.user .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  word-break: break-all;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.loading-bubble {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  background-color: var(--el-bg-color-page) !important;
}

.service .message-bubble {
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
  border-top-left-radius: 4px;
}

.user .message-bubble {
  background-color: var(--el-color-primary);
  color: #fff;
  border-top-right-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  padding: 0 4px;
}

.input-area {
  padding: 24px 30px;
  background-color: #fff;
  border-top: 1px solid var(--el-border-color-light);
}

.chat-input {
  --el-input-border-color: var(--el-border-color-light);
  --el-input-hover-border-color: var(--el-color-primary);
  --el-input-focus-border-color: var(--el-color-primary);
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 12px 16px;
  font-size: 15px;
}

.input-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

.tip {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.send-btn {
  padding: 10px 24px;
  font-size: 15px;
  border-radius: 8px;
}
</style>
