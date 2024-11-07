package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.user.RedisRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveToQueue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String getFromQueue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}