package com.mvp.vendingmachine.api;

import com.mvp.vendingmachine.api.model.ProductDto;
import com.mvp.vendingmachine.exception.AvailableProductException;
import com.mvp.vendingmachine.exception.DuplicateProductException;
import com.mvp.vendingmachine.exception.ProductNotFoundException;
import com.mvp.vendingmachine.exception.UserNotFoundException;
import com.mvp.vendingmachine.mapper.ProductMapper;
import com.mvp.vendingmachine.service.ProductService;
import com.mvp.vendingmachine.storage.model.StoredProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper mapper;

    public ProductController(final ProductService productService, final ProductMapper mapper) {
        this.productService = productService;
        this.mapper = mapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<Object> createProduct(@RequestBody ProductDto productDto) throws UserNotFoundException {
        try {
            if (productDto.getCost() % 5 != 0) {
                log.error("Invalid cost for the product {}! Should be in multiples of 5.", productDto.getProductName());
                final Map<String, String> message = new HashMap<>();
                message.put("message", format("Invalid cost for the product [%s]! Should be in multiples of 5", productDto.getProductName()));
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
            }
            StoredProduct storedProduct = productService.createProduct(mapper.asStoredProduct(productDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.asProductDto(storedProduct));
        } catch (DuplicateProductException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{productName}")
    public ResponseEntity<ProductDto> findProductByProductName(@PathVariable String productName) throws ProductNotFoundException {
        StoredProduct storedProduct = productService.findProductByProductName(productName);
        return ResponseEntity.ok(mapper.asProductDto(storedProduct));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> findAllProducts() {
        return ResponseEntity.ok(mapper.asProductList(productService.findAllProducts()));
    }

    @PutMapping("/{productName}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<Object> updateUserByUsername(@PathVariable String productName, @RequestBody ProductDto productDto) {
        if (productDto.getCost() % 5 != 0) {
            log.error("Invalid cost for the product {}! Should be in multiples of 5.", productName);
            final Map<String, String> message = new HashMap<>();
            message.put("message", format("Invalid cost for the product [%s]! Should be in multiples of 5", productName));
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
        }
        try {
            final StoredProduct storedProduct = productService.updateProduct(productName, mapper.asStoredProduct(productDto));
            return ResponseEntity.ok(mapper.asProductDto(storedProduct));
        } catch (DuplicateProductException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{productName}")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    public ResponseEntity<Object> deleteProductByProductName(@PathVariable String productName) {
        productService.deleteUserByProductName(productName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/buy/{productId}/{amountOfProducts}")
    @PreAuthorize("hasRole('ROLE_BUYER')")
    public ResponseEntity<Object> buyProduct(@PathVariable String productId, @PathVariable Integer amountOfProducts) {
        try {
            return ResponseEntity.ok(productService.buyProduct(productId, amountOfProducts));
        } catch (ProductNotFoundException | UserNotFoundException | AvailableProductException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
    }
}
