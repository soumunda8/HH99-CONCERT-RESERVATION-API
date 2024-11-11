# Redis를 통한 대기열 관리

## 1. 개요
콘서트 예약 시스템에서 대기열 관리 성능을 최적화하기 위해 Redis의 자료 구조를 활용한 방안을 설명합니다. 대기열에 진입한 사용자는 **대기 상태(wait token)** 로 관리되며, 각 사용자 순서에 따라 **Active Token**이 부여되어 콘서트 예약이 가능해집니다. Redis를 통해 대기열과 Active Token을 효율적으로 관리하여 성능을 개선하고자 합니다.

## 2. 문제 정의
### 2.1 기존 시스템의 문제점과 성능 저하 요인
- **대기열 관리의 비효율성 및 N+1 문제**: 기존 시스템에서 대기열 정보를 관계형 데이터베이스에서 관리하는 과정에서, 다수의 사용자가 동시에 접근할 경우 지연이 발생하며, 특히 여러 사용자의 상태를 한 번에 조회할 때 N+1 문제가 발생해 응답 시간이 길어집니다.
- **Active Token 관리의 복잡성 및 반복적 데이터 조회**: 대기열 상위 사용자에게만 Active Token을 부여하기 위해 대기 상태를 빠르게 확인하고 관리해야 하며, Active Token 상태 조회가 반복되면서 DB 접근이 빈번해져 시스템 부하가 증가합니다.

### 2.2 성능 개선 목표
- **빠른 응답 제공**: Redis의 빠른 조회 성능을 활용해 대기열 순번 조회와 Active Token 추출 작업을 최적화합니다.
- **DB 부하 감소**: 대기열과 토큰 관리 로직을 Redis로 이관하여 DB 접근 빈도를 낮추고, 시스템 전반의 성능을 최적화합니다.

## 3. 성능 개선 전략

### 3.1 Redis Sorted Sets을 통한 대기열 순번 관리
**Sorted Set**은 대기 순서를 효율적으로 관리하는 데 최적화된 자료 구조입니다. 사용자의 대기열 진입 시간을 기준으로 Sorted Set에 사용자 ID를 점수(score)와 함께 저장하여, 자동 정렬된 대기 순번을 유지합니다.
- **구현**:
  - `ZADD`를 사용해 대기열에 진입한 사용자와 진입 시간을 저장합니다.
  - `ZRANK`를 통해 사용자의 대기열 순번을 확인합니다.
  - `ZRANGE`로 상위 30명의 대기 상태를 조회하고, Active Token을 부여합니다.

#### 예제 코드
```java
public void addToQueue(String userId) {
    long score = System.currentTimeMillis();
    redisTemplate.opsForZSet().add("waitQueue", userId, score);
}

public Long getQueuePosition(String userId) {
    return redisTemplate.opsForZSet().rank("waitQueue", userId);
}

public Set<String> getTopUsersForActive() {
    return redisTemplate.opsForZSet().range("waitQueue", 0, 29); // 상위 30명 조회
}
```

### 3.2 Active Token 관리 - Redis Sets vs. Redis Strings 비교

Active Token을 관리하는 방식에는 Redis Sets과 Redis Strings 두 가지 방법이 있습니다. 각 방식의 특징과 장단점을 비교하여 상황에 맞는 자료 구조를 선택할 수 있습니다.

#### Redis Sets을 이용한 Active Token 관리
- **특징**: 여러 사용자의 Active Token을 하나의 Set으로 그룹화하여 관리할 수 있으며, 한 번에 조회가 가능합니다.
- **장점**:
  - **효율적인 그룹 관리**: 대기열에서 Active 상태로 전환된 사용자를 하나의 Set으로 관리하여, 특정 사용자가 Active 상태인지 손쉽게 조회할 수 있습니다.
  - **TTL 설정으로 전체 Set 자동 만료 가능**: Set에 TTL을 설정하여 모든 Active Token을 일괄 관리할 수 있습니다.
- **단점**:
  - Set 내 개별 Token에 TTL을 별도로 설정할 수 없기 때문에, Active Token이 부여된 모든 사용자가 동일한 만료 시간을 가집니다.

#### Redis Strings을 이용한 Active Token 관리
- **특징**: 사용자별로 개별 Active Token을 관리하며, 각 Token에 개별 TTL을 설정할 수 있습니다.
- **장점**:
  - **개별 TTL 관리 용이**: 사용자별로 Active Token의 TTL을 설정할 수 있어, 특정 사용자마다 다른 만료 시간 설정이 가능합니다.
  - **간단한 구조**: 단일 사용자별 토큰 관리가 필요할 때 효율적입니다.
- **단점**:
  - 사용자가 많아지면 관리해야 할 키의 수가 늘어나며, 메모리 사용량이 증가할 수 있습니다.

#### 예제 코드 - Redis Sets를 통한 Active Token 관리
```java
public void promoteToActive(String userId) {
    redisTemplate.opsForSet().add("activeTokens", userId);
    redisTemplate.expire("activeTokens", Duration.ofMinutes(5)); // 전체 Set의 TTL 설정
    redisTemplate.opsForZSet().remove("waitQueue", userId); // 대기열에서 제거
}

public boolean isTokenActive(String userId) {
    return redisTemplate.opsForSet().isMember("activeTokens", userId);
}
```

#### 예제 코드 - Redis Strings를 통한 Active Token 관리
```java
public void promoteToActiveWithIndividualTTL(String userId) {
    redisTemplate.opsForValue().set("activeToken:" + userId, "active", Duration.ofMinutes(5)); // 사용자별 TTL 설정
    redisTemplate.opsForZSet().remove("waitQueue", userId); // 대기열에서 제거
}

public boolean isTokenActiveWithIndividualTTL(String userId) {
    return redisTemplate.hasKey("activeToken:" + userId);
}
```

### 선택 기준
- **그룹 단위로 Active Token을 관리**해야 하는 경우, Redis Sets가 적합하며, 모든 Active Token이 동일한 만료 시간을 가집니다.
- **개별 사용자별로 TTL 설정**이 필요한 경우 Redis Strings을 선택하는 것이 유리합니다.

## 4. Redis 성능 개선 예상 시나리오

- **동시 사용자 수 증가**: Redis 캐싱 적용을 통해 동시 접속자가 많아도 안정적으로 대기열 데이터를 처리할 수 있습니다.
- **DB 트랜잭션 감소**: Redis에 대기열 관련 정보를 저장함으로써 DB에서의 트랜잭션을 최소화하여 안정성을 높입니다.

## 5. 결론
Redis의 Sorted Sets와 Sets 또는 Strings를 통한 대기열 및 Active Token 관리는 반복적인 데이터 조회와 상태 업데이트의 효율성을 높여 응답 시간을 크게 줄일 수 있는 방안입니다. **개별 사용자별 TTL 설정을 위해 Redis Strings을 사용하는 것이 적합하다고 결정했습니다.**

