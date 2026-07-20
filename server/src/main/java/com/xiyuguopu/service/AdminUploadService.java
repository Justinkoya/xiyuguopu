package com.xiyuguopu.service;

import com.xiyuguopu.config.UploadProperties;
import com.xiyuguopu.dto.AdminImageUploadVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUploadService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final UploadProperties uploadProperties;

    public AdminImageUploadVO uploadImage(MultipartFile file) {
        validate(file);

        String day = LocalDate.now().format(DAY_FORMAT);
        String filename = UUID.randomUUID() + ".webp";
        Path targetDir = Path.of(uploadProperties.getImageDir()).resolve(day);
        Path target = targetDir.resolve(filename);

        try {
            Files.createDirectories(targetDir);
            BufferedImage source = readImage(file);
            BufferedImage output = resize(source, uploadProperties.getMaxImageSide());
            writeWebp(output, target);
            return new AdminImageUploadVO("images/uploads/" + day + "/" + filename);
        } catch (IOException e) {
            throw new IllegalArgumentException("图片保存失败，请稍后重试", e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择要上传的图片");
        }
        if (file.getSize() > uploadProperties.getMaxImageSize()) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }

        String name = file.getOriginalFilename();
        String ext = "";
        int dot = name == null ? -1 : name.lastIndexOf('.');
        if (dot >= 0 && dot < name.length() - 1) {
            ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("只支持 jpg、jpeg、png、webp 图片");
        }
    }

    private BufferedImage readImage(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new IllegalArgumentException("图片文件无法识别");
            }
            return image;
        }
    }

    private BufferedImage resize(BufferedImage source, int maxSide) {
        int width = source.getWidth();
        int height = source.getHeight();
        double scale = Math.min(1.0, (double) maxSide / Math.max(width, height));
        int targetWidth = Math.max(1, (int) Math.round(width * scale));
        int targetHeight = Math.max(1, (int) Math.round(height * scale));

        BufferedImage target = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = target.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, targetWidth, targetHeight);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    private void writeWebp(BufferedImage image, Path target) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new IOException("WebP writer not available");
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream out = ImageIO.createImageOutputStream(Files.newOutputStream(target))) {
            writer.setOutput(out);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                String[] types = param.getCompressionTypes();
                if (types != null && types.length > 0) {
                    param.setCompressionType(types[0]);
                }
                param.setCompressionQuality(uploadProperties.getImageQuality());
            }
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }
}
