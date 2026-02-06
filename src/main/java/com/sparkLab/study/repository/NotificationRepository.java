package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_AccountIdOrderByCreatedAtDesc(Long accountId);

    boolean existsByRecipient_AccountIdAndTypeAndLinkTypeAndLinkIdAndCreatedAtBetween(
            Long accountId,
            String type,
            String linkType,
            Long linkId,
            LocalDateTime start,
            LocalDateTime end
    );
}
