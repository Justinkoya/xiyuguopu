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
    console.error('保存购物车失败', e)
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

function cartKey(item) {
  return (item.itemType || 'PRODUCT') + ':' + (item.packageCode || item.productId || item.id)
}

function addToCart(productId, name, price, unit, image, quantity, options) {
  const cart = getCart()
  const item = {
    itemType: (options && options.itemType) || 'PRODUCT',
    productId,
    packageCode: options && options.packageCode,
    name,
    price: price || 0,
    unit: unit || '500g',
    image: image || '',
    quantity,
    checked: true
  }
  item.cartKey = cartKey(item)
  const existing = cart.find(i => (i.cartKey || cartKey(i)) === item.cartKey)
  if (existing) {
    existing.quantity += quantity
  } else {
    cart.push(item)
  }
  saveCart(cart)
}

function removeCartItems(items) {
  if (!items || items.length === 0) return
  const keySet = new Set(items.map(i => i.cartKey || cartKey(i)))
  saveCart(getCart().filter(i => !keySet.has(i.cartKey || cartKey(i))))
}

module.exports = {
  getCart,
  saveCart,
  updateCartBadge,
  cartKey,
  addToCart,
  removeCartItems
}
