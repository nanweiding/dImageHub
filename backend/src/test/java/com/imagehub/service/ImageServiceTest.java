package com.imagehub.service;

import com.imagehub.entity.Image;
import com.imagehub.repository.ImageRepository;
import com.imagehub.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    private ImageService imageService;

    private String uploadDir = "D:/dImageHub/uploads/";

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(imageRepository);
        ReflectionTestUtils.setField(imageService, "uploadDir", uploadDir);
    }

    @Test
    void getUserImages_shouldReturnUserImages() {
        Long userId = 1L;
        Image image1 = new Image();
        image1.setId(1L);
        image1.setUserId(userId);
        image1.setOriginalFilename("test1.jpg");

        Image image2 = new Image();
        image2.setId(2L);
        image2.setUserId(userId);
        image2.setOriginalFilename("test2.png");

        when(imageRepository.findByUserId(userId)).thenReturn(Arrays.asList(image1, image2));

        List<Image> result = imageService.getUserImages(userId);

        assertEquals(2, result.size());
        verify(imageRepository).findByUserId(userId);
    }

    @Test
    void getImageById_shouldReturnImage_whenExists() {
        Long imageId = 1L;
        Image image = new Image();
        image.setId(imageId);
        image.setOriginalFilename("test.jpg");

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        Image result = imageService.getImageById(imageId);

        assertNotNull(result);
        assertEquals(imageId, result.getId());
    }

    @Test
    void getImageById_shouldThrowException_whenNotExists() {
        Long imageId = 999L;
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> imageService.getImageById(imageId));
    }

    @Test
    void deleteImage_shouldDelete_whenUserOwnsImage() {
        Long imageId = 1L;
        Long userId = 1L;
        Image image = new Image();
        image.setId(imageId);
        image.setUserId(userId);
        image.setFilePath("D:/dImageHub/uploads/test.jpg");

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        assertDoesNotThrow(() -> imageService.deleteImage(imageId, userId));
        verify(imageRepository).delete(image);
    }

    @Test
    void deleteImage_shouldThrowException_whenUserDoesNotOwnImage() {
        Long imageId = 1L;
        Long imageOwnerId = 1L;
        Long requestUserId = 2L;
        Image image = new Image();
        image.setId(imageId);
        image.setUserId(imageOwnerId);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        assertThrows(RuntimeException.class, () ->
            imageService.deleteImage(imageId, requestUserId)
        );
        verify(imageRepository, never()).delete(any(Image.class));
    }
}