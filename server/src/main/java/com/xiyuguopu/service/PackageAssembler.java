package com.xiyuguopu.service;

import com.xiyuguopu.dto.PackageVO;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.PackageProduct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PackageAssembler {

    public PackageVO toVO(PackageDef pkg, List<PackageProduct> products) {
        List<String> items = products.stream()
                .map(p -> p.getProductName() + " " + p.getQuantity())
                .collect(Collectors.toList());

        return PackageVO.builder()
                .code(pkg.getCode())
                .icon(pkg.getIcon())
                .image(pkg.getImage())
                .name(pkg.getName())
                .subtitle(pkg.getSubtitle())
                .price(pkg.getPrice())
                .stock(pkg.getStock())
                .sale(pkg.getSale())
                .featured(pkg.getFeatured())
                .badge(pkg.getBadge())
                .extra(pkg.getExtra())
                .items(items)
                .build();
    }
}
