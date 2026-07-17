// ============================================================
// API 封装层 — 替代云开发 db.js，直连 Spring Boot 后端
// ============================================================

const util = require('./util.js')

const ENV = 'dev'                                 // 'dev' | 'prod' — 上线时改这里
const CFG = {
  dev: {
    baseUrl: 'http://localhost/api',              // 走 Nginx :80 → 反代到 :8080
    imageBase: 'http://localhost/images',         // 走 Nginx 静态文件
  },
  prod: {
    baseUrl: 'https://api.xiyuguopu.com/api',
    imageBase: 'https://api.xiyuguopu.com/images',
  }
}[ENV]

// ========== 辅助 ==========

/** 拼接完整图片 URL（dev → localhost, prod → CDN 域名） */
function imageUrl(path, fallback) {
  if (!path) return fallback || CFG.imageBase + '/assortment.png'
  if (path.startsWith('http')) return path        // 已是完整 URL
  // 去重 images/ 前缀，统一拼接
  const clean = path.replace(/\\/g, '/').replace(/^\//, '').replace(/^images\//, '')
  return CFG.imageBase + '/' + clean
}

// ========== Token 管理 ==========

function getToken() {
  return wx.getStorageSync('token') || ''
}

function setToken(token) {
  wx.setStorageSync('token', token)
}

// ========== HTTP 请求封装 ==========

/**
 * 通用请求
 * 后端统一返回 Result<T> = { code: 200, msg: "success", data: ... }
 * 这里自动解包取 .data
 */
function request(method, path, data) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const header = { 'Content-Type': 'application/json' }
    if (token) {
      header['Authorization'] = 'Bearer ' + token
    }

    wx.request({
      url: CFG.baseUrl + path,
      method,
      header,
      data,
      success(res) {
        if (res.statusCode === 200) {
          const body = res.data
          // 后端统一 Result 包装
          if (body && typeof body.code !== 'undefined') {
            if (body.code === 200) {
              resolve(body.data)
            } else {
              reject(new Error(body.msg || '请求失败'))
            }
          } else {
            resolve(body)
          }
        } else if (res.statusCode === 401) {
          // token 过期，清除并提示
          wx.removeStorageSync('token')
          reject(new Error('登录已过期，请重新打开小程序'))
        } else {
          reject(new Error('请求失败: ' + res.statusCode))
        }
      },
      fail(err) {
        console.error('网络请求失败:', err)
        reject(new Error('网络连接失败，请检查后端服务是否启动'))
      }
    })
  })
}

function get(path, data) {
  let url = path
  if (data) {
    const params = Object.keys(data)
      .filter(k => data[k] !== null && data[k] !== undefined && data[k] !== '')
      .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(data[k]))
      .join('&')
    if (params) url += '?' + params
  }
  return request('GET', url)
}

function post(path, data) { return request('POST', path, data) }
function put(path, data) { return request('PUT', path, data) }
function del(path) { return request('DELETE', path) }

// ========== 字段映射适配 ==========

function toProductItem(raw) {
  if (!raw) return null
  const img = imageUrl(raw.image)
  return {
    _id: raw.id,
    id: raw.id,
    name: raw.name,
    price: raw.price,
    unit: raw.unit,
    images: img ? [img] : [],
    image: img,
    tags: raw.tags || [],
    badge: raw.badge,
    description: raw.description,
    highlight: raw.highlight,
    highlightText: raw.highlightText,
    extra: raw.extra,
    stock: raw.stock,
    isOnSale: raw.isOnSale,
    // 兼容旧模板字段
    subtitle: raw.description || '',               // 详情页用 subtitle
    origin: parseOrigin(raw.extra),
    scorecard: null,                                // 6星评分 — mergeProductExtras() 填充
    costBreakdown: null,                            // 价格透明 — mergeProductExtras() 填充
    ingredients: null,                              // 配料表 — mergeProductExtras() 填充
    sales: raw.sale || raw.sales || raw.salesVolume || raw.saleCount || 0
  }
}

/** 解析 extra 字段：JSON { region, orchard, harvestSeason } 或纯文本 → region */
function parseOrigin(extra) {
  if (!extra) return {}
  try {
    const obj = JSON.parse(extra)
    return {
      region: obj.region || '',
      orchard: obj.orchard || '',
      harvestSeason: obj.harvestSeason || ''
    }
  } catch (e) {
    // 纯文本 → 当产地
    return { region: String(extra), orchard: '', harvestSeason: '' }
  }
}

function toCategoryItem(raw) {
  if (!raw) return null
  return {
    _id: raw.id,
    id: raw.id,
    code: raw.code,
    name: raw.name,
    description: '',
    slug: raw.code,
    color: raw.themeColor || '#C4774A',
    icon: '',
    products: []
  }
}

function toAddressItem(raw) {
  if (!raw) return null
  return {
    _id: raw.id,
    id: raw.id,
    name: raw.name,
    phone: raw.phone,
    province: raw.province || '',
    city: raw.city || '',
    district: raw.district || '',
    detail: raw.detail || '',
    isDefault: raw.isDefault,
    createdAt: raw.createdAt
  }
}

