package com.example.ebookspring.service;

import com.example.ebookspring.exception.CartItemException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Cart;
import com.example.ebookspring.model.CartItem;
import com.example.ebookspring.model.Product;

public interface CartItemService {

    public CartItem createCartItem(CartItem cartItem);

    public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException;

    public CartItem isCartItemExist(Cart cart, Product product, String language, Long userId);

    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException;

    public CartItem findCartItemById(Long cartItemId) throws CartItemException;
}
