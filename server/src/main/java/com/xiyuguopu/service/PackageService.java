package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.dto.PackageVO;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.PackageProduct;
import com.xiyuguopu.mapper.PackageDefMapper;
import com.xiyuguopu.mapper.PackageProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageDefMapper packageDefMapper;
    private final PackageProductMapper packageProductMapper;

    public List<PackageVO> listAll() {
        List<PackageDef> packages = packageDefMapper.selectList(
                new LambdaQueryWrapper<PackageDef>().orderByAsc(PackageDef::getSortOrder));

        return packages.stream().map(pkg -> {
            List<PackageProduct> products = packageProductMapper.selectList(
                    new LambdaQueryWrapper<PackageProduct>()
                            .eq(PackageProduct::getPackageId, pkg.getId())
                            .orderByAsc(PackageProduct::getSortOrder));

            List<String> items = products.stream()
                    .map(p -> p.getProductName() + " " + p.getQuantity())
                    .collect(Collectors.toList());

            return PackageVO.builder()
                    .code(pkg.getCode())
                    .icon(pkg.getIcon())
                    .name(pkg.getName())
                    .subtitle(pkg.getSubtitle())
                    .price(pkg.getPrice())
                    .featured(pkg.getFeatured())
                    .badge(pkg.getBadge())
                    .extra(pkg.getExtra())
                    .items(items)
                    .build();
        }).collect(Collectors.toList());
    }
}
