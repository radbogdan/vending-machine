package com.mvp.vendingmachine.api;

import com.mvp.vendingmachine.mapper.UserMapper;
import com.mvp.vendingmachine.storage.MongoProductRepository;
import com.mvp.vendingmachine.storage.MongoUserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractBaseITest {

    final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withReuse(true);
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    MongoProductRepository mongoProductRepository;
    @Autowired
    UserMapper userMapper;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
    }

    @SneakyThrows
    @BeforeAll
    public static void setup() {
        mongoDBContainer.start();
    }

}
