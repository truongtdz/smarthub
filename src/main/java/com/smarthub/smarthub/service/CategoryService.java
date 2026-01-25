package com.smarthub.smarthub.service;

import com.smarthub.smarthub.config.exception.AppException;
import com.smarthub.smarthub.domain.Category;
import com.smarthub.smarthub.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy danh mục"));
    }

    @Transactional
    public Category create(Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new AppException("Tên danh mục đã tồn tại");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category req) {
        Category category = getById(id);

        if (!category.getName().equals(req.getName()) &&
                categoryRepository.findByName(req.getName()).isPresent()) {
            throw new AppException("Tên danh mục đã tồn tại");
        }

        category.setName(req.getName());
        category.setDescription(req.getDescription());

        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }

    public List<Category> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return categoryRepository.search(keyword);
    }
}
