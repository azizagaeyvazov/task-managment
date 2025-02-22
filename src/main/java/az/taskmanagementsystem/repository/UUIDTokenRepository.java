package az.taskmanagementsystem.repository;

import az.taskmanagementsystem.entity.UUIDToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UUIDTokenRepository extends JpaRepository<UUIDToken, Long> {

    Optional<UUIDToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM uuidtoken WHERE expiry_date < NOW()", nativeQuery = true)
    void deleteExpiredUUIDToken();
}
