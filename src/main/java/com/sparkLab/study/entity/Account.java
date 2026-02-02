package com.sparkLab.study.entity;

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
    private String deletedYn;

    @Column(name = "tokenVersion", nullable = false)
    private Long tokenVersion = 0L;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @PrePersist
    public void prePersist() {
        if (this.deletedYn == null)
            this.deletedYn = "N";

        if (this.role == null)
            this.role = "USER";
    }
}