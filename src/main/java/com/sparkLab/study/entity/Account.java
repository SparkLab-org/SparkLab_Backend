package com.sparkLab.study.entity;

import com.sparkLab.study.constant.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name="accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseTime {

    @Id
    @Column(name="accountId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "loginId",
            unique = true)
    private String loginId;  // 중복 아이디 불가

    @Column(name = "loginPw",
            unique = true)
    private String loginPw;

    @Column(name = "accountRole")
    private String role;

    @Column(name = "accountDeleted",
            nullable = false)
    private AccountStatus accountStatus;

    @Column(name = "tokenVersion", nullable = false)
    @Builder.Default
    private Long tokenVersion = 0L;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Notification> notifications;
}