function toOrderItem(raw) {
  if (!raw) return null
  const createdAt = raw.createdAt || raw.createTime || raw.created_time || raw.created_at
  const paidAt = raw.paidAt || raw.paidTime || raw.paid_time || raw.paid_at
  const shippedAt = raw.shippedAt || raw.shippedTime || raw.shipped_time || raw.shipped_at
  const completedAt = raw.completedAt || raw.completedTime || raw.completed_time || raw.completed_at
  const cancelledAt = raw.cancelledAt || raw.cancelledTime || raw.cancelled_time || raw.cancelled_at

  return {
    _id: raw.id,
    id: raw.id,
    orderNo: raw.orderNo,
    userId: raw.userId,
    status: raw.status,                         // UNPAID/PAID/SHIPPED/COMPLETED/CANCELLED
    statusText: util.getOrderStatusText(raw.status),
    statusColor: util.getOrderStatusColor(raw.status),
    total: raw.totalAmount,                     // 模板用 total
    totalAmount: raw.totalAmount,
    subtotal: raw.totalAmount,
    shipping: 0,
    totalCount: (raw.items || []).reduce((sum, i) => sum + (i.quantity || 0), 0),
    items: (raw.items || []).map(i => ({
      productId: i.productId,
      name: i.productName,                      // 模板用 name
      productName: i.productName,
      price: i.price,
      quantity: i.quantity,
      unit: '',
      image: ''
    })),
    address: raw.addressSnapshot || {},         // 模板用 address.xxx
    addressSnapshot: raw.addressSnapshot,
    remark: raw.remark || '',
    wxTransactionId: raw.wxTransactionId,
    createdAt,
    createdAtText: util.formatTime(createdAt),
    paidAt,
    paidAtText: util.formatTime(paidAt),
    shippedAt,
    shippedAtText: util.formatTime(shippedAt),
    completedAt,
    completedAtText: util.formatTime(completedAt),
    cancelledAt,
    cancelledAtText: util.formatTime(cancelledAt)
  }
}

function toPackageItem(raw) {
  if (!raw) return null
  // 套餐图: code 对应的图片文件名
  const pkgImages = {
    trial: imageUrl('assortment.png'),
    gift: imageUrl('gift-scene.png'),
    family: imageUrl('assortment.png')
  }
  const img = pkgImages[raw.code] || imageUrl('assortment.png')
  return {
    _id: raw.code,
    id: raw.code,
    code: raw.code,
    name: raw.name,
    subtitle: raw.subtitle || '',
    description: raw.subtitle || '',
    price: raw.price,
    unit: '500g',
    image: img,
    images: [img],                               // 详情页轮播用
    tags: [],
    origin: {},
    scorecard: null,                             // 套餐无评分
    costBreakdown: null,
    ingredients: null,
    items: raw.items || [],
    badge: raw.badge,
    isHot: raw.featured || !!raw.badge,
    extra: raw.extra
  }
}

// ========== 评分卡 / 成本透明 适配 ==========

// 维度中文标签 → 模板 key 映射
const DIM_LABEL_MAP = {
  '产地透明': 'originTransparency',
  '颗粒均匀': 'sizeUniformity',
  '干湿适口': 'moisture',
  '洁净免洗': 'cleanliness',
  '口感稳定': 'taste',
  '配料纯净': 'purity'
}

// 成本项中文标签 → 模板 key 映射
const COST_LABEL_MAP = {
  '产地收购价': 'purchasePrice',
  '冷链物流': 'logistics',
  '包装+分拣': 'packaging',
  '快递包邮': 'delivery',
  '我们利润': 'profit'
}

function toScorecardData(vo) {
  if (!vo) return null
  const data = { overall: vo.totalScore || 0 }
  ;(vo.dimensions || []).forEach(d => {
    const key = DIM_LABEL_MAP[d.label] || d.label
    data[key] = d.score || 0
  })
  return data
}

function toCostBreakdownData(vo) {
  if (!vo) return null
  const data = { profit: vo.profit || 0 }
  ;(vo.items || []).forEach(item => {
    const key = COST_LABEL_MAP[item.label] || item.label
    data[key] = item.amount || 0
  })
  return data
}

/**
 * 按 productId 从列表中查找并合并 scorecard / costBreakdown 到 product 对象
 */
function mergeProductExtras(product, scorecards, costBreakdowns) {
  if (!product) return product
  const id = product.id
  const sc = (scorecards || []).find(s => s.productId === id)
  const cb = (costBreakdowns || []).find(c => c.productId === id)
  // 配料表：有 scorecard 则用 ingredientText 拆分数组，否则保持原值
  let ingredients = product.ingredients
  if (sc && sc.ingredientText) {
    ingredients = sc.ingredientText.split(/[,，、\n]/).map(s => s.trim()).filter(Boolean)
  }
  return {
    ...product,
    scorecard: sc ? toScorecardData(sc) : null,
    costBreakdown: cb ? toCostBreakdownData(cb) : null,
    ingredients
  }
}

