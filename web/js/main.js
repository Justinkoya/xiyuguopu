/**
 * 西域果铺 — 页面渲染 & 交互逻辑
 * 数据全部从后端 API 获取
 */

// ==================== 通用工具 ====================

/**
 * 调用 API，自动解析 { code, data } 响应
 */
async function api(url) {
  const res = await fetch(API_BASE + url);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const json = await res.json();
  if (json.code !== 200) throw new Error(json.message || 'API error');
  return json.data;
}

function errorHTML(msg) {
  return `<p class="text-center text-gray-400 py-8">${msg || '加载失败，请刷新重试'}</p>`;
}

// ==================== 产品卡片渲染 ====================

// 尝鲜精选
(async function renderTrial() {
  const container = document.getElementById('products-trial');
  if (!container) return;

  try {
    const products = await api('/products?categoryCode=trial');

    const header = `
      <div class="flex items-center gap-4 mb-8">
        <div class="w-14 h-14 bg-blue-100 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0">新</div>
        <div><h3 class="text-2xl font-bold text-blue-700">尝鲜精选 · 初次见面，从这里认识我们</h3><p class="text-gray-500 text-sm mt-1">精选人人爱吃、价格一眼就觉得划算的品类，降低你的首次尝试成本</p></div>
      </div>`;

    const cards = products.map(p => {
      const tags = p.tags || [];
      const tagsHTML = tags.map(t => {
        let cls = 'bg-purple-50 text-purple-600';
        if (t.includes('品种') || t.includes('解腻')) cls = 'bg-blue-50 text-blue-600';
        else if (t.includes('无糖精') || t.includes('手捏')) cls = 'bg-green-50 text-green-600';
        return `<span class="text-xs px-2 py-0.5 rounded-full ${cls}">${t}</span>`;
      }).join('');

      return `
      <div class="bg-white rounded-3xl p-6 card-hover shadow-sm border border-gray-100 group">
        <div class="relative mb-4 overflow-hidden rounded-2xl h-48">
          <img src="${p.image}" alt="${p.name}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" loading="lazy">
          <div class="absolute top-3 left-3 bg-blue-500 text-white text-xs px-2.5 py-1 rounded-full font-bold">${p.badge || ''}</div>
        </div>
        <div class="flex items-center gap-2 mb-1">${tagsHTML}</div>
        <h4 class="font-bold text-lg">${p.name}</h4>
        <p class="text-sm text-gray-400 mb-2">${p.description || ''}</p>
        <div class="flex items-baseline gap-1"><span class="text-3xl font-black text-blue-600 price-badge">${p.price}</span><span class="text-sm text-gray-400">/${p.unit}</span></div>
      </div>`;
    }).join('');

    container.innerHTML = header + '<div class="grid md:grid-cols-3 gap-6">' + cards + '</div>';
  } catch (err) {
    console.error('加载尝鲜精选失败:', err);
    container.innerHTML = errorHTML();
  }
})();

// 西域珍品
(async function renderPremium() {
  const container = document.getElementById('products-premium');
  if (!container) return;

  try {
    const products = await api('/products?categoryCode=premium');

    const header = `
      <div class="flex items-center gap-4 mb-8">
        <div class="w-14 h-14 bg-amber-100 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0">珍</div>
        <div><h3 class="text-2xl font-bold text-amber-700">西域珍品 · 产地限定，难得一遇</h3><p class="text-gray-500 text-sm mt-1">新疆独有品种 + 进价低于全网，别人想模仿都找不到货源</p></div>
      </div>`;

    const cards = products.map(p => {
      const tags = p.tags || [];
      const tagsHTML = tags.map(t => `<span class="text-xs bg-amber-50 text-amber-700 px-2 py-0.5 rounded-full">${t}</span>`).join('');

      return `
      <div class="bg-white rounded-3xl p-6 card-hover shadow-sm border-t-4 border-amber-400 group">
        <div class="relative mb-4 overflow-hidden rounded-2xl h-48">
          <img src="${p.image}" alt="${p.name}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" loading="lazy">
          <div class="absolute top-3 left-3 bg-amber-500 text-white text-xs px-2.5 py-1 rounded-full font-bold">${p.badge || ''}</div>
        </div>
        <div class="flex items-center gap-2 mb-1">${tagsHTML}</div>
        <h4 class="font-bold text-lg">${p.name}</h4>
        <p class="text-sm text-gray-400 mb-2">${p.description || ''}</p>
        <div class="flex items-baseline gap-1"><span class="text-3xl font-black text-amber-600 price-badge">${p.price}</span><span class="text-sm text-gray-400">/${p.unit}</span></div>
      </div>`;
    }).join('');

    container.innerHTML = header + '<div class="grid md:grid-cols-3 gap-6">' + cards + '</div>';
  } catch (err) {
    console.error('加载西域珍品失败:', err);
    container.innerHTML = errorHTML();
  }
})();

