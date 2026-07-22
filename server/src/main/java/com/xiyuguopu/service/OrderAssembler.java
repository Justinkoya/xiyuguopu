package com.xiyuguopu.service;

import com.xiyuguopu.dto.AdminOrderVO;
import com.xiyuguopu.dto.UserOrderVO;
import com.xiyuguopu.entity.OrderHead;
import com.xiyuguopu.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderAssembler {

    public UserOrderVO toUserVO(OrderHead head, List<OrderItem> items) {
        return UserOrderVO.builder()
                .id(head.getId())
                .orderNo(head.getOrderNo())
                .userId(head.getUserId())
                .status(head.getStatus())
                .totalAmount(head.getTotalAmount())
                .remark(head.getRemark())
                .addressSnapshot(head.getAddressSnapshot())
                .wxTransactionId(head.getWxTransactionId())
                .items(toUserItemVOs(items))
                .paidAt(head.getPaidAt())
                .shippedAt(head.getShippedAt())
                .completedAt(head.getCompletedAt())
                .cancelledAt(head.getCancelledAt())
                .createdAt(head.getCreatedAt())
                .trackingNumber(head.getTrackingNumber())
                .shippingCompany(head.getShippingCompany())
                .build();
    }

    public AdminOrderVO toAdminVO(OrderHead head, List<OrderItem> items) {
        return AdminOrderVO.builder()
                .id(head.getId())
                .orderNo(head.getOrderNo())
                .userId(head.getUserId())
                .status(head.getStatus())
                .totalAmount(head.getTotalAmount())
                .remark(head.getRemark())
                .addressSnapshot(head.getAddressSnapshot())
                .items(toAdminItemVOs(items))
                .paidAt(head.getPaidAt())
                .shippedAt(head.getShippedAt())
                .completedAt(head.getCompletedAt())
                .cancelledAt(head.getCancelledAt())
                .createdAt(head.getCreatedAt())
                .trackingNumber(head.getTrackingNumber())
                .shippingCompany(head.getShippingCompany())
                .build();
    }

    private List<UserOrderVO.OrderItemVO> toUserItemVOs(List<OrderItem> items) {
        return items.stream()
                .map(i -> UserOrderVO.OrderItemVO.builder()
                        .itemType(i.getItemType())
                        .productId(i.getProductId())
                        .packageCode(i.getPackageCode())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());
    }

    private List<AdminOrderVO.OrderItemVO> toAdminItemVOs(List<OrderItem> items) {
        return items.stream()
                .map(i -> AdminOrderVO.OrderItemVO.builder()
                        .itemType(i.getItemType())
                        .productId(i.getProductId())
                        .packageCode(i.getPackageCode())
                        .productName(i.getProductName())
                        .price(i.getPrice())
                        .quantity(i.getQuantity())
                        .subtotal(i.getSubtotal())
                        .build())
                .collect(Collectors.toList());
    }
}
