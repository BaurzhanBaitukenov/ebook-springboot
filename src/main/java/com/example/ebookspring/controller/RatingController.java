package com.example.ebookspring.controller;

import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Rating;
import com.example.ebookspring.model.User;
import com.example.ebookspring.request.RatingRequest;
import com.example.ebookspring.service.RatingService;
import com.example.ebookspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private UserService userService;
    private RatingService ratingServices;

    public RatingController(UserService userService,RatingService ratingServices) {
        this.ratingServices=ratingServices;
        this.userService=userService;
        // TODO Auto-generated constructor stub
    }

    @PostMapping("/create")
    public ResponseEntity<Rating> createRatingHandler(@RequestBody RatingRequest req, @RequestHeader("Authorization") String jwt) throws UserException, ProductException {
        User user=userService.findUserProfileByJwt(jwt);
        Rating rating=ratingServices.createRating(req, user);
        return new ResponseEntity<>(rating, HttpStatus.ACCEPTED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Rating>> getProductsReviewHandler(@PathVariable Long productId){

        List<Rating> ratings=ratingServices.getProductsRating(productId);
        return new ResponseEntity<>(ratings,HttpStatus.OK);
    }
}
