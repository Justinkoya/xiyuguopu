const api = require('../../../utils/api.js')

Page({
  data: {
    items: [],
    subtotal: 0,
    shipping: 0,
    total: 0,
    address: null,
    remark: '',
    source: 'cart'
  },

  onLoad(options) {
    const items = JSON.parse(decodeURIComponent(options.items || '[]')).map(i => ({
      ...i,
      itemType: i.itemType || 'PRODUCT',
      cartKey: i.cartKey || `${i.itemType || 'PRODUCT'}:${i.packageCode || i.productId || i.id}`
    }))
    const source = options.source || 'cart'
    const subtotal = items.reduce((s, i) => s + i.price * i.quantity, 0)
    this.setData({
      items,
      source,
      subtotal: subtotal.toFixed(1),
      total: subtotal.toFixed(1)
    })
    this.loadDefaultAddress()
  },

  async loadDefaultAddress() {
    try {
      const addresses = await api.getAddresses()
      const defaultAddr = addresses.find(a => a.isDefault) || addresses[0]
      if (defaultAddr) {
        this.setData({ address: defaultAddr })
      }
    } catch (err) {
      console.error('加载地址失败:', err)
    }
  },

  onChooseAddress() {
    wx.navigateTo({ url: '/pages/address/list/index?select=true' })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  async onSubmit() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }
    if (this.data.items.length === 0) {
      wx.showToast({ title: '订单为空', icon: 'none' })
      return
    }

    wx.showLoading({ title: '提交中...' })

    try {
      // Step 1: 创建订单
      const order = await api.createOrder({
        addressId: this.data.address.id,
        items: this.data.items.map(i => {
          const itemType = i.itemType || 'PRODUCT'
          return {
            itemType,
            productId: itemType === 'PACKAGE' ? null : (i.productId || i.id),
            packageCode: itemType === 'PACKAGE' ? (i.packageCode || i.code || i.id) : i.packageCode,
            quantity: i.quantity
          }
        }),
        remark: this.data.remark
      })

      wx.hideLoading()

      // 从购物车中移除已购买的商品（保留未勾选的）
      api.removeCartItems(this.data.items)

      wx.redirectTo({ url: `/pages/order/pay/index?id=${order.id}` })
    } catch (err) {
      wx.hideLoading()
      console.error('提交订单失败:', err)
      wx.showToast({ title: err.message || '下单失败，请重试', icon: 'none' })
    }
  }
})
