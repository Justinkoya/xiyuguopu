const api = require('./utils/api.js')

App({
  onLaunch() {
    // 自动 mock 登录，存 Promise 供页面等待
    this.globalData.loginReady = this.autoLogin()
  },

  async autoLogin() {
    try {
      const res = await api.login()
      console.log('自动登录成功:', res.user)
      this.globalData.userInfo = res.user
      this.globalData.isLogin = true
    } catch (err) {
      console.warn('自动登录失败，重试中...:', err.message)
      // 1秒后重试一次（后端可能还没启动）
      try {
        await new Promise(r => setTimeout(r, 1000))
        const res = await api.login()
        console.log('重试登录成功:', res.user)
        this.globalData.userInfo = res.user
        this.globalData.isLogin = true
      } catch (e) {
        console.error('登录最终失败:', e.message)
        // 不阻塞页面加载——未登录也能看商品（公开接口）
      }
    }
  },

  globalData: {
    // 品牌信息
    brand: {
      name: '西域果脯',
      slogan: '一口西域，一份实在',
      subtitle: '敢给你看产地底牌的新疆干果品牌',
      wechat: 'xiyuguopu'
    },
    // 品牌色板
    colors: {
      gold: '#F3C45B',
      red: '#B64A2E',
      green: '#1F6F5C',
      sand: '#D88831',
      cream: '#F8F1E3',
      dark: '#2E241C',
      brown: '#7A4A2A',
      light: '#FFF8EA',
      soft: '#F5E6D0',
      copper: '#C4774A',
      olive: '#0D4A3E'
    },
    // 用户信息（自动登录后填充）
    userInfo: null,
    isLogin: false,
    // 购物车数量
    cartCount: 0,
    // 分类选择标记
    selectedCategoryCode: null
  }
})
