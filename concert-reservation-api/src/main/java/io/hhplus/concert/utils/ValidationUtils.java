package io.hhplus.concert.utils;

public class ValidationUtils {

    // 사용자 아이디 검증
    public static void validateUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("아이디가 없습니다. 아이디를 확인해주세요.");
        }
    }

}