package az.taskmanagementsystem.repository;

import az.taskmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE role != 'ADMIN'", nativeQuery = true)
    List<User> findAllExceptAdmin();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users u WHERE u.is_enabled = false " +
            "AND u.id NOT IN (SELECT user_id FROM uuidtoken WHERE expiry_date >= NOW());", nativeQuery = true)
    void deleteInactiveUsers();
}
