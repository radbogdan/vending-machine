package com.mvp.vendingmachine.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderedProduct {

    private String purchasedProduct;
    private Integer totalSpent;
    private List<Integer> change;
}
