const api = require('../../../utils/api.js')

Page({
  data: {
    id: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    region: ['北京市', '北京市', '东城区'],
    detail: '',
    isDefault: false,
    isEdit: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ isEdit: true, id: options.id })
      this.loadAddress(options.id)
    }
  },

  async loadAddress(id) {
    try {
      const addr = await api.getAddressDetail(id)
      const province = addr.province || '北京市'
      const city = addr.city || '北京市'
      const district = addr.district || '东城区'
      this.setData({
        name: addr.name,
        phone: addr.phone,
        province,
        city,
        district,
        region: [province, city, district],
        detail: addr.detail || '',
        isDefault: addr.isDefault || false
      })
    } catch (err) {
      console.error('加载地址失败:', err)
      wx.showToast({ title: '加载地址失败', icon: 'none' })
    }
  },

  onNameInput(e) { this.setData({ name: e.detail.value }) },
  onPhoneInput(e) { this.setData({ phone: e.detail.value }) },
  onDetailInput(e) { this.setData({ detail: e.detail.value }) },

  onRegionChange(e) {
    const val = e.detail.value
    this.setData({
      region: val,
      province: val[0],
      city: val[1],
      district: val[2]
    })
  },

  onToggleDefault() {
    this.setData({ isDefault: !this.data.isDefault })
  },

  async onSave() {
    const { name, phone, province, city, district, detail, isDefault } = this.data
    if (!name || !phone) {
      wx.showToast({ title: '请填写姓名和电话', icon: 'none' })
      return
    }
    if (!detail) {
      wx.showToast({ title: '请填写详细地址', icon: 'none' })
      return
    }

    const payload = {
      name,
      phone,
      province: province || '请选择',
      city: city || '',
      district: district || '',
      detail,
      isDefault
    }

    try {
      if (this.data.isEdit) {
        await api.updateAddress(this.data.id, payload)
      } else {
        await api.addAddress(payload)
      }
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (err) {
      console.error('保存地址失败:', err)
      wx.showToast({ title: '保存失败', icon: 'none' })
    }
  }
})
