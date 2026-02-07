package com.sparkLab.study.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;
    private String type;
    private String title;
    private String linkType;
    private Long linkId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
