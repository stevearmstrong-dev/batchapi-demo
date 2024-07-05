package com.example.batchapidemo.repository;

import com.example.batchapidemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
