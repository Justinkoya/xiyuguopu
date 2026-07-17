const api = require('../../utils/api.js')

Page({
  data: {
    cartItems: [],
    allChecked: true,
    totalPrice: 0,
    totalCount: 0,
    isEmpty: true
  },

  onShow() {
    this.loadCart()
  },

  loadCart() {
    const items = api.getCart()
    const isEmpty = items.length === 0
    this.setData({ cartItems: items, isEmpty })
    this.calcTotal()
  },

  onToggleCheck(e) {
    const index = e.currentTarget.dataset.index
    const items = this.data.cartItems
    items[index].checked = !items[index].checked
    const allChecked = items.every(i => i.checked)
    this.setData({ cartItems: items, allChecked })
    this.calcTotal()
    api.saveCart(items)
  },

  onToggleAll() {
    const allChecked = !this.data.allChecked
    const items = this.data.cartItems.map(i => ({ ...i, checked: allChecked }))
    this.setData({ cartItems: items, allChecked })
    this.calcTotal()
    api.saveCart(items)
  },

  onMinus(e) {
    const index = e.currentTarget.dataset.index
    const items = this.data.cartItems
    if (items[index].quantity > 1) {
      items[index].quantity -= 1
      this.setData({ cartItems: items })
      this.calcTotal()
      api.saveCart(items)
    }
  },

  onPlus(e) {
    const index = e.currentTarget.dataset.index
    const items = this.data.cartItems
    items[index].quantity += 1
    this.setData({ cartItems: items })
    this.calcTotal()
    api.saveCart(items)
  },

  onDelete(e) {
    const index = e.currentTarget.dataset.index
    wx.showModal({
      title: '提示',
      content: '确定要移除此商品吗？',
      success: (res) => {
        if (res.confirm) {
          const items = this.data.cartItems
          items.splice(index, 1)
          const isEmpty = items.length === 0
          this.setData({ cartItems: items, isEmpty })
          this.calcTotal()
          api.saveCart(items)
        }
      }
    })
  },

  calcTotal() {
    const checkedItems = this.data.cartItems.filter(i => i.checked)
    const totalPrice = checkedItems.reduce((sum, i) => sum + i.price * i.quantity, 0)
    const totalCount = checkedItems.reduce((sum, i) => sum + i.quantity, 0)
    const allChecked = this.data.cartItems.length > 0 && this.data.cartItems.every(i => i.checked)
    this.setData({
      totalPrice: Math.round(totalPrice * 10) / 10,
      totalCount,
      allChecked
    })
  },

  onCheckout() {
    const checkedItems = this.data.cartItems.filter(i => i.checked)
    if (checkedItems.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' })
      return
    }
    const items = encodeURIComponent(JSON.stringify(checkedItems))
    wx.navigateTo({ url: `/pages/order/confirm/index?items=${items}&source=cart` })
  },

  onGoShop() {
    wx.switchTab({ url: '/pages/category/index' })
  }
})
