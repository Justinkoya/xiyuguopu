package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiyuguopu.dto.AdminPackageSaveDTO;
import com.xiyuguopu.dto.AdminPackageVO;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.PackageProduct;
import com.xiyuguopu.mapper.PackageDefMapper;
import com.xiyuguopu.mapper.PackageProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理后台 — 套餐管理
 */
@Service
@RequiredArgsConstructor
public class AdminPackageService {

    private final PackageDefMapper packageDefMapper;
    private final PackageProductMapper packageProductMapper;

    public Page<PackageDef> list(int page, int size, String keyword) {
        LambdaQueryWrapper<PackageDef> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(PackageDef::getName, keyword).or().like(PackageDef::getCode, keyword));
        }
        qw.orderByAsc(PackageDef::getSortOrder);
        return packageDefMapper.selectPage(new Page<>(page, size), qw);
    }

    public AdminPackageVO detail(Long id) {
        PackageDef pkg = packageDefMapper.selectById(id);
        if (pkg == null) {
            throw new RuntimeException("套餐不存在");
        }
        return toVO(pkg);
    }

    public AdminPackageVO add(AdminPackageSaveDTO dto) {
        ensureCodeUnique(dto.getCode(), null);
        PackageDef pkg = new PackageDef();
        BeanUtils.copyProperties(dto, pkg);
        packageDefMapper.insert(pkg);
        saveItems(pkg.getId(), dto.getItems());
        return detail(pkg.getId());
    }

    public AdminPackageVO edit(Long id, AdminPackageSaveDTO dto) {
        PackageDef pkg = packageDefMapper.selectById(id);
        if (pkg == null) {
            throw new RuntimeException("套餐不存在");
        }
        ensureCodeUnique(dto.getCode(), id);
        BeanUtils.copyProperties(dto, pkg);
        pkg.setId(id);
        packageDefMapper.updateById(pkg);
        packageProductMapper.delete(new LambdaQueryWrapper<PackageProduct>().eq(PackageProduct::getPackageId, id));
        saveItems(id, dto.getItems());
        return detail(id);
    }

    public void delete(Long id) {
        if (packageDefMapper.selectById(id) == null) {
            throw new RuntimeException("套餐不存在");
        }
        packageProductMapper.delete(new LambdaQueryWrapper<PackageProduct>().eq(PackageProduct::getPackageId, id));
        packageDefMapper.deleteById(id);
    }

    private void saveItems(Long packageId, List<AdminPackageSaveDTO.ItemDTO> items) {
        List<AdminPackageSaveDTO.ItemDTO> safeItems = items == null ? Collections.emptyList() : items;
        for (int i = 0; i < safeItems.size(); i++) {
            AdminPackageSaveDTO.ItemDTO dto = safeItems.get(i);
            if (dto.getProductName() == null || dto.getProductName().isBlank()) {
                continue;
            }
            PackageProduct item = new PackageProduct();
            item.setPackageId(packageId);
            item.setProductName(dto.getProductName().trim());
            item.setQuantity(dto.getQuantity() == null || dto.getQuantity().isBlank() ? "500g" : dto.getQuantity().trim());
            item.setSortOrder(i + 1);
            packageProductMapper.insert(item);
        }
    }

    private void ensureCodeUnique(String code, Long currentId) {
        PackageDef exists = packageDefMapper.selectOne(
                new LambdaQueryWrapper<PackageDef>().eq(PackageDef::getCode, code));
        if (exists != null && (currentId == null || !exists.getId().equals(currentId))) {
            throw new RuntimeException("套餐编码已存在");
        }
    }

    private AdminPackageVO toVO(PackageDef pkg) {
        List<AdminPackageVO.ItemVO> items = packageProductMapper.selectList(
                        new LambdaQueryWrapper<PackageProduct>()
                                .eq(PackageProduct::getPackageId, pkg.getId())
                                .orderByAsc(PackageProduct::getSortOrder))
                .stream()
                .map(item -> AdminPackageVO.ItemVO.builder()
                        .id(item.getId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .sortOrder(item.getSortOrder())
                        .build())
                .collect(Collectors.toList());

        return AdminPackageVO.builder()
                .id(pkg.getId())
                .code(pkg.getCode())
                .icon(pkg.getIcon())
                .name(pkg.getName())
                .subtitle(pkg.getSubtitle())
                .price(pkg.getPrice())
                .stock(pkg.getStock())
                .sale(pkg.getSale())
                .featured(pkg.getFeatured())
                .badge(pkg.getBadge())
                .extra(pkg.getExtra())
                .sortOrder(pkg.getSortOrder())
                .items(items)
                .build();
    }
}
