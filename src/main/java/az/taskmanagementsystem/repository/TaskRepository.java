package az.taskmanagementsystem.repository;

import az.taskmanagementsystem.entity.Task;
import io.jsonwebtoken.security.Jwks;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, QuerydslPredicateExecutor<Task> {

    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.assignedUser " +
            "LEFT JOIN FETCH t.createdBy " +
            "WHERE t.id = :id")
    @Override
    @NonNull
    Optional<Task> findById(@NonNull Long id);
}