// 口碑好物
(async function renderBestseller() {
  const container = document.getElementById('products-bestseller');
  if (!container) return;

  try {
    const products = await api('/products?categoryCode=bestseller');

    const header = `
      <div class="flex items-center gap-4 mb-8">
        <div class="w-14 h-14 bg-green-100 rounded-2xl flex items-center justify-center text-2xl flex-shrink-0">赞</div>
        <div><h3 class="text-2xl font-bold text-green-700">口碑好物 · 买过都说好，回购率最高</h3><p class="text-gray-500 text-sm mt-1">利润空间足 + 进价全网有优势 + 家家必囤的高频消费品</p></div>
      </div>`;

    const cards = products.map(p => {
      const borderClass = p.highlight ? 'border-2 border-green-300 relative overflow-hidden' : '';
      const badgeHTML = p.highlight ? `<div class="absolute -top-1 right-6 bg-green-500 text-white text-xs px-3 py-1 rounded-b-lg font-bold">${p.highlightText || ''}</div>` : '';
      const imgMT = p.highlight ? ' mt-2' : '';
      const extraHTML = p.extra ? `<p class="text-xs text-green-600 text-center mt-2 font-medium bg-green-50 rounded-full py-1">${p.extra}</p>` : '';

      return `
      <div class="bg-white rounded-3xl p-6 card-hover shadow-sm group ${borderClass}">
        ${badgeHTML}
        <div class="relative mb-4 overflow-hidden rounded-2xl h-44${imgMT}"><img src="${p.image}" alt="${p.name}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" loading="lazy"></div>
        <h4 class="font-bold text-lg text-center">${p.name}</h4>
        <p class="text-sm text-gray-400 text-center mb-1">${p.description || ''}</p>
        <div class="text-center"><span class="text-3xl font-black text-green-600 price-badge">${p.price}</span><span class="text-sm text-gray-400">/${p.unit}</span></div>
        ${extraHTML}
      </div>`;
    }).join('');

    container.innerHTML = header + '<div class="grid md:grid-cols-4 gap-6">' + cards + '</div>';
  } catch (err) {
    console.error('加载口碑好物失败:', err);
    container.innerHTML = errorHTML();
  }
})();

// ==================== 成本透明渲染 ====================
(async function renderCostBreakdown() {
  const container = document.getElementById('cost-breakdown');
  if (!container) return;

  try {
    const list = await api('/cost-breakdown');

    container.innerHTML = list.map(c => `
      <div class="bg-white rounded-3xl p-8 shadow-sm card-hover">
        <div class="flex items-center gap-3 mb-6">
          <span class="text-3xl">${c.icon}</span>
          <h3 class="text-xl font-bold">${c.productName} · ${c.weight} 售价 ${c.price}</h3>
        </div>
        <div class="space-y-4">
          ${(c.items || []).map(cost => `
            <div class="flex justify-between py-3 border-b border-gray-50">
              <span>${cost.label}</span>
              <span class="font-bold">${cost.amount} <span class="text-xs text-gray-400">${cost.pct}</span></span>
            </div>`).join('')}
          <div class="flex justify-between py-3">
            <span class="text-b-red font-bold">我们的利润</span>
            <span class="font-bold text-xl text-b-red">${c.profit} <span class="text-xs text-gray-400">${c.profitPct}</span></span>
          </div>
        </div>
      </div>`).join('');
  } catch (err) {
    console.error('加载成本透明失败:', err);
    container.innerHTML = errorHTML();
  }
})();

