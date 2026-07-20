<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎使用流浪动物救助系统</h1>
      <p class="welcome-subtitle">管理员仪表盘</p>
    </div>

    <!-- 概览数据卡片 -->
    <el-row :gutter="20" class="data-overview">
      <el-col :xs="24" :sm="12" :md="6" v-for="(card, index) in dataCards" :key="index">
        <el-card class="data-card" :class="card.class">
          <div class="card-icon">
            <el-icon><component :is="card.icon" /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">{{ card.label }}</div>
            <div class="card-value">{{ card.value }}</div>
            <div class="card-desc">{{ card.description }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-section">
      <!-- 动物种类分布 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>动物种类分布</span>
              <el-tooltip content="展示不同种类动物的数量分布" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="chart-container" ref="categoryChart"></div>
        </el-card>
      </el-col>

      <!-- 领养状态分布 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>领养状态分布</span>
              <el-tooltip content="展示待领养/已领养/救助中的动物数量" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="chart-container" ref="adoptionChart"></div>
        </el-card>
      </el-col>

      <!-- 健康状况分布 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>健康状况分布</span>
              <el-tooltip content="展示不同健康状态的动物数量" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="chart-container" ref="healthChart"></div>
        </el-card>
      </el-col>

      <!-- 领养申请状态 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>领养申请状态</span>
              <el-tooltip content="展示待审核/通过/拒绝的申请数量" placement="top">
                <el-icon><InfoFilled /></el-icon>
              </el-tooltip>
            </div>
          </template>
          <div class="chart-container" ref="applicationChart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, onBeforeUnmount } from 'vue';
import { getDashboardStats } from '@/api/admin';
import * as echarts from 'echarts/core';
import { 
  TitleComponent, 
  TooltipComponent, 
  LegendComponent,
  GridComponent,
  ToolboxComponent
} from 'echarts/components';
import { PieChart, BarChart } from 'echarts/charts';
import { LabelLayout } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
import { 
  Tickets, 
  HomeFilled, 
  User, 
  FirstAid, 
  InfoFilled 
} from '@element-plus/icons-vue';

// 注册ECharts组件
echarts.use([
  TitleComponent, 
  TooltipComponent, 
  LegendComponent,
  GridComponent,
  ToolboxComponent,
  PieChart,
  BarChart,
  LabelLayout,
  CanvasRenderer
]);

// 图表引用
const categoryChart = ref(null);
const adoptionChart = ref(null);
const healthChart = ref(null);
const applicationChart = ref(null);

// 数据卡片
const dataCards = reactive([
  { 
    label: '动物总数', 
    value: 0, 
    icon: 'Tickets', 
    class: 'animal-card',
    description: '系统中登记的所有流浪动物数量' 
  },
  { 
    label: '用户总数', 
    value: 0, 
    icon: 'User', 
    class: 'user-card',
    description: '所有注册用户的总人数' 
  },
  { 
    label: '领养申请', 
    value: 0, 
    icon: 'HomeFilled', 
    class: 'adoption-card',
    description: '待处理的领养申请数量' 
  },
  { 
    label: '救助信息', 
    value: 0, 
    icon: 'FirstAid', 
    class: 'rescue-card',
    description: '当前登记的救助信息数量' 
  }
]);

// 图表实例
let charts = {
  category: null,
  adoption: null,
  health: null,
  application: null
};

// 获取统计数据
const fetchDashboardStats = async () => {
  try {
    const response = await getDashboardStats();
    if(response && response.code === 200 && response.data) {
      const statsData = response.data;
      
      // 更新数据卡片
      if (statsData.totalCounts) {
        dataCards[0].value = statsData.totalCounts.animalCount || 0;
        dataCards[1].value = statsData.totalCounts.userCount || 0;
        dataCards[2].value = statsData.totalCounts.adoptionCount || 0;
        dataCards[3].value = statsData.totalCounts.rescueCount || 0;
      }
      
      // 初始化图表
      initCharts(statsData);
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
  }
};

// 初始化图表
const initCharts = (statsData) => {
  // 动物种类分布饼图
  if (categoryChart.value) {
    charts.category = echarts.init(categoryChart.value);
    charts.category.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        type: 'scroll',
        formatter: (name) => {
          const data = statsData.categoryStats || [];
          const targetData = data.find(item => item.name === name);
          return targetData ? `${name}: ${targetData.value}` : name;
        }
      },
      series: [
        {
          name: '动物种类',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: statsData.categoryStats || []
        }
      ]
    });
  }

  // 领养状态饼图
  if (adoptionChart.value) {
    charts.adoption = echarts.init(adoptionChart.value);
    charts.adoption.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        formatter: (name) => {
          const data = statsData.adoptionStats || [];
          const targetData = data.find(item => item.name === name);
          return targetData ? `${name}: ${targetData.value}` : name;
        }
      },
      series: [
        {
          name: '领养状态',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: statsData.adoptionStats || [],
          color: ['#91cc75', '#5470c6', '#fac858']
        }
      ]
    });
  }

  // 健康状况饼图
  if (healthChart.value) {
    charts.health = echarts.init(healthChart.value);
    charts.health.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: 10,
        top: 'center',
        formatter: (name) => {
          const data = statsData.healthStats || [];
          const targetData = data.find(item => item.name === name);
          return targetData ? `${name}: ${targetData.value}` : name;
        }
      },
      series: [
        {
          name: '健康状况',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: statsData.healthStats || [],
          color: ['#91cc75', '#fac858', '#ee6666', '#73c0de']
        }
      ]
    });
  }

  // 领养申请状态柱状图
  if (applicationChart.value) {
    charts.application = echarts.init(applicationChart.value);
    const applicationData = statsData.applicationStats || [];
    charts.application.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: applicationData.map(item => item.name)
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '申请数量',
          type: 'bar',
          barWidth: '60%',
          data: applicationData.map(item => item.value),
          itemStyle: {
            color: function(params) {
              const colorList = ['#fac858', '#91cc75', '#ee6666'];
              return colorList[params.dataIndex];
            }
          }
        }
      ]
    });
  }
};

