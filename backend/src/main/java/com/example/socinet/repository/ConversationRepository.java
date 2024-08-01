package com.example.socinet.repository;

import com.example.socinet.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.sender.id = :userId1 AND c.receiver.id = :userId2) OR (c.sender.id = :userId2 AND c.receiver.id = :userId1)")
    List<Conversation> getConversations(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

}
