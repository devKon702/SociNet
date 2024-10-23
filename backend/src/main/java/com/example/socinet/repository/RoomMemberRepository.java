package com.example.socinet.repository;

import com.example.socinet.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    List<RoomMember> findAllByUser_Id(Long id);
    boolean existsByUser_IdAndRoom_IdAndIsAdminTrue(Long user_id, Long room_id);
    boolean existsByUser_IdAndRoom_Id(Long userId, Long roomId);
    List<RoomMember> findAllByRoom_Id(Long roomId);
    Optional<RoomMember> findByUser_IdAndRoom_Id(Long userId, Long roomId);
}
