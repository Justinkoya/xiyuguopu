const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    userInfo: null,
    orderCounts: {
      UNPAID: 0,
      PAID: 0,
      SHIPPED: 0
    }
  },

  onShow() {
    this.setData({ userInfo: app.globalData.userInfo })
    this.loadOrderCounts()
  },

  async loadOrderCounts() {
    try {
      // 取前 50 条统计各状态数量（用户中心 badge 不需要全量精确）
      const result = await api.getOrders(null, 1, 50)
      const counts = { UNPAID: 0, PAID: 0, SHIPPED: 0 }
      for (const o of result.list) {
        if (counts.hasOwnProperty(o.status)) {
          counts[o.status]++
        }
      }
      this.setData({ orderCounts: counts })
    } catch (err) {
      console.error('加载订单数量失败:', err)
    }
  },

  onTapOrders(e) {
    const status = e.currentTarget.dataset.status || 'all'
    wx.navigateTo({ url: `/pages/order/list/index?status=${status}` })
  },

  onTapAddress() {
    wx.navigateTo({ url: '/pages/address/list/index' })
  },

  onTapStory(e) {
    const tab = e.currentTarget.dataset.tab || 'story'
    wx.navigateTo({ url: `/pages/story/index?tab=${tab}` })
  },

  onContact() {
    wx.showModal({
      title: '联系客服',
      content: '微信：xiyuguopu\n工作时间：工作日 9:00-21:00',
      confirmText: '复制微信号',
      success: (res) => {
        if (res.confirm) {
          wx.setClipboardData({
            data: 'xiyuguopu',
            success: () => wx.showToast({ title: '已复制', icon: 'success' })
          })
        }
      }
    })
  },

  onShareAppMessage() {
    return {
      title: '西域果脯 - 一口西域，一份实在',
      path: '/pages/index/index'
    }
  }
})
