package com.mvp.vendingmachine.api;

import com.mvp.vendingmachine.api.model.UserDto;
import com.mvp.vendingmachine.storage.model.StoredUser;
import com.mvp.vendingmachine.util.Constants;
import com.mvp.vendingmachine.util.SampleData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.mvp.vendingmachine.util.Constants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends AbstractBaseITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void depositNotInRange_Return406_Test() throws Exception {
        this.mockMvc.perform(post(Constants.DEPOSIT_URL, 1).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void deposit_Return201WhenUserExistsInDb_Test() throws Exception {
        mongoUserRepository.deleteAll();
        StoredUser storedUser = SampleData.buildStoredUser();
        final int deposit = 10;
        mongoUserRepository.save(storedUser);
        String actual = this.mockMvc.perform(post(Constants.DEPOSIT_URL, deposit).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        storedUser.setDeposit(storedUser.getDeposit() + deposit);
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(storedUser);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_UNKNOWN_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void deposit_Return404WhenUserNotExistsInDb_Test() throws Exception {
        mongoUserRepository.deleteAll();
        final int deposit = 10;
        this.mockMvc.perform(post(Constants.DEPOSIT_URL, deposit).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deposit_Return401_Test() throws Exception {
        this.mockMvc.perform(post(Constants.DEPOSIT_URL, 10).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_UNKNOWN_VALUE,
        authorities = Constants.ROLE_SELLER_VALUE)
    public void deposit_Return403_Test() throws Exception {
        mongoUserRepository.deleteAll();
        this.mockMvc.perform(post(Constants.DEPOSIT_URL, 10).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void depositReset_Return204_Test() throws Exception {
        mongoUserRepository.deleteAll();
        StoredUser storedUser = SampleData.buildStoredUser();
        storedUser.setDeposit(10);
        mongoUserRepository.save(storedUser);
        this.mockMvc.perform(post(Constants.DEPOSIT_RESET_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
        Optional<StoredUser> actual = mongoUserRepository.findById(storedUser.getUsername());
        storedUser.setDeposit(0);
        assertTrue(actual.isPresent());
        assertEquals(storedUser, actual.get());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void depositReset_Return406_Test() throws Exception {
        this.mockMvc.perform(post(Constants.DEPOSIT_RESET_URL, Constants.USERNAME_UNKNOWN_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    public void depositReset_Return401_Test() throws Exception {
        this.mockMvc.perform(post(Constants.DEPOSIT_RESET_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_SELLER_VALUE)
    public void depositReset_Return403_Test() throws Exception {
        this.mockMvc.perform(post(Constants.DEPOSIT_RESET_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createUser_Return201_Test() throws Exception {
        mongoUserRepository.deleteAll();
        ObjectMapper mapper = new ObjectMapper();
        UserDto userDto = SampleData.buildUserDto();
        String response = this.mockMvc.perform(post(Constants.USER_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(userDto)))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UserDto actual = mapper.readValue(response, UserDto.class);
        userDto.setPassword(actual.getPassword());
        assertEquals(userDto, actual);
    }

    @Test
    public void createUser_Return406_Test() throws Exception {
        mongoUserRepository.deleteAll();
        ObjectMapper mapper = new ObjectMapper();
        UserDto userDto = SampleData.buildUserDto();
        userDto.setRole(ROLE_ANONYMUS_VALUE);
        this.mockMvc.perform(post(Constants.USER_URL).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(userDto)))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_SELLER_VALUE)
    public void findUserById_Return200_Test() throws Exception {
        mongoUserRepository.deleteAll();
        StoredUser storedUser = SampleData.buildStoredUser();
        mongoUserRepository.save(storedUser);
        String actual = this.mockMvc.perform(get(Constants.USERNAME_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(storedUser);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_SELLER_VALUE)
    public void findUserById_Return404_Test() throws Exception {
        mongoUserRepository.deleteAll();
        this.mockMvc.perform(get(Constants.USERNAME_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void findAllUsers_Return200_Test() throws Exception {
        mongoUserRepository.deleteAll();
        mongoUserRepository.save(SampleData.buildStoredUser());
        this.mockMvc.perform(get(Constants.USER_URL).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].username", is(Constants.USERNAME_VALUE)))
            .andExpect(jsonPath("$[0].role", is(Constants.ROLE_BUYER_VALUE)));
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void findAllUsers_ReturnEmptyAndStatus200_Test() throws Exception {
        mongoUserRepository.deleteAll();
        this.mockMvc.perform(get(Constants.USER_URL).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = Constants.ROLE_BUYER_VALUE)
    public void updateUsers_ReturnEmptyAndStatus200_Test() throws Exception {
        mongoUserRepository.deleteAll();
        StoredUser storedUser = SampleData.buildStoredUser();
        mongoUserRepository.save(storedUser);
        ObjectMapper mapper = new ObjectMapper();
        UserDto update = new UserDto();
        update.setRole(ROLE_SELLER_VALUE);
        update.setPassword("newS3cr3t");

        String response = mockMvc.perform(put(USERNAME_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(update))).andExpect(status().isOk())
            .andExpect(jsonPath("$.role", is(ROLE_SELLER_VALUE)))
            .andReturn().getResponse().getContentAsString();
        assertTrue(new BCryptPasswordEncoder().matches("newS3cr3t", mapper.readValue(response, UserDto.class).getPassword()));
    }

    @Test
    @WithMockUser(username = Constants.USERNAME_VALUE,
        authorities = ROLE_BUYER_VALUE)
    public void deleteUserById_Return404_Test() throws Exception {
        mongoUserRepository.deleteAll();
        StoredUser storedUser = SampleData.buildStoredUser();
        mongoUserRepository.save(storedUser);
        this.mockMvc.perform(delete(Constants.USERNAME_URL, Constants.USERNAME_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }
}