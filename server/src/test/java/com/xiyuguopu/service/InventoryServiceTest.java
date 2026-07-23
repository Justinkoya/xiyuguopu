package com.xiyuguopu.service;

import com.xiyuguopu.common.BusinessException;
import com.xiyuguopu.entity.OrderItem;
import com.xiyuguopu.entity.PackageDef;
import com.xiyuguopu.entity.Product;
import com.xiyuguopu.mapper.PackageDefMapper;
import com.xiyuguopu.mapper.ProductMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryServiceTest {

    private final ProductMapper productMapper = mock(ProductMapper.class);
    private final PackageDefMapper packageDefMapper = mock(PackageDefMapper.class);
    private final InventoryService service = new InventoryService(productMapper, packageDefMapper);

    @Test
    void deductsProductStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("纸皮核桃");
        when(productMapper.update(isNull(), any())).thenReturn(1);

        service.deductProduct(product, 2);

        verify(productMapper).update(isNull(), any());
    }

    @Test
    void rejectsInsufficientProductStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("纸皮核桃");
        when(productMapper.update(isNull(), any())).thenReturn(0);

        assertThatThrownBy(() -> service.deductProduct(product, 2))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    void restoresPackageStock() {
        OrderItem item = new OrderItem();
        item.setItemType("PACKAGE");
        item.setPackageCode("gift");
        item.setQuantity(1);

        service.restore(item);

        verify(packageDefMapper).update(isNull(), any());
    }

    @Test
    void incrementsPackageSale() {
        OrderItem item = new OrderItem();
        item.setItemType("PACKAGE");
        item.setPackageCode("gift");
        item.setQuantity(1);

        service.incrementSale(item);

        verify(packageDefMapper).update(isNull(), any());
    }

    @Test
    void decrementsPackageSale() {
        OrderItem item = new OrderItem();
        item.setItemType("PACKAGE");
        item.setPackageCode("gift");
        item.setQuantity(1);

        service.decrementSale(item);

        verify(packageDefMapper).update(isNull(), any());
    }

    @Test
    void decrementsProductSale() {
        OrderItem item = new OrderItem();
        item.setItemType("PRODUCT");
        item.setProductId(1L);
        item.setQuantity(2);

        service.decrementSale(item);

        verify(productMapper).update(isNull(), any());
    }

    @Test
    void deductsPackageStock() {
        PackageDef pkg = new PackageDef();
        pkg.setCode("gift");
        pkg.setName("礼盒");
        when(packageDefMapper.update(isNull(), any())).thenReturn(1);

        service.deductPackage(pkg, 1);

        verify(packageDefMapper).update(isNull(), any());
    }
}
