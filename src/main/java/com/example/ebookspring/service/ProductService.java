package com.example.ebookspring.service;

import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.model.Product;
import com.example.ebookspring.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public Product createProduct(CreateProductRequest req);

    public String deleteProduct(Long productId) throws ProductException;

    public Product updateProduct(Long productId, Product req) throws ProductException;

    public Product findProductById(Long id) throws ProductException;

    public List<Product> findProductByCategory(String category);

    public Page<Product> getAllProduct(String category, List<String> genres, List<String> languages, Integer minPrice, Integer maxPrice,
                                       Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize);

    public List<Product> getAllProducts();

    public List<Product> recentlyAddedProduct();
}
