package com.example.ebookspring.service;

import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.model.Cart;
import com.example.ebookspring.model.CartItem;
import com.example.ebookspring.model.User;
import com.example.ebookspring.request.AddItemRequest;

public interface CartService {

    public Cart createCart(User user);

    public CartItem addCartItem(Long userId, AddItemRequest req) throws ProductException;

    public Cart findUserCart(Long userId);

}
