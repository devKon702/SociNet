package com.example.socinet.repository;

import com.example.socinet.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findAccountByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    long countByUser_NameContainingIgnoreCase(String name);
    List<Account> findByUser_NameContainingIgnoreCase(String name, Pageable pageable);
}
