const ENV = 'prod'
const CFG = {
  dev: {
    baseUrl: 'http://localhost/api',
    imageBase: 'http://localhost/images'
  },
  prod: {
    baseUrl: 'http://47.109.93.162/api',
    imageBase: 'http://47.109.93.162/images'
  }
}[ENV]

function imageUrl(path, fallback) {
  if (!path) return fallback || CFG.imageBase + '/assortment.png'
  if (path.startsWith('http')) return path
  const clean = path.replace(/\\/g, '/').replace(/^\//, '').replace(/^images\//, '')
  return CFG.imageBase + '/' + clean
}

function getToken() {
  return wx.getStorageSync('token') || ''
}

function setToken(token) {
  wx.setStorageSync('token', token)
}

function request(method, path, data) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const header = { 'Content-Type': 'application/json' }
    if (token) header.Authorization = 'Bearer ' + token

    wx.request({
      url: CFG.baseUrl + path,
      method,
      header,
      data,
      success(res) {
        if (res.statusCode === 200) {
          const body = res.data
          if (body && typeof body.code !== 'undefined') {
            if (body.code === 200) {
              resolve(body.data)
            } else {
              reject(new Error(body.message || body.msg || '请求失败'))
            }
          } else {
            resolve(body)
          }
          return
        }
        if (res.statusCode === 401) {
          wx.removeStorageSync('token')
          reject(new Error('登录已过期，请重新打开小程序'))
          return
        }
        reject(new Error('请求失败: ' + res.statusCode))
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

function post(path, data) {
  return request('POST', path, data)
}

function put(path, data) {
  return request('PUT', path, data)
}

function del(path) {
  return request('DELETE', path)
}

module.exports = {
  CFG,
  imageUrl,
  getToken,
  setToken,
  request,
  get,
  post,
  put,
  del
}
