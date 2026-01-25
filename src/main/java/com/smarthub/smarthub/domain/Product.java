package com.smarthub.smarthub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String screenSize;
    private String screenType;
    private Integer ram;
    private Integer battery;
    private String chipset;
    private Integer storage;
    private String imageUrl;
    private Long stock;
}
