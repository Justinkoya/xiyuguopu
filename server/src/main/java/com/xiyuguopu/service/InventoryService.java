package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.PackageDefMapper;
import com.xiyuguopu.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductMapper productMapper;
    private final PackageDefMapper packageDefMapper;

    public void deductProduct(Product product, int quantity) {
        int updated = productMapper.update(null,
                new LambdaUpdateWrapper<Product>()
                        .setSql("stock = stock - " + quantity)
                        .eq(Product::getId, product.getId())
                        .ge(Product::getStock, quantity));
        if (updated == 0) {
            throw BusinessException.conflict("「" + product.getName() + "」库存不足");
        }
    }

    public void deductPackage(PackageDef pkg, int quantity) {
        int updated = packageDefMapper.update(null,
                new LambdaUpdateWrapper<PackageDef>()
                        .setSql("stock = stock - " + quantity)
                        .eq(PackageDef::getCode, pkg.getCode())
                        .ge(PackageDef::getStock, quantity));
        if (updated == 0) {
            throw BusinessException.conflict("「" + pkg.getName() + "」库存不足");
        }
    }

    public void restore(OrderItem item) {
        int qty = item.getQuantity();
        if ("PACKAGE".equals(item.getItemType())) {
            packageDefMapper.update(null,
                    new LambdaUpdateWrapper<PackageDef>()
                            .setSql("stock = stock + " + qty)
                            .eq(PackageDef::getCode, item.getPackageCode()));
        } else if (item.getProductId() != null) {
            productMapper.update(null,
                    new LambdaUpdateWrapper<Product>()
                            .setSql("stock = stock + " + qty)
                            .eq(Product::getId, item.getProductId()));
        }
    }

    public void incrementSale(OrderItem item) {
        int qty = item.getQuantity();
        if ("PACKAGE".equals(item.getItemType())) {
            packageDefMapper.update(null,
                    new LambdaUpdateWrapper<PackageDef>()
                            .setSql("sale = COALESCE(sale,0) + " + qty)
                            .eq(PackageDef::getCode, item.getPackageCode()));
        } else if (item.getProductId() != null) {
            productMapper.update(null,
                    new LambdaUpdateWrapper<Product>()
                            .setSql("sale = COALESCE(sale,0) + " + qty)
                            .eq(Product::getId, item.getProductId()));
        }
    }

    public void decrementSale(OrderItem item) {
        int qty = item.getQuantity();
        if ("PACKAGE".equals(item.getItemType())) {
            packageDefMapper.update(null,
                    new LambdaUpdateWrapper<PackageDef>()
                            .setSql("sale = GREATEST(COALESCE(sale,0) - " + qty + ", 0)")
                            .eq(PackageDef::getCode, item.getPackageCode()));
        } else if (item.getProductId() != null) {
            productMapper.update(null,
                    new LambdaUpdateWrapper<Product>()
                            .setSql("sale = GREATEST(COALESCE(sale,0) - " + qty + ", 0)")
                            .eq(Product::getId, item.getProductId()));
        }
    }
}