// ==================== 6星评分卡渲染 ====================
(async function renderScorecards() {
  const container = document.getElementById('scorecards');
  if (!container) return;

  try {
    const cards = await api('/scorecards');

    container.innerHTML = cards.map(card => `
      <div class="bg-b-cream rounded-3xl p-6 card-hover">
        <div class="flex items-center gap-3 mb-4">
          <img src="${card.image}" alt="${card.productName}" class="w-16 h-16 rounded-xl object-cover" loading="lazy">
          <div><h4 class="font-bold">${card.productName}</h4><p class="text-xs text-gray-400">${card.originText || ''}</p></div>
          <div class="ml-auto text-xl font-black text-b-red serif">${card.totalScore}<span class="text-sm font-normal text-gray-400">/6</span></div>
        </div>
        <div class="space-y-2 text-xs">
          ${(card.dimensions || []).map(s => `
            <div class="flex justify-between"><span>${s.label}</span><span class="font-bold ${s.score === 6.0 ? 'text-b-green' : ''}">${s.score.toFixed(1)}</span></div>
            <div class="w-full h-1.5 bg-gray-200 rounded-full"><div class="h-full bg-b-gold rounded-full" style="width:${s.pct}%"></div></div>`).join('')}
        </div>
        <p class="text-xs text-gray-400 mt-3 text-center">${card.ingredientText || ''}</p>
      </div>`).join('');
  } catch (err) {
    console.error('加载评分卡失败:', err);
    container.innerHTML = errorHTML();
  }
})();

// ==================== 套餐渲染 ====================
(async function renderPackages() {
  const container = document.getElementById('packages');
  if (!container) return;

  try {
    const packages = await api('/packages');

    container.innerHTML = packages.map(pkg => {
      const featuredClass = pkg.featured ? ' border-2 border-b-gold relative transform md:scale-105 z-10' : '';
      const badgeHTML = pkg.featured ? `<div class="absolute -top-4 left-1/2 -translate-x-1/2 bg-b-red text-white text-sm font-bold px-6 py-1.5 rounded-full pulse-dot">${pkg.badge || ''}</div>` : '';
      const imgMT = pkg.featured ? ' mt-2' : '';
      const btnClass = pkg.code === 'gift' ? 'bg-b-red text-white hover:bg-red-800 shadow-xl shadow-b-red/20' : 'bg-b-dark text-white hover:bg-gray-800';
      const extraHTML = pkg.extra ? `<p class="text-green-600 font-medium pt-1">${pkg.extra}</p>` : '';
      const imageHTML = pkg.image
        ? `<div class="relative mb-4 overflow-hidden rounded-2xl h-44${imgMT}"><img src="${pkg.image}" alt="${pkg.name}" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" loading="lazy"></div>`
        : `<div class="text-5xl mb-4${imgMT}">${pkg.icon}</div>`;

      return `
      <div class="bg-b-cream rounded-3xl p-8 card-hover text-center group${featuredClass}">
        ${badgeHTML}
        ${imageHTML}
        <h3 class="font-bold text-2xl mb-2">${pkg.name}</h3>
        <p class="text-gray-400 text-sm mb-2">${pkg.subtitle || ''}</p>
        <div class="text-4xl font-black ${pkg.code === 'gift' ? 'text-b-red' : 'text-b-dark'} mb-6">${pkg.price}</div>
        <div class="text-sm text-gray-500 space-y-2 mb-6 bg-white/50 rounded-2xl py-4">
          ${(pkg.items || []).map(i => `<p>${typeof i === 'string' ? i : (i.productName + ' ' + i.quantity)}</p>`).join('')}
          ${extraHTML}
        </div>
        <a href="#contact" class="block w-full ${btnClass} py-3.5 rounded-full font-bold transition-all">立即购买</a>
      </div>`;
    }).join('');
  } catch (err) {
    console.error('加载套餐失败:', err);
    container.innerHTML = errorHTML();
  }
})();

// ==================== 交互逻辑 ====================

// FAQ Toggle
document.querySelectorAll('.faq-open').forEach(el => {
  el.addEventListener('click', function() {
    const arrow = this.querySelector('.transition-transform');
    if (this.classList.contains('faq-open')) {
      arrow.style.transform = 'rotate(180deg)';
    } else {
      arrow.style.transform = 'rotate(0deg)';
    }
  });
});

// Navbar Shadow on Scroll
window.addEventListener('scroll', () => {
  document.getElementById('navbar').classList.toggle('shadow-md', window.scrollY > 40);
});

// Fade-up Animation Observer
const observer = new IntersectionObserver((entries) => {
  entries.forEach(e => {
    if (e.isIntersecting) e.target.classList.add('fade-up');
  });
}, { threshold: 0.1 });

document.querySelectorAll('section > div > div').forEach(el => observer.observe(el));
