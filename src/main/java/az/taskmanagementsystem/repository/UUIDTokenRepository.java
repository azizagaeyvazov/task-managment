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

//    Optional<UUIDToken> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query(value = "delete from uuidtoken where user_id = :userId", nativeQuery = true)
    void deleteUUIDTokenByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM uuidtoken WHERE user_id = :userId AND expiry_date < NOW()", nativeQuery = true)
    void deleteExpiredUUIDTokenByUserId(@Param("userId") Long userId);   // find use case
}
