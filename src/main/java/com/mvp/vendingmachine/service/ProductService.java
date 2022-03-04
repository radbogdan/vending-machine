package com.mvp.vendingmachine.service;

import com.mvp.vendingmachine.api.model.OrderedProduct;
import com.mvp.vendingmachine.exception.AvailableProductException;
import com.mvp.vendingmachine.exception.DuplicateProductException;
import com.mvp.vendingmachine.exception.ProductNotFoundException;
import com.mvp.vendingmachine.exception.UserNotFoundException;
import com.mvp.vendingmachine.storage.model.StoredProduct;

import java.util.List;

public interface ProductService {

    StoredProduct createProduct(final StoredProduct storedProduct) throws UserNotFoundException, DuplicateProductException;

    StoredProduct findProductByProductName(final String productName) throws ProductNotFoundException;

    List<StoredProduct> findAllProducts();

    StoredProduct updateProduct(final String productName, final StoredProduct storedProduct) throws DuplicateProductException;

    void deleteUserByProductName(final String productName);

    OrderedProduct buyProduct(final String productId, final Integer amountOfProducts) throws ProductNotFoundException, UserNotFoundException, AvailableProductException;
}
