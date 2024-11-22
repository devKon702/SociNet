package com.example.chatservice.repository;

import com.example.chatservice.entity.RoomActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomActivityRepository extends JpaRepository<RoomActivity, Long> {
    List<RoomActivity> findAllByRoom_Id(Long roomId);
}
