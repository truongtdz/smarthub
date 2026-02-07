package com.smarthub.smarthub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    private String screenSize;
    private Integer ram;
    private Integer battery;
    private Integer storage;
    private String imageUrl;
    private Long stock;
    private Long originalPrice;
    private Long price;
    private Long discount;
}
