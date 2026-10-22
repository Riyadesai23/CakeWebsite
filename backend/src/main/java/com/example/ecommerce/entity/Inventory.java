package com.example.ecommerce.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "inventory", indexes = {
    @Index(name = "idx_inventory_product", columnList = "product_id", unique = true)
})
public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel = 5;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_restocked_at")
    private Date lastRestockedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    public Inventory() {
    }

    public Inventory(Product product, Integer availableQuantity, Integer minimumStockLevel) {
        this.product = product;
        this.availableQuantity = availableQuantity != null ? availableQuantity : 0;
        this.reservedQuantity = 0;
        this.minimumStockLevel = minimumStockLevel != null ? minimumStockLevel : 5;
        this.lastRestockedAt = new Date();
        this.updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        if (this.lastRestockedAt == null) {
            this.lastRestockedAt = new Date();
        }
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public boolean isLowStock() {
        return this.availableQuantity != null && this.minimumStockLevel != null 
                && this.availableQuantity <= this.minimumStockLevel;
    }

    public boolean isOutOfStock() {
        return this.availableQuantity == null || this.availableQuantity <= 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(Integer minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public Date getLastRestockedAt() {
        return lastRestockedAt;
    }

    public void setLastRestockedAt(Date lastRestockedAt) {
        this.lastRestockedAt = lastRestockedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
