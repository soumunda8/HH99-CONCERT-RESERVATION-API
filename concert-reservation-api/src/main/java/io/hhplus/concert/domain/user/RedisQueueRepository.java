package io.hhplus.concert.domain.user;

import java.time.Duration;
import java.util.Set;

public interface RedisQueueRepository {

    void saveToQueue(String key, String value);
    String getFromQueue(String key);
    void addWaitQueue(String userId, double score);
    Long getUserOrderInWait(String userId);
    Set<String> getTopUsersInWait(int num);
    void setActive(String userId, String value, Duration ttl);
    void removeWait(String userId);
    String getActiveUserInfo(String userId);
    void deleteActiveUser(String userId);
    long countActiveUsers();

}