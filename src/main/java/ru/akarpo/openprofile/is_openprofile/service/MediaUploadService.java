package ru.akarpo.openprofile.is_openprofile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.akarpo.openprofile.is_openprofile.domain.MediaAsset;
import ru.akarpo.openprofile.is_openprofile.domain.User;
import ru.akarpo.openprofile.is_openprofile.dto.MediaAssetDTO;
import ru.akarpo.openprofile.is_openprofile.exception.BadRequestException;
import ru.akarpo.openprofile.is_openprofile.exception.ResourceNotFoundException;
import ru.akarpo.openprofile.is_openprofile.mapper.MediaAssetMapper;
import ru.akarpo.openprofile.is_openprofile.repository.MediaAssetRepository;
import ru.akarpo.openprofile.is_openprofile.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaUploadService {

    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;
    private final MediaAssetMapper mediaAssetMapper;

    @Value("${media.upload.directory:./uploads}")
    private String uploadDirectory;

    @Value("${media.max-file-size:10485760}")
    private long maxFileSize;

    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};

    @Transactional
    public MediaAssetDTO uploadImage(UUID userId, MultipartFile file, String altText) {
        validateFile(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new BadRequestException("Invalid image file");
            }

            String filename = generateFilename(file.getOriginalFilename());
            String storageKey = String.format("users/%s/%s", userId, filename);
            Path filePath = Paths.get(uploadDirectory, storageKey);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            MediaAsset mediaAsset = MediaAsset.builder()
                    .user(user)
                    .filename(file.getOriginalFilename())
                    .storageKey(storageKey)
                    .contentType(file.getContentType())
                    .sizeBytes(file.getSize())
                    .width(originalImage.getWidth())
                    .height(originalImage.getHeight())
                    .altText(altText)
                    .build();

            mediaAsset = mediaAssetRepository.save(mediaAsset);
            return mediaAssetMapper.toDto(mediaAsset);

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    @Transactional
    public MediaAssetDTO cropImage(UUID mediaId, int x, int y, int width, int height) {
        MediaAsset mediaAsset = mediaAssetRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", mediaId));

        try {
            Path originalPath = Paths.get(uploadDirectory, mediaAsset.getStorageKey());
            BufferedImage originalImage = ImageIO.read(originalPath.toFile());

            BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

            String croppedFilename = "cropped_" + generateFilename(mediaAsset.getFilename());
            String croppedStorageKey = String.format("users/%s/%s", mediaAsset.getUser().getId(), croppedFilename);
            Path croppedPath = Paths.get(uploadDirectory, croppedStorageKey);

            String format = mediaAsset.getContentType().split("/")[1];
            ImageIO.write(croppedImage, format, croppedPath.toFile());

            MediaAsset croppedMediaAsset = MediaAsset.builder()
                    .user(mediaAsset.getUser())
                    .filename(croppedFilename)
                    .storageKey(croppedStorageKey)
                    .contentType(mediaAsset.getContentType())
                    .sizeBytes(Files.size(croppedPath))
                    .width(width)
                    .height(height)
                    .altText(mediaAsset.getAltText())
                    .build();

            croppedMediaAsset = mediaAssetRepository.save(croppedMediaAsset);
            return mediaAssetMapper.toDto(croppedMediaAsset);

        } catch (IOException e) {
            log.error("Failed to crop image", e);
            throw new BadRequestException("Failed to crop image: " + e.getMessage());
        }
    }

    @Transactional
    public MediaAssetDTO compressImage(UUID mediaId, float quality) {
        if (quality < 0.1f || quality > 1.0f) {
            throw new BadRequestException("Quality must be between 0.1 and 1.0");
        }

        MediaAsset mediaAsset = mediaAssetRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", mediaId));

        try {
            Path originalPath = Paths.get(uploadDirectory, mediaAsset.getStorageKey());
            BufferedImage originalImage = ImageIO.read(originalPath.toFile());

            String compressedFilename = "compressed_" + generateFilename(mediaAsset.getFilename());
            String compressedStorageKey = String.format("users/%s/%s", mediaAsset.getUser().getId(), compressedFilename);
            Path compressedPath = Paths.get(uploadDirectory, compressedStorageKey);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String format = mediaAsset.getContentType().split("/")[1];
            ImageIO.write(originalImage, format, baos);

            Files.write(compressedPath, baos.toByteArray());

            MediaAsset compressedMediaAsset = MediaAsset.builder()
                    .user(mediaAsset.getUser())
                    .filename(compressedFilename)
                    .storageKey(compressedStorageKey)
                    .contentType(mediaAsset.getContentType())
                    .sizeBytes(Files.size(compressedPath))
                    .width(mediaAsset.getWidth())
                    .height(mediaAsset.getHeight())
                    .altText(mediaAsset.getAltText())
                    .build();

            compressedMediaAsset = mediaAssetRepository.save(compressedMediaAsset);
            return mediaAssetMapper.toDto(compressedMediaAsset);

        } catch (IOException e) {
            log.error("Failed to compress image", e);
            throw new BadRequestException("Failed to compress image: " + e.getMessage());
        }
    }

    public byte[] getMediaFile(UUID mediaId) {
        MediaAsset mediaAsset = mediaAssetRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("MediaAsset", "id", mediaId));

        try {
            Path filePath = Paths.get(uploadDirectory, mediaAsset.getStorageKey());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Failed to read media file", e);
            throw new BadRequestException("Failed to read media file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File size exceeds maximum limit of " + maxFileSize + " bytes");
        }

        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidType = true;
                break;
            }
        }

        if (!isValidType) {
            throw new BadRequestException("Invalid file type. Allowed types: JPEG, PNG, GIF, WEBP");
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }
}