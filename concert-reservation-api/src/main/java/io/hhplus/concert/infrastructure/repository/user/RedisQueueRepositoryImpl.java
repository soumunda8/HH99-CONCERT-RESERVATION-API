package io.hhplus.concert.infrastructure.repository.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.RedisQueueRepository;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

@Repository
public class RedisQueueRepositoryImpl implements RedisQueueRepository {

    private static final String WAIT_QUEUE = QueueStatus.STANDBY.name();
    private static final String ACTIVE_USER_PREFIX = "test_active_user:";

    private final RedisTemplate<String, String> redisTemplate;

    public RedisQueueRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
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

    // 1. 대기열 사용자 추가
    @Override
    public void addWaitQueue(String userId, double score) {
        redisTemplate.opsForZSet().add(WAIT_QUEUE, userId, score);
    }

    // 2. 대기열 순위 조회
    @Override
    public Long getUserOrderInWait(String userId) {
        return redisTemplate.opsForZSet().rank(WAIT_QUEUE, userId);
    }

    // 3. 대기열 상위 n명 추출
    @Override
    public Set<String> getTopUsersInWait(int num) {
        return redisTemplate.opsForZSet().range(WAIT_QUEUE, 0, num - 1);
    }

    // 4. 활성 상태로 전환된 사용자 TTL 설정
    @Override
    public void setActive(String userId, String value, Duration ttl) {
        redisTemplate.opsForValue().set(ACTIVE_USER_PREFIX + userId, value, ttl);
    }

    // 5. 대기열 사용자 제거
    @Override
    public void removeWait(String userId) {
        redisTemplate.opsForZSet().remove(WAIT_QUEUE, userId);
    }

    // 6. 활성 사용자 확인
    @Override
    public String getActiveUserInfo(String userId) {
        return redisTemplate.opsForValue().get(ACTIVE_USER_PREFIX + userId);
    }

    // 7. 결제 완료 시 활성 사용자 삭제
    @Override
    public void deleteActiveUser(String userId) {
        redisTemplate.delete(ACTIVE_USER_PREFIX + userId);
    }

    @Override
    public long countActiveUsers() {
        long activeCount = 0;

        ScanOptions options = ScanOptions.scanOptions().match(ACTIVE_USER_PREFIX + "*").count(1000).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            String value = redisTemplate.opsForValue().get(key);

            if (value != null && Objects.equals(value, QueueStatus.ACTIVE.name())) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl > 0) {
                    activeCount++;
                }
            }
        }
        return activeCount;
    }

}