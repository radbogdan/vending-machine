package com.mvp.vendingmachine.storage.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class StoredProduct {
    @Id
    private String productName;
    private int amountAvailable;
    private int cost;
    private String sellerId;

}
