package com.mvp.vendingmachine.storage.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class StoredUser {
    @Id
    private String username;
    private String password;
    private Integer deposit;
    private String role;
}
