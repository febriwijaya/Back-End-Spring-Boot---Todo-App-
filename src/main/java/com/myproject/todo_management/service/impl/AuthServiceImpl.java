package com.myproject.todo_management.service.impl;

import com.myproject.todo_management.dto.JwtAuthResponse;
import com.myproject.todo_management.dto.LoginDto;
import com.myproject.todo_management.dto.RegisterDto;
import com.myproject.todo_management.dto.TodoDto;
import com.myproject.todo_management.entity.Role;
import com.myproject.todo_management.entity.User;
import com.myproject.todo_management.exception.TodoAPIException;
import com.myproject.todo_management.respository.RoleRepository;
import com.myproject.todo_management.respository.UserRepository;
import com.myproject.todo_management.security.JwtTokenProvider;
import com.myproject.todo_management.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.modelmapper.ModelMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    // tempat penyimpanan foto
    private final String UPLOAD_DIR = "uploads/photos/";

    @Override
    public List<RegisterDto> getAllRegister() {

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, RegisterDto.class))
                .collect(Collectors.toList());

    }

    @Override
    public RegisterDto getUserById(Long id)  {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new TodoAPIException(HttpStatus.NOT_FOUND, "User not found with id " + id));
        return modelMapper.map(user, RegisterDto.class);
    }

    @Override
    public RegisterDto getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new TodoAPIException(HttpStatus.NOT_FOUND, "User not found with username or email " + usernameOrEmail));
        return modelMapper.map(user, RegisterDto.class);
    }

    private String savePhoto(MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) return null;

        String contentType = photo.getContentType();
        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg") ||
                        contentType.equalsIgnoreCase("image/jpg") ||
                        contentType.equalsIgnoreCase("image/png"))) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Hanya file JPG/PNG yang diperbolehkan");
        }

        if (photo.getSize() > 2 * 1024 * 1024) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Ukuran file maksimum adalah 2MB");
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();


        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String fileName = UUID.randomUUID() + extension;

        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.write(filePath, photo.getBytes());

        return "/uploads/photos/" + fileName; // gunakan path yang bisa diakses via browser

//        String extension = contentType.contains("png") ? ".png" : ".jpg";
//        String fileName = UUID.randomUUID() + extension;
//
//        Path filePath = Paths.get(UPLOAD_DIR, fileName);
//        Files.write(filePath, photo.getBytes());
//
//        return "/uploads/photos/" + fileName;
    }

    private void deletePhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                // photoPath contoh: "/uploads/photos/abc.jpg"
                String fileName = Paths.get(photoPath).getFileName().toString();
                File file = Paths.get(UPLOAD_DIR, fileName).toFile();

                if (file.exists()) {
                    if (file.delete()) {
                        logger.info("Photo deleted: {}", file.getAbsolutePath());
                    } else {
                        logger.warn("Failed to delete photo: {}", file.getAbsolutePath());
                    }
                } else {
                    logger.debug("Photo file not found: {}", file.getAbsolutePath());
                }
            } catch (Exception ex) {
                logger.error("Error while deleting photo: {}", photoPath, ex);
            }
        }
    }

    @Override
    @Transactional
    public String register(RegisterDto registerDto, MultipartFile photo) throws IOException {

        // check username is already exist in database
        if(userRepository.existsByUsername(registerDto.getUsername())) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        // check email is already exist in database
        if(userRepository.existsByEmail(registerDto.getEmail())) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        // Mapping DTO ke Entity
        User user = modelMapper.map(registerDto, User.class);

        String photoPath = savePhoto(photo);

//        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Set field tambahan
        user.setBirthDate(registerDto.getBirthDate());
        user.setJobTitle(registerDto.getJobTitle());
        user.setLocation(registerDto.getLocation());
        user.setProfilePhoto(photoPath);

        // Timestamp otomatis
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setCreatedBy(registerDto.getUsername());


        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER");
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);

        return "User Registered Successfully";
    }

    @Override
    @Transactional
    public String updateRegister(Long userId, RegisterDto registerDto, MultipartFile photo) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TodoAPIException(HttpStatus.NOT_FOUND, "User not found"));

        // Cek duplikasi username (pastikan bukan user yang sedang update)
        Optional<User> existingByUsername = userRepository.findByUsername(registerDto.getUsername());
        if (existingByUsername.isPresent() && !existingByUsername.get().getId().equals(user.getId())) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Username already taken");
        }

        // Cek duplikasi email (pastikan bukan user yang sedang update)
        Optional<User> existingByEmail = userRepository.findByEmail(registerDto.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(user.getId())) {
            throw new TodoAPIException(HttpStatus.BAD_REQUEST, "Email already taken");
        }

        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        if (registerDto.getPassword() != null && !registerDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        }
        user.setBirthDate(registerDto.getBirthDate());
        user.setJobTitle(registerDto.getJobTitle());
        user.setLocation(registerDto.getLocation());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(registerDto.getUsername());

//        if(photo != null && !photo.isEmpty()) {
            //hapus foto lama
            deletePhoto(user.getProfilePhoto());
            //simpan foto baru
            String newPhotoPath = savePhoto(photo);
            user.setProfilePhoto(newPhotoPath);
//        }

        userRepository.save(user);

        return "User updated successfully";
    }

    @Override
    @Transactional
    public String deleteRegister(Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TodoAPIException(HttpStatus.NOT_FOUND, "User not found"));

        // hapus foto dari folder
        deletePhoto(user.getProfilePhoto());

        userRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(
                loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail()
        );

        if (userOptional.isEmpty()) {
            throw new TodoAPIException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new TodoAPIException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsernameOrEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);


        // Saat login, update updatedAt dan updatedBy otomatis
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(authentication.getName()); // ambil username user yang login
        userRepository.save(user);

        // Ambil role
        String role = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse(null);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole(role);
        jwtAuthResponse.setAccessToken(token);
        jwtAuthResponse.setUsernameOrEmail(loginDto.getUsernameOrEmail());

        return jwtAuthResponse;

    }
}
