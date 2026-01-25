package com.smarthub.smarthub.repository;

import com.smarthub.smarthub.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR CAST(p.categoryId AS string) LIKE CONCAT('%', :keyword, '%')
    """)
    List<Product> search(@Param("keyword") String keyword);
}
