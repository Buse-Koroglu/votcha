package com.example.votify_meet.users.domain.repository;

import com.example.votify_meet.users.domain.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<Users, String> {
    Users findByEmail(String email);
}
