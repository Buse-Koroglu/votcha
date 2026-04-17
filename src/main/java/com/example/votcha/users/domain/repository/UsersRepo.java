package com.example.votcha.users.domain.repository;

import com.example.votcha.users.domain.model.Role;
import com.example.votcha.users.domain.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    boolean existsByRole(Role role);
}
