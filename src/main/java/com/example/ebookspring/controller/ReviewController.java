package com.example.ebookspring.controller;

import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Review;
import com.example.ebookspring.model.User;
import com.example.ebookspring.request.ReviewRequest;
import com.example.ebookspring.service.ReviewService;
import com.example.ebookspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private ReviewService reviewService;
    private UserService userService;

    public ReviewController(ReviewService reviewService,UserService userService) {
        this.reviewService=reviewService;
        this.userService=userService;
        // TODO Auto-generated constructor stub
    }
    @PostMapping("/create")
    public ResponseEntity<Review> createReviewHandler(@RequestBody ReviewRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        User user=userService.findUserProfileByJwt(jwt);
        System.out.println("product id "+req.getProductId()+" - "+req.getReview());
        Review review=reviewService.createReview(req, user);
        System.out.println("product review "+req.getReview());
        return new ResponseEntity<Review>(review, HttpStatus.ACCEPTED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductsReviewHandler(@PathVariable Long productId){
        List<Review>reviews=reviewService.getAllReview(productId);
        return new ResponseEntity<List<Review>>(reviews,HttpStatus.OK);
    }

}
