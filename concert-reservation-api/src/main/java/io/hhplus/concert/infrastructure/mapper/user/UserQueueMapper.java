package io.hhplus.concert.infrastructure.mapper.user;

import io.hhplus.concert.domain.user.QueueStatus;
import io.hhplus.concert.domain.user.UserQueue;
import io.hhplus.concert.infrastructure.entity.user.UserQueueEntity;

public class UserQueueMapper {

    public static UserQueue toDomain(UserQueueEntity entity) {
        return new UserQueue(
                entity.getQueueId(),
                entity.getUserId(),
                QueueStatus.valueOf(entity.getQueueStatus()),
                entity.getCreateAt(),
                entity.getQueueExpireAt()
        );
    }

    public static UserQueueEntity toEntity(UserQueue userQueue) {
        return UserQueueEntity.builder()
                .queueId(userQueue.getQueueId())
                .userId(userQueue.getUserId())
                .queueStatus(userQueue.getQueueStatus().name())
                .queueExpireAt(userQueue.getQueueExpireAt())
                .build();
    }

}
