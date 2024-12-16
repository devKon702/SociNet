package com.example.userservice.repository;

import com.example.userservice.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.sender.id = :userId1 AND c.receiver.id = :userId2) OR (c.sender.id = :userId2 AND c.receiver.id = :userId1) ORDER BY c.createdAt")
    List<Conversation> getConversations(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("""
        SELECT DISTINCT c.receiver.id 
        FROM Conversation c 
        WHERE c.sender.id = :userId AND NOT EXISTS (SELECT 1 
                                                FROM Friend f 
                                                WHERE (f.isAccepted = true AND (f.sender.id = :userId AND f.receiver.id = c.receiver.id
                                                OR (f.sender.id = c.receiver.id AND f.receiver.id = :userId))))
""")
    List<Long> getChattedStrangeUserId(@Param("userId") Long userId);
}
