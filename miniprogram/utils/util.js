// 通用工具函数

// 格式化价格
function formatPrice(price) {
  return Number(price).toFixed(1)
}

// 生成评分星星字符串
function getStarRating(score) {
  return Math.round(score)
}

// 计算套餐节省金额
function calcSavings(individualTotal, setPrice) {
  return (individualTotal - setPrice).toFixed(1)
}

// 订单状态映射（前端旧值 + 后端值）
const orderStatusMap = {
  pending: '待付款',
  paid: '待发货',
  shipped: '待收货',
  received: '已完成',
  cancelled: '已取消',
  refunded: '已退款',
  UNPAID: '待付款',
  PAID: '待发货',
  SHIPPED: '待收货',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
}

function getOrderStatusText(status) {
  return orderStatusMap[status] || '未知状态'
}

function getOrderStatusColor(status) {
  const map = {
    pending: '#B64A2E',
    paid: '#D88831',
    shipped: '#1F6F5C',
    received: '#999999',
    cancelled: '#CCCCCC',
    refunded: '#CCCCCC',
    UNPAID: '#B64A2E',
    PAID: '#D88831',
    SHIPPED: '#1F6F5C',
    COMPLETED: '#999999',
    CANCELLED: '#CCCCCC'
  }
  return map[status] || '#999999'
}

// 时间格式化
function parseTime(value) {
  if (!value) return null
  if (value instanceof Date) return isNaN(value.getTime()) ? null : value
  if (typeof value === 'number') {
    const date = new Date(value)
    return isNaN(date.getTime()) ? null : date
  }
  if (Array.isArray(value)) {
    const date = new Date(
      value[0],
      (value[1] || 1) - 1,
      value[2] || 1,
      value[3] || 0,
      value[4] || 0,
      value[5] || 0
    )
    return isNaN(date.getTime()) ? null : date
  }
  if (typeof value === 'string') {
    const match = value.match(/^(\d{4})-(\d{1,2})-(\d{1,2})(?:[ T](\d{1,2}):(\d{1,2})(?::(\d{1,2}))?)?/)
    if (match) {
      return new Date(
        Number(match[1]),
        Number(match[2]) - 1,
        Number(match[3]),
        Number(match[4] || 0),
        Number(match[5] || 0),
        Number(match[6] || 0)
      )
    }
    const date = new Date(value)
    return isNaN(date.getTime()) ? null : date
  }
  return null
}

function formatTime(value) {
  const date = parseTime(value)
  if (!date) return '--'
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  const ss = String(date.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${d} ${hh}:${mm}:${ss}`
}

// 防抖
function debounce(fn, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

// 拨打电话
function makePhoneCall(phone) {
  wx.makePhoneCall({ phoneNumber: phone })
}

// 复制文字
function copyText(text) {
  return new Promise((resolve, reject) => {
    wx.setClipboardData({
      data: text,
      success: resolve,
      fail: reject
    })
  })
}

// 生成订单号
function generateOrderNo() {
  const now = new Date()
  const date = now.toISOString().slice(0, 10).replace(/-/g, '')
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0')
  return `XY${date}${random}`
}

module.exports = {
  formatPrice,
  getStarRating,
  calcSavings,
  getOrderStatusText,
  getOrderStatusColor,
  formatTime,
  generateOrderNo,
  debounce,
  makePhoneCall,
  copyText
}
