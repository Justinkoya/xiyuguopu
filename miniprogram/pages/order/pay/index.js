const api = require('../../../utils/api.js')

Page({
  data: {
    orderId: null,
    order: null,
    loading: true,
    paying: false
  },

  onLoad(options) {
    this.setData({ orderId: options.id })
    this.loadOrder(options.id)
  },

  async loadOrder(id) {
    try {
      const order = await api.getOrderDetail(id)
      this.setData({ order, loading: false })
    } catch (err) {
      console.error('加载支付订单失败:', err)
      this.setData({ loading: false })
      wx.showToast({ title: '订单加载失败', icon: 'none' })
    }
  },

  async onPay() {
    if (!this.data.order || this.data.order.status !== 'UNPAID') {
      wx.showToast({ title: '订单状态不可支付', icon: 'none' })
      return
    }

    this.setData({ paying: true })
    wx.showLoading({ title: '支付中...' })

    try {
      await api.pay(this.data.order.id)
      const txnId = 'txn_mock_' + Date.now()
      await api.payCallback(this.data.order.id, txnId)
      wx.hideLoading()
      wx.showToast({ title: '支付成功', icon: 'success' })
      setTimeout(() => {
        wx.redirectTo({ url: `/pages/order/detail/index?id=${this.data.order.id}` })
      }, 800)
    } catch (err) {
      wx.hideLoading()
      console.error('支付失败:', err)
      wx.showToast({ title: err.message || '支付失败', icon: 'none' })
      this.setData({ paying: false })
    }
  },

  onPayLater() {
    wx.redirectTo({ url: '/pages/order/list/index?status=UNPAID' })
  }
})
