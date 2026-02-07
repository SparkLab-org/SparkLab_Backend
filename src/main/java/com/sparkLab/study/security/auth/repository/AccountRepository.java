package com.sparkLab.study.security.auth.repository;

import com.sparkLab.study.security.auth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
