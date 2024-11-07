package io.hhplus.concert.domain.user;

public interface RedisRepository {

    void saveToQueue(String key, String value);
    String getFromQueue(String key);

}