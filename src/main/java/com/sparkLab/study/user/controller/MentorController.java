package com.sparkLab.study.user.controller;

import com.sparkLab.study.user.dto.MenteeActiveLevelResponse;
import com.sparkLab.study.user.dto.MenteeActiveLevelUpdateRequest;
import com.sparkLab.study.user.dto.MenteeSummaryResponse;
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
    private final MenteeService menteeService;


    @PreAuthorize("hasRole('MENTOR')")
    @GetMapping("/me/mentees")
    public ResponseEntity<List<MenteeSummaryResponse>> listMenteesByMentor(@AuthenticationPrincipal Jwt jwt) {
        Long mentorId = mentorService.accountToUser(jwt.getSubject());
        List<MenteeSummaryResponse> response = menteeService.listMenteesByMentorAccount(mentorId);
        return ResponseEntity.ok(response);
    }

}
