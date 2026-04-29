package com.imagehub.service;

import com.imagehub.entity.Folder;
import java.util.List;

public interface FolderService {
    Folder createFolder(String name, Long userId);
    List<Folder> getUserFolders(Long userId);
    void deleteFolder(Long folderId, Long userId);
}