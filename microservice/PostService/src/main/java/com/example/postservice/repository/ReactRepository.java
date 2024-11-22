package com.example.postservice.repository;

import com.example.postservice.entity.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactRepository extends JpaRepository<React, String> {
}
