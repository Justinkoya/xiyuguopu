package com.xiyuguopu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyuguopu.dto.AddressSaveDTO;
import com.xiyuguopu.entity.Address;
import com.xiyuguopu.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址管理
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressMapper addressMapper;

    /**
     * 我的地址列表（默认地址排最前）
     */
    public List<Address> list(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getUpdatedAt));
    }

    /**
     * 获取单个地址（含权限校验）
     */
    public Address get(Long userId, Long id) {
        return getOwnAddress(userId, id);
    }

    /**
     * 新增地址
     */
    public Address add(Long userId, AddressSaveDTO dto) {
        // 如果设为默认，先清掉旧的默认地址
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefault(userId);
        }

        Address addr = new Address();
        addr.setUserId(userId);
        copyFromDTO(addr, dto);
        addressMapper.insert(addr);
        return addr;
    }

    /**
     * 编辑地址
     */
    public Address edit(Long userId, Long id, AddressSaveDTO dto) {
        Address addr = getOwnAddress(userId, id);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefault(userId);
        }

        copyFromDTO(addr, dto);
        addr.setId(id);
        addressMapper.updateById(addr);
        return addr;
    }

    /**
     * 删除地址
     */
    public void delete(Long userId, Long id) {
        getOwnAddress(userId, id); // 校验所属
        addressMapper.deleteById(id);
    }

    // ---- 私有工具方法 ----

    private Address getOwnAddress(Long userId, Long id) {
        Address addr = addressMapper.selectById(id);
        if (addr == null) {
            throw new RuntimeException("地址不存在");
        }
        if (!addr.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该地址");
        }
        return addr;
    }

    private void clearDefault(Long userId) {
        List<Address> defaults = addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, true));
        for (Address a : defaults) {
            a.setIsDefault(false);
            addressMapper.updateById(a);
        }
    }

    private void copyFromDTO(Address addr, AddressSaveDTO dto) {
        addr.setName(dto.getName());
        addr.setPhone(dto.getPhone());
        addr.setProvince(dto.getProvince());
        addr.setCity(dto.getCity());
        addr.setDistrict(dto.getDistrict());
        addr.setDetail(dto.getDetail());
        addr.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
    }
}
