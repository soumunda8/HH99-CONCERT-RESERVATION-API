package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PointRechargeConcurrencyTest {

    @Autowired
    private UserService userService;  // 필드 주입

    private String testUserId;

    @BeforeEach
    public void setUp() {
        testUserId = "testUser";  // 테스트를 위한 사용자 ID
        userService.addUser(testUserId);  // 테스트 사용자 생성
        userService.updateRechargePoints(testUserId, 1000L); // 초기 포인트 설정 (1,000포인트)
    }

    @Test
    @Transactional  // 트랜잭션 보장
    public void testConcurrentPointRecharge() throws InterruptedException {
        int numberOfThreads = 10;  // 동시에 충전 요청을 보낼 스레드 개수
        long rechargeAmount = 200L;  // 각 스레드당 충전할 포인트
        long insufficientAmount = 1500L;  // 포인트 부족 상황을 위한 금액

        // 스레드 풀과 CountDownLatch 설정
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 여러 스레드에서 동시에 포인트 충전 요청을 보냅니다.
        for (int i = 0; i < numberOfThreads; i++) {
            final long amount = (i == 0) ? insufficientAmount : rechargeAmount; // 첫 번째 스레드는 부족한 포인트 충전 시도
            executorService.submit(() -> {
                try {
                    if (amount > 1000L) {
                        assertThrows(IllegalArgumentException.class, () -> {
                            userService.updateUsePoints(testUserId, amount);  // 포인트 사용
                        });
                    } else {
                        userService.updateRechargePoints(testUserId, amount);  // 포인트 충전
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 작업을 마칠 때까지 대기
        latch.await();

        // 충전 실패 후 최종 포인트 확인 (실패한 스레드는 제외)
        User user = userService.getUserInfo(testUserId);
        long expectedPoints = 1000L + ((numberOfThreads - 1) * rechargeAmount);  // 첫 번째 스레드는 충전 실패
        assertThat(user.getPoints()).isEqualTo(expectedPoints);  //
    }

}