package com.xiyuguopu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String imageDir = "/data/xiyuguopu/uploads/images";
    private long maxImageSize = 5 * 1024 * 1024L;
    private int maxImageSide = 1200;
    private float imageQuality = 0.82f;
}
