import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import path from 'path'
import svgLoader from 'vite-svg-loader'

// https://vite.dev/config/

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    // element-plus按需导入
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [
          // 配置element-Plus采用sass样式配置系统
          ElementPlusResolver({importStyle:"sass"})
      ],
    }),
    svgLoader()
  ],
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/css/index.scss" as *;`,
      },
    },
  },
  resolve: {
    alias: {
      // '@': fileURLToPath(new URL('./src', import.meta.url))
      '@': path.resolve(__dirname, 'src')
    },
  },
})

// const { defineConfig } = require('@vue/cli-service')
// module.exports = defineConfig({
//
//   transpileDependencies: true,
//   lintOnSave:false,
//   devServer: {
//     historyApiFallback: true,
//     allowedHosts: "all",
//   }
// })
