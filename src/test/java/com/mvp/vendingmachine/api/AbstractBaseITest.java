package com.mvp.vendingmachine.api;

import com.mvp.vendingmachine.mapper.UserMapper;
import com.mvp.vendingmachine.storage.MongoProductRepository;
import com.mvp.vendingmachine.storage.MongoUserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public abstract class AbstractBaseITest {
    @Container
    final static FixedHostPortGenericContainer mongoDBContainer = new FixedHostPortGenericContainer("mongo:latest").withFixedExposedPort(27017, 27017);
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    MongoProductRepository mongoProductRepository;
    @Autowired
    UserMapper userMapper;

    @SneakyThrows
    @BeforeAll
    public static void setup() {
        mongoDBContainer.start();

    }

    @AfterAll
    public static void clean() {
        mongoDBContainer.stop();
    }

}
