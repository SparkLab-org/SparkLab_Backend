package com.sparkLab.study.controller;

import com.sparkLab.study.dto.notification.NotificationResponse;
import com.sparkLab.study.service.NotificationService;
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
    public List<NotificationResponse> list(@RequestParam Long accountId) {
        return notificationService.listByAccount(accountId);
    }
}
