package az.taskmanagementsystem.service;

import az.taskmanagementsystem.repository.UUIDTokenRepository;
import az.taskmanagementsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepository userRepository;

    private final UUIDTokenRepository uuidTokenRepository;

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 1)
    public void deleteUnusedData(){
        uuidTokenRepository.deleteExpiredUUIDToken();
        userRepository.deleteInactiveUsers();
    }
}


