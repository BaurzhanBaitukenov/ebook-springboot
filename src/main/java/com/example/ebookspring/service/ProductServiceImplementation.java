package com.example.ebookspring.service;

import com.example.ebookspring.exception.ProductException;
import com.example.ebookspring.model.Category;
import com.example.ebookspring.model.Product;
import com.example.ebookspring.repository.CategoryRepository;
import com.example.ebookspring.repository.ProductRepository;
import com.example.ebookspring.request.CreateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplementation implements ProductService{

    private ProductRepository productRepository;
    private UserService userService;
    private CategoryRepository categoryRepository;

    public ProductServiceImplementation(ProductRepository productRepository, UserService userService, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(CreateProductRequest req) {

        Category topLevel=categoryRepository.findByName(req.getTopLavelCategory());

        if(topLevel==null) {

            Category topLavelCategory=new Category();
            topLavelCategory.setName(req.getTopLavelCategory());
            topLavelCategory.setLevel(1);

            topLevel= categoryRepository.save(topLavelCategory);
        }

        Category secondLevel=categoryRepository.
                findByNameAndParent(req.getSecondLavelCategory(),topLevel.getName());
        if(secondLevel==null) {

            Category secondLavelCategory=new Category();
            secondLavelCategory.setName(req.getSecondLavelCategory());
            secondLavelCategory.setParentCategory(topLevel);
            secondLavelCategory.setLevel(2);

            secondLevel= categoryRepository.save(secondLavelCategory);
        }

        Category thirdLevel=categoryRepository.findByNameAndParent(req.getThirdLavelCategory(),secondLevel.getName());
        if(thirdLevel==null) {

            Category thirdLavelCategory=new Category();
            thirdLavelCategory.setName(req.getThirdLavelCategory());
            thirdLavelCategory.setParentCategory(secondLevel);
            thirdLavelCategory.setLevel(3);

            thirdLevel=categoryRepository.save(thirdLavelCategory);
        }


        Product product=new Product();
        product.setTitle(req.getTitle());
        product.setAuthor(req.getAuthor());
        product.setGenre(req.getGenre());
        product.setDescription(req.getDescription());
        product.setDiscountedPrice(req.getDiscountedPrice());
        product.setDiscountPersent(req.getDiscountPersent());
        product.setImageUrl(req.getImageUrl());
        product.setGenre(req.getGenre());
        product.setPrice(req.getPrice());
        product.setLanguages(req.getLanguage());
        product.setQuantity(req.getQuantity());
        product.setCategory(thirdLevel);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct= productRepository.save(product);

        System.out.println("products - "+product);

        return savedProduct;
    }

    @Override
    public String deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        product.getLanguages().clear();
        productRepository.delete(product);
        return "Product deleted successfully";
    }

    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {
        Product product = findProductById(productId);

        if(req.getQuantity() != 0) {
            product.setQuantity(req.getQuantity());
        }

        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long id) throws ProductException {
        Optional<Product> opt=productRepository.findById(id);

        if(opt.isPresent()) {
            return opt.get();
        }
        throw new ProductException("product not found with id "+id);
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        return null;
    }

    @Override
    public Page<Product> getAllProduct(String category, List<String> genres, List<String> languages, Integer minPrice,
                                       Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Product> products = productRepository.filterProducts(category, minPrice, maxPrice, minDiscount, sort);


        if (!genres.isEmpty()) {
            products = products.stream()
                    .filter(p -> genres.stream().anyMatch(c -> c.equalsIgnoreCase(p.getGenre())))
                    .collect(Collectors.toList());


        }

        if(stock!=null) {

            if(stock.equals("in_stock")) {
                products=products.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList());
            }
            else if (stock.equals("out_of_stock")) {
                products=products.stream().filter(p->p.getQuantity()<1).collect(Collectors.toList());
            }


        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        List<Product> pageContent = products.subList(startIndex, endIndex);
        Page<Product> filteredProducts = new PageImpl<>(pageContent, pageable, products.size());
        return filteredProducts;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> recentlyAddedProduct() {
        return productRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
