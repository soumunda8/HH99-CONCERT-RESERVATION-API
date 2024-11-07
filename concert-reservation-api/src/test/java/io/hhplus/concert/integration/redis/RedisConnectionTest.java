package io.hhplus.concert.integration.redis;

import io.hhplus.concert.domain.user.RedisQueueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisQueueRepository redisRepository;

    @Test
    public void testRedisConnection() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            assertNotNull(connection, "Redis connection should not be null");
            System.out.println("Redis connection successful: " + connection.ping());
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
        }
    }

    @Test
    public void testRedisData() {
        redisRepository.saveToQueue("testKey", "testValue");

        String value = redisRepository.getFromQueue("testKey");
        assertThat(value).isEqualTo("testValue");
    }

}