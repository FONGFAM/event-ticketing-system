package com.eventticket.notificationanalytics.notification.repository;

import com.eventticket.notificationanalytics.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
}
