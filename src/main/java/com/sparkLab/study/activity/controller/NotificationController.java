package com.sparkLab.study.activity.controller;

import com.sparkLab.study.activity.service.NotificationService;
import com.sparkLab.study.activity.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasAnyRole('MENTEE','MENTOR')")
    @GetMapping
    public List<NotificationResponse> list(@RequestParam String accountId) {
        return notificationService.listByAccount(accountId);
    }
}
