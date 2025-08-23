package com.myproject.todo_management.service.impl;

import com.myproject.todo_management.dto.PagedResponse;
import com.myproject.todo_management.dto.TodoDto;
import com.myproject.todo_management.entity.Todo;
import com.myproject.todo_management.entity.User;
import com.myproject.todo_management.exception.ResourceNotFoundException;
import com.myproject.todo_management.exception.TodoAPIException;
import com.myproject.todo_management.respository.TodoRepository;
import com.myproject.todo_management.respository.UserRepository;
import com.myproject.todo_management.service.TodoService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private TodoRepository todoRepository;

    private ModelMapper modelMapper;

    private UserRepository userRepository;

    //  Helper untuk ambil data user yang sedang login
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getCurrentUsername() {
        return getAuthentication().getName();
    }

    private boolean isAdmin() {
        return getAuthentication().getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
    }

    //  Helper untuk ambil todo dan cek akses
    private Todo getTodoWithAccessCheck(Long id, String action) {
        String currentUsername = getCurrentUsername();
        boolean admin = isAdmin();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id : " + id));

        if (!admin && !todo.getCreatedBy().equals(currentUsername)) {
            throw new TodoAPIException(HttpStatus.FORBIDDEN,
                    "you dont have access to " + action + " another user data");
        }
        return todo;
    }

    @Override
    public TodoDto addTodo(TodoDto todoDto) {
        // Ambil user yang sedang login
        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Cek apakah title sudah ada untuk user ini
        boolean exists = todoRepository.existsByTitleAndUser(todoDto.getTitle(), user);
        if (exists) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Title already exists");
        }

        // Map TodoDto ke Todo entity
        Todo todo = modelMapper.map(todoDto, Todo.class);

        todo.setUser(user); // set user ke todo
        todo.setCreatedBy(username); // siapa yang buat

        // Hibernate otomatis set timeCreated & timeUpdated
        Todo savedTodo = todoRepository.save(todo);

        // Mapping kembali ke DTO untuk response
        return modelMapper.map(savedTodo, TodoDto.class);
    }

    @Override
    public TodoDto getTodo(Long id) {
        Todo todo = getTodoWithAccessCheck(id, "view");
        return modelMapper.map(todo, TodoDto.class);
    }

    @Override
    public PagedResponse<TodoDto> getAllTodos(Pageable pageable) {
        String currentUsername = getCurrentUsername();

        Page<Todo> todos = isAdmin()
                ? todoRepository.findAll(pageable)
                : todoRepository.findByCreatedBy(currentUsername, pageable);

        List<TodoDto> content = todos.getContent()
                .stream()
                .map(todo -> modelMapper.map(todo, TodoDto.class))
                .toList();

        return new PagedResponse<>(
                content,
                todos.getNumber(),
                todos.getSize(),
                todos.getTotalElements(),
                todos.getTotalPages()
        );
    }

    @Override
    public TodoDto updateTodo(TodoDto todoDto, Long id) {
        Todo todo = getTodoWithAccessCheck(id, "update");

        // update fields
       todo.setTitle(todoDto.getTitle());
       todo.setDescription(todoDto.getDescription());
       todo.setCompleted(todoDto.isCompleted());
       todo.setUpdatedBy(getCurrentUsername()); // siapa yang terakhir update

       Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public void deleteTodo(Long id) {

        Todo todo = getTodoWithAccessCheck(id, "delete");
        todoRepository.delete(todo);

    }

    @Override
    public TodoDto completedTodo(Long id) {
        Todo todo = getTodoWithAccessCheck(id, "completed");

        todo.setCompleted(Boolean.TRUE);
        todo.setUpdatedBy(getCurrentUsername());

        return modelMapper.map(todoRepository.save(todo), TodoDto.class);
    }

    @Override
    public TodoDto inCompleteTodo(Long id) {
        Todo todo = getTodoWithAccessCheck(id, "incompleted");
        todo.setCompleted(Boolean.FALSE);
        todo.setUpdatedBy(getCurrentUsername());
        return modelMapper.map(todoRepository.save(todo), TodoDto.class);
    }
}
