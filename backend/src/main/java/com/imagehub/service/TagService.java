package com.imagehub.service;

import com.imagehub.entity.Tag;
import com.imagehub.entity.Image;
import java.util.List;
import java.util.Map;

public interface TagService {
    Tag createTag(Long userId, String name, String color);
    List<Tag> getUserTags(Long userId);
    Tag updateTag(Long tagId, String name, String color, Long userId);
    void deleteTag(Long tagId, Long userId);

    Image addTagToImage(Long imageId, Long tagId, Long userId);
    Image removeTagFromImage(Long imageId, Long tagId, Long userId);
    List<Image> getImagesByTags(List<Long> tagIds, Long userId);

    Map<String, Object> getTagStats(Long userId);
}
