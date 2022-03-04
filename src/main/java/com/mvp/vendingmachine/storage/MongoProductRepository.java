package com.mvp.vendingmachine.storage;

import com.mvp.vendingmachine.storage.model.StoredProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoProductRepository extends MongoRepository<StoredProduct, String> {
}
