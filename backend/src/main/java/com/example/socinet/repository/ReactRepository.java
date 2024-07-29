package com.example.socinet.repository;

import com.example.socinet.entity.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactRepository extends JpaRepository<React, String> {
}
