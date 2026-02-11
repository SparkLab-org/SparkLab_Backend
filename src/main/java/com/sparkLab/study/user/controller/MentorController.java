package com.sparkLab.study.user.controller;

import com.sparkLab.study.user.dto.*;
import com.sparkLab.study.user.service.MenteeService;
import com.sparkLab.study.user.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @GetMapping("/me")
    public MentorRes getMentor(@AuthenticationPrincipal Jwt jwt) {
        return mentorService.getMentor(jwt.getSubject());
    }
}
