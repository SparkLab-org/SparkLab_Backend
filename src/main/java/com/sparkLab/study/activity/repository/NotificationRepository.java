package com.sparkLab.study.activity.repository;

import com.sparkLab.study.activity.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_AccountIdOrderByCreatedAtDesc(String accountId);

    boolean existsByRecipient_AccountIdAndTypeAndLinkTypeAndLinkIdAndCreatedAtBetween(
            String accountId,
            String type,
            String linkType,
            Long linkId,
            LocalDateTime start,
            LocalDateTime end
    );
}
