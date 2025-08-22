package com.myproject.todo_management.respository;

import com.myproject.todo_management.entity.Todo;
import com.myproject.todo_management.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    boolean existsByTitleAndUser(String title, User user);

    List<Todo> findByCreatedBy(String createdBy);

    @Modifying
    @Transactional
    @Query("DELETE FROM Todo t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
