package com.myproject.todo_management.service.impl;

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
import org.springframework.http.HttpStatus;
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

    @Override
    public TodoDto addTodo(TodoDto todoDto) {
        // Ambil user yang sedang login
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

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
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id : " + id));

        return modelMapper.map(todo, TodoDto.class);
    }

    @Override
    public List<TodoDto> getAllTodos() {

        List<Todo> todos = todoRepository.findAll();

        return todos.stream().map((todo) -> modelMapper.map(todo, TodoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TodoDto updateTodo(TodoDto todoDto, Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

       Todo todo =  todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id : " + id));

       todo.setTitle(todoDto.getTitle());
       todo.setDescription(todoDto.getDescription());
       todo.setCompleted(todoDto.isCompleted());
       todo.setUpdatedBy(username); // siapa yang terakhir update

       Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public void deleteTodo(Long id) {

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id : " + id));

        todoRepository.deleteById(id);

    }

    @Override
    public TodoDto completedTodo(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id : " + id));

        todo.setCompleted(Boolean.TRUE);
        todo.setUpdatedBy(username);

        Todo updatedTodo = todoRepository.save(todo);
        return modelMapper.map(updatedTodo, TodoDto.class);
    }

    @Override
    public TodoDto inCompleteTodo(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id : " + id));

        todo.setCompleted(Boolean.FALSE);

        Todo updatedTodo = todoRepository.save(todo);
        todo.setUpdatedBy(username);

        return modelMapper.map(updatedTodo, TodoDto.class);
    }
}
