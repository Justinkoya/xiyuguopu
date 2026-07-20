const api = require('../../utils/api.js')

Page({
  data: {
    banners: [
      {
        id: 1,
        image: api.imageUrl('assortment.png'),
        title: '一口西域，一份实在',
        subtitle: '敢给你看产地底牌的新疆干果品牌'
      },
      {
        id: 2,
        image: api.imageUrl('orchard.png'),
        title: '来自天山脚下的自然馈赠',
        subtitle: '180天自然生长 · 传统晾晒工艺'
      },
      {
        id: 3,
        image: api.imageUrl('market.png'),
        title: '产地直发 · 品质看得见',
        subtitle: '4大核心产区 · 30+合作果园'
      }
    ],
    quickEntries: [
      { icon: '📖', text: '品牌故事', path: '/pages/story/index' },
      { icon: '🌍', text: '产地溯源', path: '/pages/story/index?tab=origin' },
      { icon: '⭐', text: '6星标准', path: '/pages/story/index?tab=standard' },
      { icon: '💰', text: '价格透明', path: '/pages/story/index?tab=price' }
    ],
    featuredProducts: [],
    giftSets: [],
    personas: [
      {
        name: '办公室白领',
        icon: '💼',
        desc: '下午3点，抽屉里的健康狙击手',
        rec: '酸奶巴旦木、西梅干、开心果',
        color: 'red'
      },
      {
        name: '家庭日常',
        icon: '🏠',
        desc: '全家人的零食守护者',
        rec: '小白杏干、纸皮核桃、无花果干',
        color: 'green'
      },
      {
        name: '节日送礼',
        icon: '🎁',
        desc: '会用礼物说话的人',
        rec: '尊享礼盒、企业定制装',
        color: 'sand'
      },
      {
        name: '银发养生',
        icon: '🍵',
        desc: '退休后的品质慢生活',
        rec: '无花果干、纸皮核桃',
        color: 'copper'
      }
    ],
    cartCount: 0
  },

  onLoad() {
    // 等登录就绪后再加载数据（首页先于登录完成渲染）
    const app = getApp()
    const ready = app.globalData.loginReady || Promise.resolve()
    ready.then(() => this.loadData())
  },

  onShow() {
    this.updateCartCount()
  },

  async loadData() {
    try {
      const [featuredProducts, giftSets] = await Promise.all([
        api.getFeaturedProducts(),
        api.getFeaturedPackages()
      ])

      this.setData({ featuredProducts, giftSets })
    } catch (err) {
      console.error('加载首页数据失败:', err)
      wx.showToast({ title: '加载失败，请确认后端已启动', icon: 'none' })
    }
  },

  updateCartCount() {
    const cart = api.getCart()
    const count = cart.reduce((sum, item) => sum + item.quantity, 0)
    this.setData({ cartCount: count })
  },

  // 事件处理
  onTapProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product/detail/index?id=${id}` })
  },

  onTapCategory(e) {
    const code = e.currentTarget.dataset.code
    wx.switchTab({ url: '/pages/category/index' })
    const app = getApp()
    app.globalData.selectedCategoryCode = code
  },

  onTapGiftSet(e) {
    const code = e.currentTarget.dataset.code || e.currentTarget.dataset.id
    if (!code) {
      wx.showToast({ title: '套餐信息缺失', icon: 'none' })
      return
    }
    wx.navigateTo({ url: `/pages/product/detail/index?id=${code}&type=gift` })
  },

  onTapQuickEntry(e) {
    const path = e.currentTarget.dataset.path
    wx.navigateTo({ url: path })
  },

  // 分享
  onShareAppMessage() {
    return {
      title: '西域果脯 - 一口西域，一份实在',
      path: '/pages/index/index',
      imageUrl: api.imageUrl('assortment.png')
    }
  }
})
