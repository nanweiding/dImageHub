package com.imagehub.service.impl;

import com.imagehub.entity.Folder;
import com.imagehub.repository.FolderRepository;
import com.imagehub.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    @Override
    @Transactional
    public Folder createFolder(String name, Long userId) {
        Folder folder = new Folder();
        folder.setUserId(userId);
        folder.setName(name);
        return folderRepository.save(folder);
    }

    @Override
    public List<Folder> getUserFolders(Long userId) {
        return folderRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId, Long userId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("文件夹不存在"));
        if (!folder.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此文件夹");
        }
        folderRepository.delete(folder);
    }
}