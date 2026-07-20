module.exports = {
  root: true,
  env: {
    node: true
  },
  'extends': [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    // 临时关闭一些规则，方便开发
    'vue/no-unused-components': 'off',
    'no-unused-vars': 'off',
    'vue/multi-word-component-names': 'off'  // 关闭组件名必须多词的规则
  }
} 