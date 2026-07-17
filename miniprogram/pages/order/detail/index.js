const api = require('../../../utils/api.js')
const util = require('../../../utils/util.js')

Page({
  data: {
    util,
    order: null,
    loading: true
  },

  onLoad(options) {
    this.loadOrder(options.id)
  },

  async loadOrder(id) {
    try {
      const order = this.formatOrder(await api.getOrderDetail(id))
      this.setData({ order, loading: false })
    } catch (err) {
      console.error('加载订单详情失败:', err)
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  formatOrder(order) {
    if (!order) return null
    return {
      ...order,
      statusText: order.statusText || util.getOrderStatusText(order.status),
      statusColor: order.statusColor || util.getOrderStatusColor(order.status),
      createdAtText: order.createdAtText && order.createdAtText !== '--'
        ? order.createdAtText
        : util.formatTime(order.createdAt || order.createTime || order.created_time),
      paidAtText: order.paidAtText && order.paidAtText !== '--'
        ? order.paidAtText
        : util.formatTime(order.paidAt || order.paidTime || order.paid_time),
      shippedAtText: order.shippedAtText && order.shippedAtText !== '--'
        ? order.shippedAtText
        : util.formatTime(order.shippedAt),
      completedAtText: order.completedAtText && order.completedAtText !== '--'
        ? order.completedAtText
        : util.formatTime(order.completedAt),
      cancelledAtText: order.cancelledAtText && order.cancelledAtText !== '--'
        ? order.cancelledAtText
        : util.formatTime(order.cancelledAt)
    }
  },

  onCopyOrderNo() {
    util.copyText(this.data.order.orderNo)
    wx.showToast({ title: '已复制订单号', icon: 'success' })
  },

  onCopyWechat() {
    util.copyText('xiyuguopu')
    wx.showToast({ title: '已复制微信号', icon: 'success' })
  }
})
