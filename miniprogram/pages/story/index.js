Page({
  data: {
    activeTab: 'story',
    tabs: [
      { key: 'story', label: '品牌故事' },
      { key: 'standard', label: '6星标准' },
      { key: 'origin', label: '产地溯源' },
      { key: 'price', label: '价格透明' }
    ],
    standards: [
      { name: '产地透明', stars: '⭐⭐⭐⭐⭐⭐', desc: '标注到县城级别，扫码可查果园信息' },
      { name: '颗粒均匀', stars: '⭐⭐⭐⭐⭐',   desc: '大小一致，空壳率<3%，碎粒率<5%' },
      { name: '干湿适口', stars: '⭐⭐⭐⭐⭐',   desc: '水分含量控制在最佳区间，不过干不潮' },
      { name: '洁净免洗', stars: '⭐⭐⭐⭐⭐⭐', desc: '无沙土、无杂质、无虫蛀，开袋即食' },
      { name: '口感稳定', stars: '⭐⭐⭐⭐⭐',   desc: '每批次盲测对比，确保口感不漂移' },
      { name: '配料纯净', stars: '⭐⭐⭐⭐⭐⭐', desc: '只有果实本身，无添加剂、无糖精、无硫熏' }
    ]
  },

  onLoad(options) {
    if (options.tab) {
      this.setData({ activeTab: options.tab })
    }
  },

  onTabChange(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab })
  },

  onShareAppMessage() {
    return {
      title: '西域果脯 - 敢给你看产地底牌的新疆干果品牌',
      path: '/pages/story/index'
    }
  }
})
