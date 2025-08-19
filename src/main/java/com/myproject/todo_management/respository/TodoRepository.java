package com.myproject.todo_management.respository;

import com.myproject.todo_management.entity.Todo;
import com.myproject.todo_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    boolean existsByTitleAndUser(String title, User user);

    List<Todo> findByCreatedBy(String createdBy);
}
