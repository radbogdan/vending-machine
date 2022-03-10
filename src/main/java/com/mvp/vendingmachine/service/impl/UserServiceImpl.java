package com.mvp.vendingmachine.service.impl;import com.mvp.vendingmachine.exception.DuplicateUserException;import com.mvp.vendingmachine.exception.ResetDepositException;import com.mvp.vendingmachine.exception.UserNotFoundException;import com.mvp.vendingmachine.service.UserService;import com.mvp.vendingmachine.storage.MongoUserRepository;import com.mvp.vendingmachine.storage.model.StoredUser;import org.springframework.data.mongodb.core.FindAndModifyOptions;import org.springframework.data.mongodb.core.MongoTemplate;import org.springframework.data.mongodb.core.query.Criteria;import org.springframework.data.mongodb.core.query.Query;import org.springframework.data.mongodb.core.query.Update;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.stereotype.Service;import java.util.List;import java.util.Optional;import static java.lang.String.format;@Servicepublic class UserServiceImpl implements UserService {    private static final String USERNAME_VALUE = "username";    private static final String PASSWORD_VALUE = "password";    private static final String DEPOSIT_VALUE = "deposit";    private static final String ROLE_VALUE = "role";    private final MongoUserRepository mongoUserRepository;    private final MongoTemplate mongoTemplate;    public UserServiceImpl(final MongoUserRepository mongoUserRepository, final MongoTemplate mongoTemplate) {        this.mongoUserRepository = mongoUserRepository;        this.mongoTemplate = mongoTemplate;    }    @Override    public StoredUser createUser(final StoredUser storedUser) throws DuplicateUserException {        try {            return mongoUserRepository.insert(storedUser);        } catch (Exception exception) {            throw new DuplicateUserException(format("User with name [%s] already exist", storedUser.getUsername()));        }    }    @Override    public StoredUser findUserByUsername(final String username) throws UserNotFoundException {        return mongoUserRepository.findById(username)            .orElseThrow(() -> new UserNotFoundException(format("User not found with name %s", username)));    }    @Override    public List<StoredUser> findAllUsers() {        return mongoUserRepository.findAll();    }    @Override    public StoredUser updateUser(final String username, final StoredUser update) {        Optional<StoredUser> retrieved = mongoUserRepository.findById(username);        if (retrieved.isPresent()) {            if (update.getPassword() != null) {                retrieved.get().setPassword(update.getPassword());            }            if (update.getRole() != null) {                retrieved.get().setRole(update.getRole());            }            return mongoTemplate.findAndModify(                Query.query(Criteria.where(USERNAME_VALUE).is(username)),                Update.update(PASSWORD_VALUE, retrieved.get().getPassword()).set(ROLE_VALUE, retrieved.get().getRole()),                FindAndModifyOptions.options().returnNew(true),                StoredUser.class            );        } else {            update.setUsername(username);            update.setDeposit(0);            return mongoTemplate.insert(update);        }    }    @Override    public void deleteUserByUsername(final String username) {        mongoUserRepository.deleteById(username);    }    @Override    public StoredUser deposit(final int deposit) throws UserNotFoundException {        final String loggedUser = getLoggedUser();        Optional<StoredUser> retrieved = mongoUserRepository.findById(loggedUser);        if (retrieved.isPresent()) {            return mongoTemplate.findAndModify(                Query.query(Criteria.where(USERNAME_VALUE).is(loggedUser)),                Update.update(DEPOSIT_VALUE, deposit + retrieved.get().getDeposit()),                FindAndModifyOptions.options().returnNew(true),                StoredUser.class            );        }        throw new UserNotFoundException(format("User not found with name %s", loggedUser));    }    @Override    public void resetDeposit(final String username) throws ResetDepositException {        final String loggedUser = getLoggedUser();        if (!loggedUser.equals(username)) {            throw new ResetDepositException(format("User [%s] is not allowed to reset deposit for user [%s]", username, loggedUser));        }        mongoTemplate.findAndModify(            Query.query(Criteria.where(USERNAME_VALUE).is(username)),            Update.update(DEPOSIT_VALUE, 0),            FindAndModifyOptions.options().returnNew(true),            StoredUser.class        );    }    private static String getLoggedUser() {        return SecurityContextHolder.getContext().getAuthentication().getName();    }}