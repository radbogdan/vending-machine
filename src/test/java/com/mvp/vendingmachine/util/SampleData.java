package com.mvp.vendingmachine.util;

import com.mvp.vendingmachine.api.model.OrderedProduct;
import com.mvp.vendingmachine.api.model.UserDto;
import com.mvp.vendingmachine.storage.model.StoredProduct;
import com.mvp.vendingmachine.storage.model.StoredUser;

import static com.mvp.vendingmachine.util.Constants.*;

public class SampleData {

    public static StoredUser buildStoredUser() {
        final StoredUser storedUser = new StoredUser();
        storedUser.setUsername(USERNAME_VALUE);
        storedUser.setPassword(Constants.PASSWORD_VALUE);
        storedUser.setDeposit(DEPOSIT);
        storedUser.setRole(Constants.ROLE_BUYER_VALUE);
        return storedUser;
    }
    public static UserDto buildUserDto() {
        final UserDto userDto = new UserDto();
        userDto.setUsername(USERNAME_VALUE);
        userDto.setPassword(Constants.PASSWORD_VALUE);
        userDto.setDeposit(DEPOSIT);
        userDto.setRole(Constants.ROLE_BUYER_VALUE);
        return userDto;
    }
    public static StoredProduct buildStoredProduct() {
        StoredProduct storedProduct = new StoredProduct();
        storedProduct.setProductName(PRODUCT_NAME_VALUE);
        storedProduct.setAmountAvailable(PRODUCT_AMOUNT_AVAILABLE_VALUE);
        storedProduct.setCost(PRODUCT_COST_VALUE);
        storedProduct.setSellerId(USERNAME_VALUE);
        return storedProduct;
    }

    public static OrderedProduct buildOrderedProduct() {
        final OrderedProduct orderedProductDto = new OrderedProduct();
        orderedProductDto.setPurchasedProduct(PRODUCT_NAME_VALUE);
        orderedProductDto.setTotalSpent(TOTAL_SPENT);
        orderedProductDto.setChange(CHANGE);
        return orderedProductDto;
    }
}