// 处理窗口大小变化，重新调整图表大小
const handleResize = () => {
  Object.values(charts).forEach(chart => {
    chart && chart.resize();
  });
};

// 生命周期钩子
onMounted(() => {
  fetchDashboardStats();
  window.addEventListener('resize', handleResize);
});

// 销毁前清除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  Object.values(charts).forEach(chart => {
    chart && chart.dispose();
  });
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.welcome-section {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 0;
  border-radius: 8px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 10px;
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.8;
}

.data-overview {
  margin-bottom: 20px;
}

.data-card {
  height: 150px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  margin-bottom: 20px;
  overflow: hidden;
  border: none;
  position: relative;
}

.card-icon {
  width: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.card-content {
  flex: 1;
  padding: 15px 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.card-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 8px;
  line-height: 1;
}

.card-desc {
  font-size: 14px;
  opacity: 0.8;
  margin-top: 5px;
}

.animal-card {
  background: linear-gradient(135deg, #7b4397, #dc2430);
  color: white;
}

.user-card {
  background: linear-gradient(135deg, #00c6fb, #005bea);
  color: white;
}

.adoption-card {
  background: linear-gradient(135deg, #f09819, #edde5d);
  color: white;
}

.rescue-card {
  background: linear-gradient(135deg, #11998e, #38ef7d);
  color: white;
}

.chart-section {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: none;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 16px;
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .data-card {
    height: 130px;
  }
  
  .card-title {
    font-size: 18px;
  }
  
  .card-value {
    font-size: 28px;
  }
  
  .card-icon {
    font-size: 32px;
    width: 70px;
  }
  
  .card-desc {
    font-size: 12px;
  }
  
  .chart-container {
    height: 250px;
  }
}
</style> 