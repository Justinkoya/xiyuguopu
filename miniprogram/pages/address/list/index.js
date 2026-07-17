const api = require('../../../utils/api.js')

Page({
  data: {
    addresses: [],
    selectMode: false
  },

  onLoad(options) {
    if (options.select === 'true') {
      this.setData({ selectMode: true })
    }
  },

  onShow() {
    this.loadAddresses()
  },

  async loadAddresses() {
    try {
      const addresses = await api.getAddresses()
      this.setData({ addresses })
    } catch (err) {
      console.error('加载地址失败:', err)
    }
  },

  // 选择地址（从订单确认页跳转过来时）
  onSelect(e) {
    if (!this.data.selectMode) return
    const index = e.currentTarget.dataset.index
    const addr = this.data.addresses[index]
    const pages = getCurrentPages()
    const prevPage = pages[pages.length - 2]
    if (prevPage) {
      prevPage.setData({ address: addr })
    }
    wx.navigateBack()
  },

  onAdd() {
    wx.navigateTo({ url: '/pages/address/edit/index' })
  },

  onEdit(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/address/edit/index?id=${id}` })
  },

  async onDelete(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认删除',
      content: '确定要删除此地址吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.deleteAddress(id)
            this.loadAddresses()
            wx.showToast({ title: '已删除', icon: 'success' })
          } catch (err) {
            wx.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      }
    })
  },

  async onSetDefault(e) {
    const id = e.currentTarget.dataset.id
    const addr = this.data.addresses.find(a => a.id === id)
    if (!addr) return
    try {
      await api.updateAddress(id, {
        name: addr.name,
        phone: addr.phone,
        province: addr.province,
        city: addr.city,
        district: addr.district,
        detail: addr.detail,
        isDefault: true
      })
      this.loadAddresses()
      wx.showToast({ title: '已设为默认', icon: 'success' })
    } catch (err) {
      wx.showToast({ title: '设置失败', icon: 'none' })
    }
  }
})
