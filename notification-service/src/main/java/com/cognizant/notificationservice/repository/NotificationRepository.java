package com.cognizant.notificationservice.repository;

import com.cognizant.notificationservice.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findByUserId(Long userId);

    @Override
    List<Notification> findAll();

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

//    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

}
