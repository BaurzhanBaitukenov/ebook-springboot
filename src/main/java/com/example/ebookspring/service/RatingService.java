package com.example.ebookspring.service;


import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.model.Rating;
import com.example.ebookspring.model.User;
import com.example.ebookspring.request.RatingRequest;

import java.util.List;

public interface RatingService {

    public Rating createRating(RatingRequest req, User user) throws ProductException;

    public List<Rating> getProductsRating(Long productId);
}
