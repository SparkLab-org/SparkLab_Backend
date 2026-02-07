package com.sparkLab.study.security.auth.entity;

import com.sparkLab.study.entity.BaseTime;
import com.sparkLab.study.entity.Notification;
import com.sparkLab.study.security.auth.constant.AccountRole;
import com.sparkLab.study.security.auth.constant.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name="accounts")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTime {

    @Id
    @Column(name = "accountId")
    private String accountId;  // 중복 아이디 불가

    @Column(name = "password")
    private String password;

    @Column(name = "accountRole")
    @Enumerated(EnumType.STRING)
    private AccountRole role;

    @Column(name = "accountStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column(name = "tokenVersion", nullable = false)
    @Builder.Default
    private Long tokenVersion = 0L;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Notification> notifications;
}