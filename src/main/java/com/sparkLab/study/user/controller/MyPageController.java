package com.sparkLab.study.user.controller;

import com.sparkLab.study.user.dto.MenteeMyPageRes;
import com.sparkLab.study.user.dto.MentorMyPageRes;
import com.sparkLab.study.user.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/mentee")
    public MenteeMyPageRes menteeMyPage(@AuthenticationPrincipal Jwt jwt) {
        return myPageService.getMenteeMyPage(jwt.getSubject());
    }

    @GetMapping("/mentor")
    public MentorMyPageRes mentorMyPage(@AuthenticationPrincipal Jwt jwt) {
        return myPageService.getMentorMyPage(jwt.getSubject());
    }
}
