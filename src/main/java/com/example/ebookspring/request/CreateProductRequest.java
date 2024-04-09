package com.example.ebookspring.request;

import com.example.ebookspring.model.Language;

import java.util.HashSet;
import java.util.Set;

public class CreateProductRequest {

    private String title;

    private String author;

    private String description;

    private int price;

    private int discountedPrice;

    private int discountPersent;

    private int quantity;

    private String genre;

    private Set<Language> language = new HashSet<>();

    private String imageUrl;

    private String topLavelCategory;

    private String secondLavelCategory;

    private String thirdLavelCategory;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getDiscountPersent() {
        return discountPersent;
    }

    public void setDiscountPersent(int discountPersent) {
        this.discountPersent = discountPersent;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Set<Language> getLanguage() {
        return language;
    }

    public void setLanguage(Set<Language> language) {
        this.language = language;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTopLavelCategory() {
        return topLavelCategory;
    }

    public void setTopLavelCategory(String topLavelCategory) {
        this.topLavelCategory = topLavelCategory;
    }

    public String getSecondLavelCategory() {
        return secondLavelCategory;
    }

    public void setSecondLavelCategory(String secondLavelCategory) {
        this.secondLavelCategory = secondLavelCategory;
    }

    public String getThirdLavelCategory() {
        return thirdLavelCategory;
    }

    public void setThirdLavelCategory(String thirdLavelCategory) {
        this.thirdLavelCategory = thirdLavelCategory;
    }
}
