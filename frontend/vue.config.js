const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000,
    proxy: {
      // 后端业务接口代理
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      },
      // 上传文件访问代理
      // 浏览器请求 /uploads/xxx → 重写为 /api/uploads/xxx → 后端 ResourceHandler 返回
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/uploads': '/api/uploads'
        }
      }
    }
  }
})