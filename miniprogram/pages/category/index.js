const api = require('../../utils/api.js')

Page({
  data: {
    categories: [],
    activeCategoryCode: '',
    products: [],
    sortType: 'default',
    loading: true
  },

  onLoad() {
    this.loadCategories()
  },

  async loadCategories() {
    try {
      const categories = await api.getCategories()
      if (categories.length > 0) {
        const app = getApp()
        const selectedCode = app.globalData.selectedCategoryCode || categories[0].code
        this.setData({ categories, activeCategoryCode: selectedCode })
        this.loadProducts(selectedCode)
        app.globalData.selectedCategoryCode = null
      } else {
        this.setData({ loading: false })
      }
    } catch (err) {
      console.error('加载分类失败:', err)
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败，请确认后端已启动', icon: 'none' })
    }
  },

  async loadProducts(categoryCode) {
    this.setData({ loading: true })
    try {
      const products = await api.getProducts(categoryCode)
      const sorted = this.applySort(products)
      this.setData({ products: sorted, loading: false })
    } catch (err) {
      console.error('加载商品失败:', err)
      this.setData({ loading: false })
    }
  },

  // 切换分类
  onTapCategory(e) {
    const code = e.currentTarget.dataset.code
    if (code === this.data.activeCategoryCode) return
    this.setData({ activeCategoryCode: code })
    this.loadProducts(code)
  },

  // 排序
  onTapSort(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ sortType: type })
    const products = this.applySort([...this.data.products])
    this.setData({ products })
  },

  applySort(products) {
    switch (this.data.sortType) {
      case 'price-asc':
        return products.sort((a, b) => a.price - b.price)
      case 'price-desc':
        return products.sort((a, b) => b.price - a.price)
      case 'sales':
        return products.sort((a, b) => (b.sales || 0) - (a.sales || 0))
      default:
        return products
    }
  },

  onTapProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product/detail/index?id=${id}` })
  }
})
