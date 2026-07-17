const api = require('../../../utils/api.js')
const util = require('../../../utils/util.js')

Page({
  data: {
    util,
    currentTab: 'all',
    tabs: [
      { key: 'all', label: '全部' },
      { key: 'UNPAID', label: '待付款' },
      { key: 'PAID', label: '待发货' },
      { key: 'SHIPPED', label: '待收货' }
    ],
    orders: [],
    loading: true,
    page: 1,
    hasMore: true
  },

  onLoad(options) {
    if (options.status) {
      this.setData({ currentTab: options.status })
    }
  },

  onShow() {
    this.loadOrders()
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadMore()
    }
  },

  async loadOrders() {
    this.setData({ loading: true, page: 1, orders: [] })
    try {
      const status = this.data.currentTab === 'all' ? null : this.data.currentTab
      const result = await api.getOrders(status, 1)
      this.setData({ orders: result.list, loading: false, page: 1, hasMore: result.hasMore })
    } catch (err) {
      console.error('加载订单失败:', err)
      this.setData({ loading: false })
    }
  },

  async loadMore() {
    const nextPage = this.data.page + 1
    this.setData({ loading: true })
    try {
      const status = this.data.currentTab === 'all' ? null : this.data.currentTab
      const result = await api.getOrders(status, nextPage)
      this.setData({
        orders: [...this.data.orders, ...result.list],
        loading: false,
        page: nextPage,
        hasMore: result.hasMore
      })
    } catch (err) {
      console.error('加载更多订单失败:', err)
      this.setData({ loading: false })
    }
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ currentTab: tab })
    this.loadOrders()
  },

  onTapOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order/detail/index?id=${id}` })
  },

  async onCancel(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '取消订单',
      content: '确定要取消此订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.cancelOrder(id)
            this.loadOrders()
            wx.showToast({ title: '已取消', icon: 'success' })
          } catch (err) {
            wx.showToast({ title: '取消失败', icon: 'none' })
          }
        }
      }
    })
  },

  // 支付（未付款订单重新发起支付）
  async onPay(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order/pay/index?id=${id}` })
  }
})
