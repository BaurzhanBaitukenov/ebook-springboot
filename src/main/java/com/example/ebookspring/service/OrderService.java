package com.example.ebookspring.service;

import com.example.ebookspring.exception.OrderException;
import com.example.ebookspring.model.Address;
import com.example.ebookspring.model.Order;
import com.example.ebookspring.model.User;

import java.util.List;

public interface OrderService {

    public Order createOrder(User user, Address shippingAdress);

    public Order findOrderById(Long orderId) throws OrderException;

    public List<Order> usersOrderHistory(Long userId);

    public Order placedOrder(Long orderId) throws OrderException;

    public Order confirmedOrder(Long orderId)throws OrderException;

    public Order shippedOrder(Long orderId) throws OrderException;

    public Order deliveredOrder(Long orderId) throws OrderException;

    public Order cancledOrder(Long orderId) throws OrderException;

    public List<Order>getAllOrders();

    public void deleteOrder(Long orderId) throws OrderException;
}
