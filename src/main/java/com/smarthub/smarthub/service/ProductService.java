package com.smarthub.smarthub.service;

import com.smarthub.smarthub.config.exception.AppException;
import com.smarthub.smarthub.domain.Product;
import com.smarthub.smarthub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy sản phẩm"));
    }

    @Transactional
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product req) {
        Product product = getById(id);

        product.setName(req.getName());
        product.setCategoryId(req.getCategoryId());
        product.setDescription(req.getDescription());
        product.setScreenSize(req.getScreenSize());
        product.setRam(req.getRam());
        product.setBattery(req.getBattery());
        product.setStorage(req.getStorage());
        product.setImageUrl(req.getImageUrl());
        product.setStock(req.getStock());
        product.setOriginalPrice(req.getOriginalPrice());
        product.setPrice(req.getPrice());
        product.setDiscount(req.getDiscount());

        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }

    public List<Product> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return productRepository.search(keyword);
    }
}
