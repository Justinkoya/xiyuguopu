const http = require('./http-client.js')
const adapter = require('./data-adapter.js')
const cart = require('./cart-store.js')

function login() {
  let code = wx.getStorageSync('mock_code')
  if (!code) {
    code = 'mp_user_' + Date.now()
    wx.setStorageSync('mock_code', code)
  }
  return http.post('/wx/login', { code }).then(res => {
    if (res && res.token) {
      http.setToken(res.token)
      return res
    }
    throw new Error('登录返回无效 token')
  })
}

function getCategories() {
  return http.get('/categories').then(list => (list || []).map(adapter.toCategoryItem))
}

function getCategoryItems(categoryCode) {
  return http.get('/categories/' + categoryCode + '/items').then(list => (list || []).map(adapter.toCatalogItem))
}

function getProducts(categoryCode) {
  return http.get('/products', categoryCode ? { categoryCode } : {})
    .then(list => (list || []).map(adapter.toProductItem))
}

function getProductDetail(id) {
  return http.get('/products/' + id).then(adapter.toProductItem)
}

function getFeaturedProducts() {
  return http.get('/products', { featured: true }).then(list => (list || []).map(adapter.toProductItem))
}

function getPackages() {
  return http.get('/packages').then(list => (list || []).map(adapter.toPackageItem))
}

function getPackageDetail(code) {
  return http.get('/packages/' + code).then(adapter.toPackageItem)
}

function getFeaturedPackages() {
  return http.get('/packages', { featured: true }).then(list => (list || []).map(adapter.toPackageItem))
}

function getScorecards() {
  return http.get('/scorecards')
}

function getCostBreakdown() {
  return http.get('/cost-breakdown')
}

function getAddresses() {
  return http.get('/user/addresses').then(list => (list || []).map(adapter.toAddressItem))
}

function getAddressDetail(id) {
  return http.get('/user/addresses/' + id).then(adapter.toAddressItem)
}

function addAddress(data) {
  return http.post('/user/addresses', {
    name: data.name,
    phone: data.phone,
    province: data.province || '',
    city: data.city || '',
    district: data.district || '',
    detail: data.detail,
    isDefault: data.isDefault || false
  })
}

function updateAddress(id, data) {
  return http.put('/user/addresses/' + id, {
    name: data.name,
    phone: data.phone,
    province: data.province || '',
    city: data.city || '',
    district: data.district || '',
    detail: data.detail,
    isDefault: data.isDefault || false
  })
}

function deleteAddress(id) {
  return http.del('/user/addresses/' + id)
}

function createOrder(data) {
  return http.post('/orders', {
    addressId: data.addressId,
    items: (data.items || []).map(i => ({
      itemType: i.itemType || 'PRODUCT',
      productId: (i.itemType || 'PRODUCT') === 'PACKAGE' ? null : (i.productId || i.id),
      packageCode: i.packageCode,
      quantity: i.quantity
    })),
    remark: data.remark || ''
  })
}

function getOrders(status, page, size) {
  const p = page || 1
  const s = size || 10
  return http.get('/orders', { page: p, size: s }).then(res => {
    const orders = (res && res.records) ? res.records : (Array.isArray(res) ? res : [])
    let result = orders.map(adapter.toOrderItem)
    if (status && status !== 'all') {
      result = result.filter(o => o.status === status)
    }
    return {
      list: result,
      page: (res && res.current) || p,
      total: (res && res.total) || result.length,
      pages: (res && res.pages) || 1,
      hasMore: (res && res.current) ? res.current < res.pages : false
    }
  })
}

function getOrderDetail(id) {
  return http.get('/orders/' + id).then(adapter.toOrderItem)
}

function cancelOrder(id) {
  return http.post('/orders/' + id + '/cancel')
}

function pay(orderId) {
  return http.post('/wx/pay', { orderId })
}

function payCallback(orderId, transactionId) {
  return http.post('/wx/pay-callback', { orderId, transactionId })
}

module.exports = {
  login,
  getToken: http.getToken,
  setToken: http.setToken,
  imageUrl: http.imageUrl,
  getCategories,
  getCategoryItems,
  getProducts,
  getProductDetail,
  getFeaturedProducts,
  getPackages,
  getPackageDetail,
  getFeaturedPackages,
  getAddresses,
  getAddressDetail,
  addAddress,
  updateAddress,
  deleteAddress,
  createOrder,
  getOrders,
  getOrderDetail,
  cancelOrder,
  pay,
  payCallback,
  getCart: cart.getCart,
  saveCart: cart.saveCart,
  addToCart: cart.addToCart,
  removeCartItems: cart.removeCartItems,
  getScorecards,
  getCostBreakdown,
  mergeProductExtras: adapter.mergeProductExtras,
  toProductItem: adapter.toProductItem,
  toOrderItem: adapter.toOrderItem,
  toAddressItem: adapter.toAddressItem,
  toScorecardData: adapter.toScorecardData,
  toCostBreakdownData: adapter.toCostBreakdownData
}
