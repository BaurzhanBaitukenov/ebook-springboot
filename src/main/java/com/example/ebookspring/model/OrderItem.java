package com.example.ebookspring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class OrderItem {
//
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;

    private String language;

    private int quantity;

    private Integer price;

    private Integer discountedPrice;

    private Long userId;

    private LocalDateTime deliveryDate;

    public OrderItem() {
    }

    public OrderItem(Long id, Order order, Product product, String language, int quantity, Integer price,
                     Integer discountedPrice, Long userId, LocalDateTime deliveryDate) {
        super();
        this.id = id;
        this.order = order;
        this.product = product;
        this.language = language;
        this.quantity = quantity;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.userId = userId;
        this.deliveryDate = deliveryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(Integer discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
