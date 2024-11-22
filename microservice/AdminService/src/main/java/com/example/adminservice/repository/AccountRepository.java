package com.example.adminservice.repository;

import com.example.adminservice.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findAccountByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    @Query("SELECT COUNT(a) FROM Account a JOIN a.roles r WHERE a.user.name LIKE %:name% AND r.role = :role")
    long countUserByName(@Param("name") String name, @Param("role") String role);
    @Query("SELECT a FROM Account a JOIN a.roles r WHERE a.user.name LIKE %:name% AND r.role = :role")
    List<Account> findUserByNameContaining(@Param("name") String name,@Param("role") String role, Pageable pageable);
}
