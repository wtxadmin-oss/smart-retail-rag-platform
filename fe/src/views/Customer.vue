<template>
  <div class="common-layout">
    <el-container style="height: 100vh;">
      <el-header style="display:flex; align-items:center; gap:12px; border-bottom: 1px solid #dcdfe6;">
        <span id="logo" style="cursor:pointer;" @click="goHome">
          <img src="/static/picture/logo.jpg" alt="logo" style="height:40px;">
        </span>
        <h2 style="margin:0;">SmartCoffee</h2>
        <span style="opacity:.7;">智能咖啡系统</span>
      </el-header>
      <el-container style="overflow: hidden;">
        <Sidebar active="customer" />
        <el-main style="padding: 0; display: flex; flex-direction: column; background-color: #f5f7fa;">
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
              placeholder="请输入您的问题..."
              @keyup.enter.prevent="sendMessage"
              :rows="3"
              type="textarea"
              resize="none"
              :disabled="loading"
            />
            <div class="input-footer">
              <span class="tip">按 Enter 发送</span>
              <el-button type="primary" @click="sendMessage" :disabled="!inputMsg.trim() || loading">发送</el-button>
            </div>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Loading } from '@element-plus/icons-vue'
import axios from 'axios'
import Sidebar from '../components/Sidebar.vue'

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

const goHome = () => {
  router.push('/')
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

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
.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-wrapper {
  display: flex;
  gap: 12px;
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
  gap: 4px;
}

.user .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.loading-bubble {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  background-color: #fff;
}

.service .message-bubble {
  background-color: #fff;
  color: #303133;
  border-bottom-left-radius: 2px;
}

.user .message-bubble {
  background-color: #409eff;
  color: #fff;
  border-bottom-right-radius: 2px;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.input-area {
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #dcdfe6;
}

.input-footer {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

.tip {
  font-size: 12px;
  color: #909399;
}
</style>
