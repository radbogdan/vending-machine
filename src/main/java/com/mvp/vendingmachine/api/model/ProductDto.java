package com.mvp.vendingmachine.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String productName;
    private int amountAvailable;
    private int cost;
    private String sellerId;
}
