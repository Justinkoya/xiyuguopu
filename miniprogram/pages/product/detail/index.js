const api = require('../../../utils/api.js')
const util = require('../../../utils/util.js')

Page({
  data: {
    productType: 'product',
    product: null,
    loading: true,
    currentImageIndex: 0,
    quantity: 1,
    showCartTip: false,
    // 6星维度名
    dimensions: [
      { key: 'originTransparency', label: '产地透明' },
      { key: 'sizeUniformity', label: '颗粒均匀' },
      { key: 'moisture', label: '干湿适口' },
      { key: 'cleanliness', label: '洁净免洗' },
      { key: 'taste', label: '口感稳定' },
      { key: 'purity', label: '配料纯净' }
    ],
    // 成本项名
    costItems: [
      { key: 'purchasePrice', label: '产地收购价' },
      { key: 'logistics', label: '冷链物流' },
      { key: 'packaging', label: '包装+分拣' },
      { key: 'delivery', label: '快递包邮' },
      { key: 'profit', label: '我们利润' }
    ]
  },

  onLoad(options) {
    const { id, type } = options
    this.setData({ productType: type || 'product' })
    this.loadProduct(id)
  },

  async loadProduct(id) {
    try {
      let product
      if (this.data.productType === 'gift') {
        product = await api.getPackageDetail(id)
      } else {
        // 并行加载商品详情 + 评分卡 + 成本透明
        const [prod, scorecards, costBreakdowns] = await Promise.all([
          api.getProductDetail(id),
          api.getScorecards(),
          api.getCostBreakdown()
        ])
        product = api.mergeProductExtras(prod, scorecards, costBreakdowns)
      }
      this.setData({ product, loading: false })
    } catch (err) {
      console.error('加载商品失败:', err)
      this.setData({ loading: false })
    }
  },

  // 轮播图切换
  onSwiperChange(e) {
    this.setData({ currentImageIndex: e.detail.current })
  },

  // 数量操作
  onMinus() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
    }
  },
  onPlus() {
    if (this.data.quantity < 99) {
      this.setData({ quantity: this.data.quantity + 1 })
    }
  },

  // 加入购物车
  onAddToCart() {
    api.addToCart(
      this.data.product.id,
      this.data.product.name,
      this.data.product.price,
      this.data.product.unit,
      this.data.product.image || (this.data.product.images && this.data.product.images[0]),
      this.data.quantity,
      {
        itemType: this.data.productType === 'gift' ? 'PACKAGE' : 'PRODUCT',
        packageCode: this.data.productType === 'gift' ? this.data.product.code : undefined
      }
    )
    this.setData({ showCartTip: true })
    setTimeout(() => this.setData({ showCartTip: false }), 1500)
  },

  // 立即购买
  onBuyNow() {
    const item = {
      itemType: this.data.productType === 'gift' ? 'PACKAGE' : 'PRODUCT',
      productId: this.data.product.id,
      packageCode: this.data.productType === 'gift' ? this.data.product.code : undefined,
      name: this.data.product.name,
      image: this.data.product.image || (this.data.product.images && this.data.product.images[0]) || '',
      price: this.data.product.price,
      unit: this.data.product.unit || '500g',
      quantity: this.data.quantity
    }
    item.cartKey = `${item.itemType}:${item.packageCode || item.productId}`
    const orderItems = encodeURIComponent(JSON.stringify([item]))
    wx.navigateTo({ url: `/pages/order/confirm/index?items=${orderItems}&source=direct` })
  },

  // 去购物车
  onGoCart() {
    wx.switchTab({ url: '/pages/cart/index' })
  },

  // 分享
  onShareAppMessage() {
    const product = this.data.product || {}
    const isGift = this.data.productType === 'gift'
    const id = isGift ? product.code : product.id
    const unit = product.unit || (isGift ? '套' : '500g')
    const typeParam = isGift ? '&type=gift' : ''
    return {
      title: `${product.name || '西域果脯'} - ¥${product.price || 0}/${unit}`,
      path: `/pages/product/detail/index?id=${id || ''}${typeParam}`,
      imageUrl: product.image || ''
    }
  }
})
