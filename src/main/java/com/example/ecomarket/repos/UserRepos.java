package com.example.ecomarket.repos;

import com.example.ecomarket.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepos extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
