package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.dto.ProductVO;
import com.xiyuguopu.entity.Category;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.CategoryMapper;
import com.xiyuguopu.mapper.OrderHeadMapper;
import com.xiyuguopu.mapper.OrderItemMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品服务
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final OrderHeadMapper orderHeadMapper;
    private final OrderItemMapper orderItemMapper;

    /**
     * 按 ID 查询单个商品
     */
    public ProductVO getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return null;
        }
        Map<Long, Integer> salesMap = loadSalesMap(List.of(id));
        return toVO(product, salesMap.getOrDefault(id, 0));
    }

    /**
     * 按分类编码查询商品列表；categoryCode 为 null 时返回全部上架商品
     */
    public List<ProductVO> getByCategoryCode(String categoryCode) {
        List<Product> products;
        if (categoryCode == null || categoryCode.isEmpty()) {
            products = productMapper.selectList(
                    new LambdaQueryWrapper<Product>()
                            .eq(Product::getIsOnSale, true)
                            .orderByAsc(Product::getSortOrder));
            return withSales(products);
        }
        // 1. 查分类
        Category category = categoryMapper.selectOne(
                new LambdaQueryWrapper<Category>().eq(Category::getCode, categoryCode));
        if (category == null) {
            return Collections.emptyList();
        }
        // 2. 查该分类下的商品，按排序字段升序
        products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getCategoryId, category.getId())
                        .eq(Product::getIsOnSale, true)
                        .orderByAsc(Product::getSortOrder));
        return withSales(products);
    }

    private List<ProductVO> withSales(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> productIds = products.stream().map(Product::getId).collect(Collectors.toList());
        Map<Long, Integer> salesMap = loadSalesMap(productIds);
        return products.stream()
                .map(product -> toVO(product, salesMap.getOrDefault(product.getId(), 0)))
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> loadSalesMap(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<OrderHead> paidOrders = orderHeadMapper.selectList(
                new LambdaQueryWrapper<OrderHead>()
                        .in(OrderHead::getStatus, List.of("PAID", "SHIPPED", "COMPLETED")));
        if (paidOrders.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> paidOrderIds = paidOrders.stream().map(OrderHead::getId).collect(Collectors.toSet());
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getProductId, productIds)
                        .in(OrderItem::getOrderId, paidOrderIds));
        return items.stream().collect(Collectors.groupingBy(
                OrderItem::getProductId,
                Collectors.summingInt(item -> item.getQuantity() == null ? 0 : item.getQuantity())));
    }

    private ProductVO toVO(Product product, Integer sales) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setPrice(product.getPrice());
        vo.setUnit(product.getUnit());
        vo.setImage(product.getImage());
        vo.setBadge(product.getBadge());
        vo.setTags(product.getTags());
        vo.setDescription(product.getDescription());
        vo.setHighlight(product.getHighlight());
        vo.setHighlightText(product.getHighlightText());
        vo.setExtra(product.getExtra());
        vo.setStock(product.getStock());
        vo.setIsOnSale(product.getIsOnSale());
        vo.setSortOrder(product.getSortOrder());
        vo.setCostDetail(product.getCostDetail());
        vo.setSales(sales);
        vo.setCreatedAt(product.getCreatedAt());
        vo.setUpdatedAt(product.getUpdatedAt());
        return vo;
    }
}
