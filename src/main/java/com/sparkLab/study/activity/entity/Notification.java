package com.sparkLab.study.activity.entity;

import com.sparkLab.study.activity.constant.NotificationLinkType;
import com.sparkLab.study.security.auth.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account recipient;

    private String type;
    private String title;
    @Enumerated(EnumType.STRING)
    private NotificationLinkType linkType;
    private Long linkId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
