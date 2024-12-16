package com.example.userservice.repository;

import com.example.userservice.entity.Friend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f WHERE (f.sender.id = :id OR f.receiver.id = :id) AND f.isAccepted = true")
    List<Friend> findAllFriendsByUserId(@Param("id") Long id);

    @Query("SELECT f FROM Friend f WHERE f.receiver.id = :id AND f.isAccepted = false")
    List<Friend> findInvitations(@Param("id") Long id);

    @Query("SELECT f FROM Friend f WHERE (f.sender.id = :id1 AND f.receiver.id = :id2) OR (f.sender.id = :id2 AND f.receiver.id = :id1)")
    Optional<Friend> findInvitation(@Param("id1") Long id1, @Param("id2") Long id2);

    @Query("SELECT f FROM Friend f WHERE (f.sender.id = :id OR f.receiver.id = :id) AND f.isAccepted = true ORDER BY FUNCTION('RAND')" )
    List<Friend> findRandomFriend(@Param("id") Long id, Pageable pageable);
}
