<template>
  <div class="coffee-wiki page-container">
    <div class="wiki-header">
      <h1>咖啡百科</h1>
      <p>探索咖啡的无限魅力，了解每一杯背后的故事</p>
    </div>

    <el-tabs v-model="activeTab" class="wiki-tabs">
      <el-tab-pane label="精品科普" name="knowledge">
        <el-row :gutter="24">
          <el-col :span="8" v-for="(article, index) in knowledgeArticles" :key="index">
            <el-card class="article-card" :body-style="{ padding: '0px' }" shadow="hover">
              <div class="image-wrapper">
                <img :src="article.image" class="article-image" />
                <div class="category-tag">{{ article.category }}</div>
              </div>
              <div class="article-content">
                <h3>{{ article.title }}</h3>
                <p>{{ article.summary }}</p>
                <div class="article-meta">
                  <span><el-icon><Calendar /></el-icon> {{ article.date }}</span>
                  <span><el-icon><View /></el-icon> {{ article.views }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
      
      <el-tab-pane label="冲煮指南" name="brewing">
        <el-timeline>
          <el-timeline-item
            v-for="(step, index) in brewingSteps"
            :key="index"
            :timestamp="step.title"
            placement="top"
            type="primary"
            size="large"
          >
            <el-card>
              <h4>{{ step.subtitle }}</h4>
              <p>{{ step.content }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-tab-pane>

      <el-tab-pane label="常见问题 FAQ" name="faq">
        <el-collapse v-model="activeFaq">
          <el-collapse-item title="你们的咖啡豆是哪里产的？" name="1">
            <div>我们的咖啡豆精选自埃塞俄比亚、哥伦比亚、巴西等全球知名的高海拔优质产区。每一批次都经过严格筛选，确保风味纯正。</div>
          </el-collapse-item>
          <el-collapse-item title="下单后多久可以送达？" name="2">
            <div>同城订单通常在下单后1-2小时内由专属骑手送达；咖啡豆及周边周边产品通过顺丰冷链发货，一般次日可达。</div>
          </el-collapse-item>
          <el-collapse-item title="深度烘焙和浅度烘焙有什么区别？" name="3">
            <div>浅度烘焙保留了咖啡豆更多的原始果酸和花香，口感明亮；深度烘焙则更能激发咖啡豆内部的焦糖和巧克力风味，口感醇厚，适合搭配牛奶。</div>
          </el-collapse-item>
          <el-collapse-item title="如何保存购买的咖啡豆？" name="4">
            <div>建议将咖啡豆存放在阴凉、干燥、避光的地方，使用单向排气阀的密封罐保存。不建议放入冰箱，以免受潮影响风味。最佳赏味期为烘焙后30天内。</div>
          </el-collapse-item>
        </el-collapse>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Calendar, View } from '@element-plus/icons-vue'

const activeTab = ref('knowledge')
const activeFaq = ref(['1'])

const knowledgeArticles = [
  {
    title: '认识咖啡风味轮：如何品鉴一杯好咖啡',
    summary: '从果酸到坚果香，带你读懂SCA咖啡风味轮，让你的味蕾不再迷茫。',
    image: '/static/picture/xuanchuan5.jpg',
    category: '品鉴技巧',
    date: '2026-03-15',
    views: '12.5k'
  },
  {
    title: '意式拼配与单品豆的区别究竟在哪？',
    summary: '为什么意式浓缩通常使用拼配豆？单品手冲又有什么独特魅力？',
    image: '/static/picture/xuanchuan6.jpg',
    category: '豆种科普',
    date: '2026-02-28',
    views: '8.3k'
  },
  {
    title: '燕麦奶与咖啡的完美邂逅',
    summary: '植物基奶潮来袭，为什么燕麦奶能成为咖啡馆的新宠？揭秘其背后的风味密码。',
    image: '/static/picture/xuanchuan7.jpg',
    category: '潮流饮品',
    date: '2026-01-10',
    views: '15.2k'
  }
]

const brewingSteps = [
  { title: 'Step 1: 研磨', subtitle: '选择合适的研磨度', content: '手冲咖啡建议使用中等粗细的研磨度，类似白砂糖的颗粒大小。研磨过细容易导致过萃，口感苦涩；过粗则容易萃取不足，口感偏酸水。' },
  { title: 'Step 2: 润湿滤纸与温杯', subtitle: '细节决定成败', content: '用热水将滤纸均匀打湿，不仅能去除滤纸的纸味，还能提前温热分享壶和咖啡杯，让咖啡的风味更好地保留。' },
  { title: 'Step 3: 闷蒸', subtitle: '唤醒咖啡豆', content: '倒入少量热水（约为咖啡粉重量的2倍），让咖啡粉充分吸收水分并排出二氧化碳。闷蒸时间通常在30秒左右。' },
  { title: 'Step 4: 注水萃取', subtitle: '控制水流与节奏', content: '从中心开始，以画同心圆的方式缓慢注水。控制好水流的稳定，通常分两到三次注水，整个萃取过程控制在2分钟到2分半钟。' }
]
</script>

<style scoped>
.coffee-wiki {
  padding-top: 40px;
}

.wiki-header {
  text-align: center;
  margin-bottom: 40px;
}

.wiki-header h1 {
  font-size: 36px;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.wiki-header p {
  font-size: 16px;
  color: var(--el-text-color-secondary);
}

.wiki-tabs {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.article-card {
  margin-bottom: 24px;
  transition: transform 0.3s;
  cursor: pointer;
}

.article-card:hover {
  transform: translateY(-5px);
}

.image-wrapper {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.article-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.article-card:hover .article-image {
  transform: scale(1.05);
}

.category-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  background: var(--el-color-primary);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.article-content {
  padding: 16px;
}

.article-content h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  line-height: 1.4;
  height: 50px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.article-content p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-collapse-item__header) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-collapse-item__content) {
  font-size: 14px;
  color: var(--el-text-color-regular);
  line-height: 1.8;
  padding-bottom: 20px;
}
</style>
