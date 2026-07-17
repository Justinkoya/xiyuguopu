/**
 * 西域果铺 — 所有可配置数据
 * 改价格、改产品、改套餐，只需改这个文件
 * ================================================================
 */

// ==================== 产品数据 ====================
const PRODUCTS = {
  // 尝鲜精选（蓝色）
  trial: [
    {
      name: '纸皮核桃',
      price: 29.9,
      unit: '500g',
      image: 'images/walnut.png',
      badge: '初次尝鲜',
      tags: ['阿克苏185品种', '手捏即开'],
      desc: '壳薄如纸，果仁饱满不空壳',
      highlight: false
    },
    {
      name: '情人梅',
      price: 19.9,
      unit: '500g',
      image: 'images/lovers-plum.png',
      badge: '零嘴必备',
      tags: ['酸甜解腻', '无糖精'],
      desc: '果肉厚实，酸甜恰到好处',
      highlight: false
    },
    {
      name: '喀什西梅干',
      price: 25.9,
      unit: '500g',
      image: 'images/prune.png',
      badge: '网红人气',
      tags: ['自然晾晒', '糯香酸甜'],
      desc: '女生最爱，办公室零食人气王',
      highlight: false
    }
  ],

  // 西域珍品（琥珀色）
  premium: [
    {
      name: '大无花果干',
      price: 49.9,
      unit: '500g',
      image: 'images/fig.png',
      badge: '全网好价',
      tags: ['小品种稀缺', '低于1688价'],
      desc: '新疆小无花果，全网没几家有货',
      highlight: false
    },
    {
      name: '沙漠果',
      price: 49.9,
      unit: '500g',
      image: 'images/brazil.png',
      badge: '新奇体验',
      tags: ['99%人没吃过', '社交话题'],
      desc: '名字自带新疆画面感的神秘坚果',
      highlight: false
    },
    {
      name: '玫瑰切糕',
      price: 45.9,
      unit: '500g',
      image: 'images/rosecake.png',
      badge: '手工点心',
      tags: ['纯手工制作', '差异化极强'],
      desc: '玫瑰花+核桃仁，传统新疆手工点心',
      highlight: false
    },
    {
      name: '椰枣',
      price: 55.9,
      unit: '500g',
      image: 'images/dates.png',
      badge: '异域珍品',
      tags: ['新疆少数民族特产', '市面少见'],
      desc: '比巧克力还好吃的天然甜糯珍果',
      highlight: false
    },
    {
      name: '库车小白杏',
      price: 39.9,
      unit: '500g',
      image: 'images/apricot.png',
      badge: '库车产地',
      tags: ['一杏两吃', '产地标签'],
      desc: '新疆独有品种，杏肉软糯杏仁酥脆',
      highlight: false
    },
    {
      name: '奶枣巴旦木',
      price: 45.9,
      unit: '500g',
      image: 'images/date-almond.png',
      badge: '网红爆款',
      tags: ['红枣+巴旦木', '进价优势'],
      desc: '网红零食，年轻人疯狂回购',
      highlight: false
    }
  ],

  // 口碑好物（绿色）
  bestseller: [
    {
      name: '开心果',
      price: 59.9,
      unit: '500g',
      image: 'images/pistachio.png',
      desc: '颗颗自然开口，不漂白',
      highlight: true,
      highlightText: '回购首选',
      extra: '进价低于1688批发价'
    },
    {
      name: '酸奶巴旦木',
      price: 52.9,
      unit: '500g',
      image: 'images/yogurt-almond.png',
      desc: '酸奶包裹，酸甜酥脆口感',
      highlight: false,
      extra: ''
    },
    {
      name: '小白杏',
      price: 39.9,
      unit: '500g',
      image: 'images/apricot.png',
      desc: '配料只有杏肉，0添加',
      highlight: false,
      extra: '进价低于1688批发价'
    },
    {
      name: '夏威夷果',
      price: 49.9,
      unit: '500g',
      image: 'images/macadamia.png',
      desc: '奶香浓郁，圆润饱满',
      highlight: false,
      extra: ''
    }
  ]
};

