package az.taskmanagementsystem.service;

import az.taskmanagementsystem.repository.UUIDTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final UUIDTokenRepository uuidTokenRepository;

    @Bean
    public ApplicationRunner cleanupOnStartup() {
        return args -> deleteExpiredTokens();
    }

    @Scheduled(fixedRate = 86_400_000)
    public void deleteExpiredTokens(){
        uuidTokenRepository.deleteExpiredUUIDToken();
    }
}
