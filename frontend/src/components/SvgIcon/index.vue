<template>
  <el-icon :size="size" :color="color">
    <component :is="name" />
  </el-icon>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  name: {
    type: String,
    required: true
  },
  size: {
    type: [String, Number],
    default: 16
  },
  color: {
    type: String,
    default: ''
  }
})
</script> 