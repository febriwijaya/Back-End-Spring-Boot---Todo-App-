package com.myproject.todo_management.security;

import com.myproject.todo_management.entity.User;
import com.myproject.todo_management.respository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(() -> new UsernameNotFoundException("User not exists by username or Email"));

//      Role dari user (misalnya "ADMIN", "USER") diubah menjadi GrantedAuthority.
//      Ini agar Spring Security tahu apa saja hak akses user ini.
        Set<GrantedAuthority> authorities = user.getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usernameOrEmail,
                user.getPassword(),
                authorities
        );
    }
}
