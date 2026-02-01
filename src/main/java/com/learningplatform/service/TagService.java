package com.learningplatform.service;

import com.learningplatform.dto.TagDTO;
import com.learningplatform.entity.Tag;
import com.learningplatform.exception.DuplicateResourceException;
import com.learningplatform.exception.ResourceNotFoundException;
import com.learningplatform.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public TagDTO createTag(TagDTO tagDTO) {
        log.info("Creating tag: {}", tagDTO.getName());

        if (tagRepository.existsByName(tagDTO.getName())) {
            throw new DuplicateResourceException("Tag", "name", tagDTO.getName());
        }

        Tag tag = Tag.builder()
                .name(tagDTO.getName())
                .build();

        Tag savedTag = tagRepository.save(tag);
        log.info("Tag created with ID: {}", savedTag.getId());

        return mapToDTO(savedTag);
    }

    @Transactional(readOnly = true)
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        return mapToDTO(tag);
    }

    @Transactional(readOnly = true)
    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        log.info("Updating tag with ID: {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));

        if (!tag.getName().equals(tagDTO.getName()) &&
                tagRepository.existsByName(tagDTO.getName())) {
            throw new DuplicateResourceException("Tag", "name", tagDTO.getName());
        }

        tag.setName(tagDTO.getName());

        Tag updatedTag = tagRepository.save(tag);
        return mapToDTO(updatedTag);
    }

    public void deleteTag(Long id) {
        log.info("Deleting tag with ID: {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));

        tagRepository.delete(tag);
        log.info("Tag deleted with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Tag getOrCreateTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag newTag = Tag.builder().name(name).build();
                    return tagRepository.save(newTag);
                });
    }

    private TagDTO mapToDTO(Tag tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
