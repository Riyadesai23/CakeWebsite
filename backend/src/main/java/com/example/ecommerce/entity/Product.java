package com.example.ecommerce.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku"),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_brand", columnList = "brand"),
    @Index(name = "idx_product_active", columnList = "active")
})
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "sku", length = 100, nullable = false, unique = true)
    private String sku;

    @Column(name = "description", length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "final_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel = 5;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "unit", length = 50)
    private String unit = "Piece";

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy = "admin";

    @Column(name = "updated_by", length = 50)
    private String updatedBy = "admin";

    public Product() {
    }

    public Product(String name, String sku, String description, Category category,
                   BigDecimal price, BigDecimal discountPercentage, String imageUrl,
                   String brand, String unit, Integer minimumStockLevel) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        this.calculateFinalPrice();
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.unit = unit != null ? unit : "Piece";
        this.minimumStockLevel = minimumStockLevel != null ? minimumStockLevel : 5;
        this.active = true;
    }

    public void calculateFinalPrice() {
        if (this.price == null) {
            this.finalPrice = BigDecimal.ZERO;
            return;
        }
        if (this.discountPercentage == null || this.discountPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            this.finalPrice = this.price;
        } else {
            BigDecimal discountAmount = this.price.multiply(this.discountPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            this.finalPrice = this.price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
        }
    }

    @PrePersist
    protected void onCreate() {
        calculateFinalPrice();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateFinalPrice();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        calculateFinalPrice();
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculateFinalPrice();
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(Integer minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
