package com.xiyuguopu.controller;

import com.xiyuguopu.common.Result;
import com.xiyuguopu.dto.AdminImageUploadVO;
import com.xiyuguopu.service.AdminUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/uploads")
@RequiredArgsConstructor
public class AdminUploadController {

    private final AdminUploadService adminUploadService;

    @PostMapping("/images")
    public Result<AdminImageUploadVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.ok(adminUploadService.uploadImage(file));
    }
}
