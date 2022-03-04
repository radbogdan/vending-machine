package com.mvp.vendingmachine.util;

import java.util.List;

public class Constants {
    public static final String USERNAME_VALUE = "testUser";
    public static final String PASSWORD_VALUE = "s3cr3t";
    public static final String USERNAME_UNKNOWN_VALUE = "unknown";
    public static final String ROLE_BUYER_VALUE = "ROLE_BUYER";
    public static final String ROLE_SELLER_VALUE = "ROLE_SELLER";
    public static final String ROLE_ANONYMUS_VALUE = "ROLE_ANONYMUS";
    public static final String USER_URL = "/user";
    public static final String USERNAME_URL = "/user/{username}";
    public static final String DEPOSIT_URL = "/deposit/{deposit}";
    public static final Integer DEPOSIT = 0;
    public static final String DEPOSIT_RESET_URL = "/deposit/reset/{username}";
    public static final String PRODUCT_BUY_URL = "/product/buy/{productId}/{amountOfProducts}";
    public static final String PRODUCT_NAME_VALUE = "cola";
    public static final Integer PRODUCT_AMOUNT_AVAILABLE_VALUE = 10;
    public static final Integer PRODUCT_COST_VALUE = 10;
    public static final Integer TOTAL_SPENT = 10;
    public static final List<Integer> CHANGE = List.of(1, 0, 1, 0, 0);
}
