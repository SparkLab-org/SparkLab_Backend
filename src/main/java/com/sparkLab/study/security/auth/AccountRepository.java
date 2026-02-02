package com.sparkLab.study.security.auth;

import com.sparkLab.study.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

}
