<template>
  <svg
      :width="size"
      :height="size"
      viewBox="0 0 120 120"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      class="cloud-ops-logo"
  >
    <!-- 渐变定义 -->
    <defs>
      <linearGradient id="cloudGradient" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" :stop-color="colors.primaryStart"/>
        <stop offset="100%" :stop-color="colors.primaryEnd"/>
      </linearGradient>
    </defs>

    <!-- 主体云朵 -->
    <path
        d="M65 35C75 35 85 40 85 55C95 55 105 65 105 75C105 85 95 95 85 95H35C25 95 15 85 15 75C15 65 25 55 35 55C35 40 45 35 55 35C60 35 65 35 65 35Z"
        fill="url(#cloudGradient)"
    />

    <!-- 服务器模块 -->
    <rect x="50" y="60" width="20" height="20" rx="3" :fill="colors.module"/>

    <!-- 数据流箭头 -->
    <path
        d="M60 80L55 75H65L60 80ZM60 80V70"
        :stroke="colors.dataFlow"
        stroke-width="2"
        fill="none"
    />

    <!-- 微光粒子 -->
    <circle cx="75" cy="50" r="1.5" fill="white"/>
    <circle cx="85" cy="60" r="1.2" fill="white"/>
  </svg>
</template>

<script>
export default {
  props: {
    size: {
      type: [Number, String],
      default: 120
    },
    colors: {
      type: Object,
      default: () => ({
        primaryStart: '#6EC3F7',
        primaryEnd: '#3B8BEB',
        module: '#2C3E50',
        dataFlow: '#2ECC71'
      })
    }
  }
}
</script>

<style scoped>
.cloud-ops-logo {
  /* 添加悬停动效 */
  transition: transform 0.3s ease;
}
.cloud-ops-logo:hover {
  transform: scale(1.05);
}
</style>