package com.mvp.vendingmachine.api.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDto {

    private String username;
    private String password;
    private Integer deposit;
    private String role;

}
