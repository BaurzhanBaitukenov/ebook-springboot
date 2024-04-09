package com.example.ebookspring.service;

import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.model.Review;
import com.example.ebookspring.model.User;
import com.example.ebookspring.request.ReviewRequest;

import java.util.List;

public interface ReviewService {

    public Review createReview(ReviewRequest req, User user) throws ProductException;
    public List<Review> getAllReview(Long productId);
}