// ==================== 套餐数据 ====================
const PACKAGES = [
  {
    id: 'trial',
    icon: '新',
    name: '尝鲜包',
    subtitle: '3款经典搭配 · 首次体验',
    price: 92.7,
    items: ['纸皮核桃 500g', '小白杏 500g', '西梅干 500g'],
    featured: false
  },
  {
    id: 'gift',
    icon: '礼',
    name: '尊享礼盒',
    subtitle: '6款精选 · 送礼自用',
    price: 281.4,
    items: ['开心果 500g', '无花果干 500g', '酸奶巴旦木 500g', '小白杏 500g', '西梅干 500g', '玫瑰切糕 500g'],
    featured: true,
    badge: '最受欢迎'
  },
  {
    id: 'family',
    icon: '家',
    name: '家庭囤货装',
    subtitle: '8款全包 · 够吃一个月',
    price: 327.2,
    items: ['尝鲜2款 + 口碑4款', '+ 凑单2款'],
    extra: '月均仅需327元',
    featured: false
  }
];

// ==================== 成本透明数据 ====================
const COST_BREAKDOWN = [
  {
    icon: '果',
    name: '开心果',
    weight: '500g',
    price: 59.9,
    costs: [
      { label: '喀什产地收购价', amount: 35.0, pct: '58.4%' },
      { label: '新疆→内地冷链物流', amount: 5.0, pct: '8.3%' },
      { label: '独立包装+人工分拣', amount: 4.0, pct: '6.7%' },
      { label: '快递包邮费', amount: 3.0, pct: '5.0%' },
    ],
    profit: 12.9,
    profitPct: '21.5%'
  },
  {
    icon: '杏',
    name: '小白杏',
    weight: '500g',
    price: 39.9,
    costs: [
      { label: '库车产地收购价', amount: 17.5, pct: '43.9%' },
      { label: '新疆→内地物流', amount: 5.0, pct: '12.5%' },
      { label: '独立包装+人工分拣', amount: 4.0, pct: '10.0%' },
      { label: '快递包邮费', amount: 3.0, pct: '7.5%' },
    ],
    profit: 10.4,
    profitPct: '26.1%'
  }
];

// ==================== 6星评分卡数据 ====================
const SCORECARDS = [
  {
    name: '开心果',
    origin: '喀什疏附县 · 自然开口',
    image: 'images/pistachio.png',
    total: 5.7,
    ingredient: '配料：开心果，食用盐',
    scores: [
      { label: '产地透明', value: 6.0, pct: 100 },
      { label: '颗粒均匀', value: 5.5, pct: 92 },
      { label: '干湿适口', value: 5.8, pct: 96 },
      { label: '洁净免洗', value: 6.0, pct: 100 },
      { label: '口感稳定', value: 5.4, pct: 90 },
      { label: '配料纯净', value: 6.0, pct: 100 }
    ]
  },
  {
    name: '小白杏干',
    origin: '库车 · 自然晾晒',
    image: 'images/apricot.png',
    total: 5.8,
    ingredient: '配料：杏肉（只有这一个）',
    scores: [
      { label: '产地透明', value: 6.0, pct: 100 },
      { label: '颗粒均匀', value: 5.5, pct: 92 },
      { label: '干湿适口', value: 5.8, pct: 96 },
      { label: '洁净免洗', value: 6.0, pct: 100 },
      { label: '口感稳定', value: 6.0, pct: 100 },
      { label: '配料纯净', value: 6.0, pct: 100 }
    ]
  },
  {
    name: '无花果干',
    origin: '喀什 · 传统晒制',
    image: 'images/fig.png',
    total: 5.5,
    ingredient: '配料：无花果（只有这一个）',
    scores: [
      { label: '产地透明', value: 6.0, pct: 100 },
      { label: '颗粒均匀', value: 5.2, pct: 87 },
      { label: '干湿适口', value: 5.5, pct: 92 },
      { label: '洁净免洗', value: 6.0, pct: 100 },
      { label: '口感稳定', value: 5.3, pct: 88 },
      { label: '配料纯净', value: 6.0, pct: 100 }
    ]
  }
];

// ==================== 站点信息 ====================
const SITE = {
  brandName: '西域果铺',
  slogan: '一口西域，一份实在',
  wechat: 'xiyuguopu',
  year: 2025,
  stats: {
    regions: 4,
    orchards: 30,
    steps: 6,
    products: 22,
    stars: 6,
    priceAdvantage: 15,
    satisfaction: 100
  }
};
