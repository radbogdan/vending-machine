
package com.mvp.vendingmachine.api;

import com.mvp.vendingmachine.api.model.OrderedProduct;
import com.mvp.vendingmachine.storage.model.StoredProduct;
import com.mvp.vendingmachine.storage.model.StoredUser;
import com.mvp.vendingmachine.util.Constants;
import com.mvp.vendingmachine.util.SampleData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProductControllerTest extends AbstractBaseITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void buyProduct_Return201_Test() throws Exception {
        StoredUser storedUser = SampleData.buildStoredUser();
        storedUser.setDeposit(35);
        StoredProduct storedProduct = SampleData.buildStoredProduct();
        mongoUserRepository.save(storedUser);
        mongoProductRepository.save(storedProduct);
        String actual = this.mockMvc.perform(post(Constants.PRODUCT_BUY_URL, Constants.PRODUCT_NAME_VALUE, 1))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        OrderedProduct orderedProduct = SampleData.buildOrderedProduct();
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(orderedProduct);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void buyProduct_Return417WhenNoProduct_Test() throws Exception {
        mongoProductRepository.deleteAll();
        this.mockMvc.perform(post(Constants.PRODUCT_BUY_URL, Constants.PRODUCT_NAME_VALUE, 1))
            .andExpect(status().isExpectationFailed());

    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void buyProduct_Return417WhenNoUser_Test() throws Exception {
        mongoUserRepository.deleteAll();
        mongoProductRepository.save(SampleData.buildStoredProduct());
        this.mockMvc.perform(post(Constants.PRODUCT_BUY_URL, Constants.PRODUCT_NAME_VALUE, 1))
            .andExpect(status().isExpectationFailed());

    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void buyProduct_Return417WhenAmountRequestedGreaterThanAmountOfPoduct_Test() throws Exception {
        mongoUserRepository.save(SampleData.buildStoredUser());
        mongoProductRepository.save(SampleData.buildStoredProduct());
        this.mockMvc.perform(post(Constants.PRODUCT_BUY_URL, Constants.PRODUCT_NAME_VALUE, 100))
            .andExpect(status().isExpectationFailed());

    }

    @Test
    public void buyProduct_Return401_Test() throws Exception {
        this.mockMvc.perform(post(Constants.PRODUCT_BUY_URL, Constants.PRODUCT_NAME_VALUE, 1))
            .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = Constants.USERNAME_UNKNOWN_VALUE,
        authorities = Constants.ROLE_SELLER_VALUE)
    public void buyProduct_Return403_Test() throws Exception {
        this.mockMvc.perform(post(Constants.PRODUCT_BUY_URL, Constants.PRODUCT_NAME_VALUE, 1))
            .andExpect(status().isForbidden());

    }

}
