package com.mvp.vendingmachine.service;

import com.mvp.vendingmachine.exception.DuplicateUserException;
import com.mvp.vendingmachine.exception.ResetDepositException;
import com.mvp.vendingmachine.exception.UserNotFoundException;
import com.mvp.vendingmachine.storage.model.StoredUser;

import java.util.List;

public interface UserService {

    StoredUser createUser(final StoredUser storedUser) throws DuplicateUserException;

    StoredUser findUserByUsername(final String username) throws UserNotFoundException;

    List<StoredUser> findAllUsers();

    StoredUser updateUser(final String username, final StoredUser storedUser);

    void deleteUserByUsername(final String username);

    StoredUser deposit(final int deposit) throws UserNotFoundException;

    void resetDeposit(final String username) throws ResetDepositException;
}
