package com.group4.repository;

import com.group4.entity.ChatEntity;
import com.group4.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    // Truy vấn các senderId đã nhắn tin cho một receiverId cụ thể
    @Query("SELECT DISTINCT u " +
            "FROM ChatEntity c " +
            "JOIN UserEntity u ON c.senderID = u.userID " +
            "WHERE c.receiverID = 1")
    List<UserEntity> findDistinctSendersByReceiverId();



}

