package io.hhplus.concert.application.user;

import io.hhplus.concert.domain.repository.user.UserPointHistoryRepository;
import io.hhplus.concert.domain.repository.user.UserQueueRepository;
import io.hhplus.concert.domain.repository.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final UserQueueRepository userQueueRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    public UserService(ModelMapper modelMapper, UserRepository userRepository, UserQueueRepository userQueueRepository, UserPointHistoryRepository userPointHistoryRepository) {
        this.modelMapper = modelMapper;
        this.userRepository =userRepository;
        this.userQueueRepository = userQueueRepository;
        this.userPointHistoryRepository = userPointHistoryRepository;
    }

}