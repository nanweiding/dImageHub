package com.imagehub.service.impl;

import com.imagehub.entity.Image;
import com.imagehub.entity.Tag;
import com.imagehub.repository.ImageRepository;
import com.imagehub.repository.TagRepository;
import com.imagehub.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public Tag createTag(Long userId, String name, String color) {
        if (tagRepository.existsByUserIdAndName(userId, name)) {
            throw new RuntimeException("标签已存在");
        }
        Tag tag = new Tag();
        tag.setUserId(userId);
        tag.setName(name);
        tag.setColor(color != null ? color : "#0071E3");
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getUserTags(Long userId) {
        return tagRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Tag updateTag(Long tagId, String name, String color, Long userId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        if (!tag.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此标签");
        }
        if (name != null && !name.isBlank()) {
            tag.setName(name);
        }
        if (color != null) {
            tag.setColor(color);
        }
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId, Long userId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        if (!tag.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此标签");
        }
        // Remove tag from all images
        List<Image> imagesWithTag = imageRepository.findAll().stream()
                .filter(img -> img.getTags().contains(tag))
                .collect(Collectors.toList());
        for (Image image : imagesWithTag) {
            image.getTags().remove(tag);
            imageRepository.save(image);
        }
        tagRepository.delete(tag);
    }

    @Override
    @Transactional
    public Image addTagToImage(Long imageId, Long tagId, Long userId) {
        Image image = imageRepository.findByIdAndDeleted(imageId, false)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此图片");
        }
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        if (!tag.getUserId().equals(userId)) {
            throw new RuntimeException("无权限使用此标签");
        }
        image.getTags().add(tag);
        return imageRepository.save(image);
    }

    @Override
    @Transactional
    public Image removeTagFromImage(Long imageId, Long tagId, Long userId) {
        Image image = imageRepository.findByIdAndDeleted(imageId, false)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此图片");
        }
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));
        image.getTags().remove(tag);
        return imageRepository.save(image);
    }

    @Override
    public List<Image> getImagesByTags(List<Long> tagIds, Long userId) {
        if (tagIds == null || tagIds.isEmpty()) {
            return imageRepository.findByUserIdAndDeleted(userId, false);
        }
        List<Image> userImages = imageRepository.findByUserIdAndDeleted(userId, false);
        Set<Long> tagIdSet = new HashSet<>(tagIds);
        return userImages.stream()
                .filter(img -> {
                    Set<Tag> imgTags = img.getTags();
                    if (imgTags == null) return false;
                    // All specified tags must be present (AND filter)
                    return imgTags.stream()
                            .anyMatch(t -> tagIdSet.contains(t.getId()));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getTagStats(Long userId) {
        List<Tag> userTags = tagRepository.findByUserId(userId);
        List<Image> userImages = imageRepository.findByUserIdAndDeleted(userId, false);

        List<Map<String, Object>> tagStats = userTags.stream().map(tag -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("tagId", tag.getId());
            stat.put("name", tag.getName());
            stat.put("color", tag.getColor());
            long count = userImages.stream()
                    .filter(img -> img.getTags() != null && img.getTags().contains(tag))
                    .count();
            stat.put("imageCount", count);
            return stat;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("tags", tagStats);
        result.put("totalTags", userTags.size());
        return result;
    }
}