// ========== API 方法 ==========

// --- 登录 ---
function login() {
  // 生成一个持久的 mock code
  let code = wx.getStorageSync('mock_code')
  if (!code) {
    code = 'mp_user_' + Date.now()
    wx.setStorageSync('mock_code', code)
  }
  return post('/wx/login', { code }).then(res => {
    if (res && res.token) {
      setToken(res.token)
      return res
    }
    throw new Error('登录返回无效 token')
  })
}

// --- 分类 ---
function getCategories() {
  return get('/categories').then(list => (list || []).map(toCategoryItem))
}

// --- 商品 ---
function getProducts(categoryCode) {
  return get('/products', categoryCode ? { categoryCode } : {})
    .then(list => (list || []).map(toProductItem))
}

function getProductDetail(id) {
  return get('/products/' + id).then(toProductItem)
}

// --- 套餐 ---
function getPackages() {
  return get('/packages').then(list => (list || []).map(toPackageItem))
}

function getPackageDetail(code) {
  return getPackages().then(list => list.find(p => p.code === code) || null)
}

// --- 评分卡 & 成本透明 ---
function getScorecards() {
  return get('/scorecards')
}

function getCostBreakdown() {
  return get('/cost-breakdown')
}

// --- 地址 ---
function getAddresses() {
  return get('/user/addresses').then(list => (list || []).map(toAddressItem))
}

function getAddressDetail(id) {
  return get('/user/addresses/' + id).then(toAddressItem)
}

function addAddress(data) {
  return post('/user/addresses', {
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
  return put('/user/addresses/' + id, {
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
  return del('/user/addresses/' + id)
}

// --- 订单 ---
function createOrder(data) {
  return post('/orders', {
    addressId: data.addressId,
    items: (data.items || []).map(i => ({
      productId: i.productId || i.id,
      quantity: i.quantity
    })),
    remark: data.remark || ''
  })
}

function getOrders(status, page, size) {
  const p = page || 1
  const s = size || 10
  return get('/orders', { page: p, size: s }).then(res => {
    // 后端 MyBatis-Plus Page: { records, total, current, size, pages }
    const orders = (res && res.records) ? res.records : (Array.isArray(res) ? res : [])
    let result = orders.map(toOrderItem)
    // 前端按状态过滤（后端暂不支持 status 参数过滤）
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
  return get('/orders/' + id).then(toOrderItem)
}

function cancelOrder(id) {
  return post('/orders/' + id + '/cancel')
}

// --- 支付 ---
function pay(orderId) {
  return post('/wx/pay', { orderId })
}

function payCallback(orderId, transactionId) {
  return post('/wx/pay-callback', { orderId, transactionId })
}

// ========== 购物车（本地存储） ==========

function getCart() {
  try {
    return wx.getStorageSync('cart') || []
  } catch (e) {
    return []
  }
}

function saveCart(items) {
  try {
    wx.setStorageSync('cart', items)
    updateCartBadge(items)
  } catch (e) {
    console.error('保存购物车失败:', e)
  }
}

function updateCartBadge(items) {
  const app = getApp()
  if (!app) return
  const count = (items || []).reduce((s, i) => s + (i.quantity || 0), 0)
  app.globalData.cartCount = count
  if (count > 0) {
    wx.setTabBarBadge({ index: 2, text: String(Math.min(count, 99)) })
  } else {
    wx.removeTabBarBadge({ index: 2 })
  }
}

/** 加入购物车（已存在则累加数量） */
function addToCart(productId, name, price, unit, image, quantity) {
  const cart = getCart()
  const existing = cart.find(i => i.productId === productId)
  if (existing) {
    existing.quantity += quantity
  } else {
    cart.push({ productId, name, price: price || 0, unit: unit || '500g', image: image || '', quantity, checked: true })
  }
  saveCart(cart)
}

/** 结算后移除已购商品 */
function removeCartItems(productIds) {
  if (!productIds || productIds.length === 0) return
  const idSet = new Set(productIds)
  saveCart(getCart().filter(i => !idSet.has(i.productId)))
}

module.exports = {
  // 核心
  login,
  getToken,
  setToken,
  // 分类
  getCategories,
  // 商品
  getProducts,
  getProductDetail,
  // 套餐
  getPackages,
  getPackageDetail,
  // 地址
  getAddresses,
  getAddressDetail,
  addAddress,
  updateAddress,
  deleteAddress,
  // 订单
  createOrder,
  getOrders,
  getOrderDetail,
  cancelOrder,
  // 支付
  pay,
  payCallback,
  // 购物车
  getCart,
  saveCart,
  addToCart,
  removeCartItems,
  // 评分卡 & 成本透明
  getScorecards,
  getCostBreakdown,
  mergeProductExtras,
  // 适配器
  toProductItem,
  toOrderItem,
  toAddressItem,
  toScorecardData,
  toCostBreakdownData
}
