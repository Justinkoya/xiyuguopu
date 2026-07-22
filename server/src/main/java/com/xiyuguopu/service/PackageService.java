package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.common.BusinessException;
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
    private final PackageAssembler packageAssembler;

    public List<PackageVO> listAll() {
        List<PackageDef> packages = packageDefMapper.selectList(
                new LambdaQueryWrapper<PackageDef>().orderByAsc(PackageDef::getSortOrder));
        return packages.stream().map(this::toVO).collect(Collectors.toList());
    }

    public List<PackageVO> listFeatured() {
        List<PackageDef> packages = packageDefMapper.selectList(
                new LambdaQueryWrapper<PackageDef>()
                        .eq(PackageDef::getFeatured, true)
                        .orderByDesc(PackageDef::getCreatedAt));
        return packages.stream().map(this::toVO).collect(Collectors.toList());
    }

    public PackageVO detail(String code) {
        PackageDef pkg = packageDefMapper.selectOne(
                new LambdaQueryWrapper<PackageDef>().eq(PackageDef::getCode, code));
        if (pkg == null) {
            throw BusinessException.notFound("套餐不存在");
        }
        return toVO(pkg);
    }

    private PackageVO toVO(PackageDef pkg) {
        List<PackageProduct> products = packageProductMapper.selectList(
                new LambdaQueryWrapper<PackageProduct>()
                        .eq(PackageProduct::getPackageId, pkg.getId())
                        .orderByAsc(PackageProduct::getSortOrder));
        return packageAssembler.toVO(pkg, products);
    }
}
