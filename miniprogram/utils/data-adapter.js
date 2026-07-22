const util = require('./util.js')
const { imageUrl } = require('./http-client.js')

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
    return { region: String(extra), orchard: '', harvestSeason: '' }
  }
}

function toProductItem(raw) {
  if (!raw) return null
  const img = imageUrl(raw.image)
  return {
    _id: raw.id || raw.productId,
    id: raw.id || raw.productId,
    itemType: raw.itemType || 'PRODUCT',
    productId: raw.productId || raw.id,
    packageCode: raw.packageCode,
    name: raw.name,
    price: raw.price,
    unit: raw.unit,
    images: img ? [img] : [],
    image: img,
    tags: raw.tags || [],
    badge: raw.badge,
    description: raw.description || raw.subtitle || '',
    featured: raw.featured,
    highlight: raw.highlight,
    highlightText: raw.highlightText,
    extra: raw.extra,
    stock: raw.stock,
    hasStock: raw.stock !== undefined && raw.stock !== null,
    isOnSale: raw.isOnSale,
    subtitle: raw.description || raw.subtitle || '',
    origin: parseOrigin(raw.extra),
    scorecard: null,
    costBreakdown: null,
    ingredients: null,
    sales: raw.sale || raw.sales || raw.salesVolume || raw.saleCount || 0
  }
}

function toCategoryItem(raw) {
  if (!raw) return null
  const colorMap = {
    blue: '#2563EB',
    amber: '#D88831',
    green: '#059669',
    red: '#B64A2E',
    purple: '#7C3AED',
    pink: '#DB2777',
    orange: '#EA580C',
    teal: '#0D9488',
    cyan: '#0891B2',
    lime: '#65A30D',
    brown: '#8B5E34',
    slate: '#475569'
  }
  return {
    _id: raw.id,
    id: raw.id,
    code: raw.code,
    entryType: raw.entryType || raw.entry_type || 'PRODUCT',
    isEnabled: raw.isEnabled !== false && raw.is_enabled !== false,
    name: raw.name,
    description: '',
    slug: raw.code,
    color: colorMap[raw.themeColor] || raw.themeColor || '#C4774A',
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
    status: raw.status,
    statusText: util.getOrderStatusText(raw.status),
    statusColor: util.getOrderStatusColor(raw.status),
    total: raw.totalAmount,
    totalAmount: raw.totalAmount,
    subtotal: raw.totalAmount,
    shipping: 0,
    totalCount: (raw.items || []).reduce((sum, i) => sum + (i.quantity || 0), 0),
    items: (raw.items || []).map((i, index) => ({
      itemType: i.itemType || 'PRODUCT',
      productId: i.productId,
      packageCode: i.packageCode,
      orderItemKey: `${i.itemType || 'PRODUCT'}:${i.packageCode || i.productId || index}`,
      name: i.productName,
      productName: i.productName,
      price: i.price,
      quantity: i.quantity,
      unit: '',
      image: ''
    })),
    address: raw.addressSnapshot || {},
    addressSnapshot: raw.addressSnapshot,
    remark: raw.remark || '',
    wxTransactionId: raw.wxTransactionId,
    trackingNumber: raw.trackingNumber || '',
    shippingCompany: raw.shippingCompany || '',
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
  const img = imageUrl(raw.image, imageUrl('assortment.png'))
  const packageItems = (raw.items || []).map(item => {
    if (typeof item === 'string') {
      const parts = item.trim().split(/\s+/)
      const quantity = parts.length > 1 ? parts.pop() : ''
      return { name: parts.join(' ') || item, quantity, text: item }
    }
    const name = item.productName || item.name || ''
    const quantity = item.quantity || ''
    return { name, quantity, text: `${name}${quantity ? ' ' + quantity : ''}` }
  }).filter(item => item.name)
  const itemCount = packageItems.length
  const tags = [
    raw.badge || '精选套餐',
    itemCount ? `${itemCount}款组合` : '',
    raw.featured ? '首页推荐' : '',
    '一键下单'
  ].filter(Boolean)

  return {
    _id: raw.code || raw.packageCode,
    id: raw.code || raw.packageCode,
    productId: null,
    itemType: 'PACKAGE',
    packageCode: raw.code || raw.packageCode,
    code: raw.code || raw.packageCode,
    name: raw.name,
    subtitle: raw.subtitle || '',
    description: raw.subtitle || raw.description || '',
    price: raw.price,
    unit: raw.unit || '套',
    stock: raw.stock,
    hasStock: raw.stock !== undefined && raw.stock !== null,
    sales: raw.sale || raw.sales || 0,
    image: img,
    images: [img],
    tags,
    origin: {},
    scorecard: null,
    costBreakdown: null,
    ingredients: null,
    items: packageItems.map(item => item.text),
    packageItems,
    itemCount,
    hasPackageItems: itemCount > 0,
    packageSummary: itemCount ? `${itemCount}款新疆干果组合` : '新疆干果组合',
    packageFeatures: [
      { title: '搭配省心', desc: '按场景配好，不用反复挑选' },
      { title: '整套购买', desc: '下单、购物车、支付都按套餐处理' },
      { title: '产地直发', desc: '和普通商品使用同一套图片与库存' }
    ],
    badge: raw.badge,
    isHot: raw.featured || !!raw.badge,
    extra: raw.extra
  }
}

const DIM_LABEL_MAP = {
  '产地透明': 'originTransparency',
  '颗粒均匀': 'sizeUniformity',
  '干湿适口': 'moisture',
  '洁净免洗': 'cleanliness',
  '口感稳定': 'taste',
  '配料纯净': 'purity'
}

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

function mergeProductExtras(product, scorecards, costBreakdowns) {
  if (!product) return product
  const id = product.id
  const sc = (scorecards || []).find(s => s.productId === id)
  const cb = (costBreakdowns || []).find(c => c.productId === id)
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

function toCatalogItem(raw) {
  if (!raw) return null
  return raw.itemType === 'PACKAGE' ? toPackageItem(raw) : toProductItem(raw)
}

module.exports = {
  imageUrl,
  toProductItem,
  toCategoryItem,
  toAddressItem,
  toOrderItem,
  toPackageItem,
  toCatalogItem,
  toScorecardData,
  toCostBreakdownData,
  mergeProductExtras
}
