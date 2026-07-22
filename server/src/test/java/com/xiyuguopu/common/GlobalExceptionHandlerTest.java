package com.xiyuguopu.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsBusinessExceptionCode() {
        Result<?> result = handler.handleBusinessException(BusinessException.conflict("库存不足"));

        assertThat(result.getCode()).isEqualTo(409);
        assertThat(result.getMessage()).isEqualTo("库存不足");
    }

    @Test
    void mapsGenericExceptionToServerError() {
        Result<?> result = handler.handleException(new RuntimeException("boom"));

        assertThat(result.getCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("boom");
    }
}
