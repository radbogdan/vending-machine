package com.mvp.vendingmachine.storage;

import com.mvp.vendingmachine.storage.model.StoredUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepository extends MongoRepository<StoredUser, String> {
}
