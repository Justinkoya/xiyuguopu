package com.xiyuguopu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 地址表单
 */
@Data
public class AddressSaveDTO {

    @NotBlank(message = "收货人不能为空")
    private String name;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "区县不能为空")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /** 是否默认地址 */
    private Boolean isDefault;
}
