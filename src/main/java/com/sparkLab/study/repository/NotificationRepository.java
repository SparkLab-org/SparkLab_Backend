package com.sparkLab.study.repository;

import com.sparkLab.study.entity.Notification;
import com.sparkLab.study.constant.NotificationLinkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_AccountIdOrderByCreatedAtDesc(String accountId);

    boolean existsByRecipient_AccountIdAndTypeAndLinkTypeAndLinkIdAndCreatedAtBetween(
            String accountId,
            String type,
            NotificationLinkType linkType,
            Long linkId,
            LocalDateTime start,
            LocalDateTime end
    );
}
