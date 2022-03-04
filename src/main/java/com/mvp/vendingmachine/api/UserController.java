package com.mvp.vendingmachine.api;

import com.mvp.vendingmachine.api.model.UserDto;
import com.mvp.vendingmachine.config.RolesProperties;
import com.mvp.vendingmachine.exception.DuplicateUserException;
import com.mvp.vendingmachine.exception.ResetDepositException;
import com.mvp.vendingmachine.exception.UserNotFoundException;
import com.mvp.vendingmachine.mapper.UserMapper;
import com.mvp.vendingmachine.service.UserService;
import com.mvp.vendingmachine.storage.model.StoredUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;


@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final RolesProperties rolesProperties;

    public UserController(final UserService userService, final UserMapper mapper, final PasswordEncoder passwordEncoder, final RolesProperties rolesProperties) {
        this.userService = userService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.rolesProperties = rolesProperties;
    }

    @PostMapping("/user")
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        if (!rolesProperties.getRoles().contains(userDto.getRole())) {
            final Map<String, String> message = new HashMap<>();
            log.error("Role {} is undefined at this moment! Please select one of the following roles {}", userDto.getRole(), rolesProperties.getRoles());
            message.put("message", format("Role [%s] is undefined at this moment! Please select one of the following roles %s", userDto.getRole(), rolesProperties.getRoles()));
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
        }
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setDeposit(0);
        try {
            StoredUser storedUser = userService.createUser(mapper.asStoredUser(userDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.asUserDto(storedUser));
        } catch (DuplicateUserException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserDto> findUserByUsername(@PathVariable String username) throws UserNotFoundException {
        StoredUser storedUser = userService.findUserByUsername(username);
        return ResponseEntity.ok(mapper.asUserDto(storedUser));
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserDto>> findAllUsers() {
        return ResponseEntity.ok(mapper.asUserList(userService.findAllUsers()));
    }

    @PutMapping("/user/{username}")
    public ResponseEntity<Object> updateUserByUsername(@PathVariable String username, @RequestBody UserDto userDto) {
        if (userDto.getPassword() != null) {
            userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        StoredUser storedUser = userService.updateUser(username, mapper.asStoredUser(userDto));
        return ResponseEntity.ok(mapper.asUserDto(storedUser));
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Object> deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deposit/{deposit}")
    @PreAuthorize("hasRole('ROLE_BUYER')")
    public ResponseEntity<Object> deposit(@PathVariable int deposit) throws UserNotFoundException {
        if (checkDeposit(deposit)) {
            final StoredUser storedUser = userService.deposit(deposit);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.asUserDto(storedUser));
        }
        final Map<String, String> message = new HashMap<>();
        log.error("You can deposit only 5, 10, 20, 50 and 100 cents, one coin at a time!");
        message.put("message", "You can deposit only 5, 10, 20, 50 and 100 cents, one coin at a time!");
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(message);
    }

    @PostMapping("/deposit/reset/{username}")
    @PreAuthorize("hasRole('ROLE_BUYER')")
    public ResponseEntity<Void> resetDeposit(final @PathVariable String username) {
        try {
            userService.resetDeposit(username);
            log.info("Deposit was reseed to 0!");
            return ResponseEntity.noContent().build();
        } catch (ResetDepositException exception) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    private boolean checkDeposit(int deposit) {
        return List.of(5, 10, 20, 50, 100).contains(deposit);
    }

}
