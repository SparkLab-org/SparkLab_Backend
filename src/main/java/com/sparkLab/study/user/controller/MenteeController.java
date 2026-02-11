package com.sparkLab.study.user.controller;

import com.sparkLab.study.user.dto.MenteeActiveLevelResponse;
import com.sparkLab.study.user.dto.MenteeActiveLevelUpdateRequest;
import com.sparkLab.study.user.dto.MenteeRes;
import com.sparkLab.study.user.service.MenteeService;
import com.sparkLab.study.user.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mentees")
@RequiredArgsConstructor
public class MenteeController {

    private final MenteeService menteeService;
    private final MentorService mentorService;

    @PutMapping("/{menteeId}/active-level")
    public ResponseEntity<MenteeActiveLevelResponse> updateActiveLevel(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long menteeId,
            @RequestBody @Valid MenteeActiveLevelUpdateRequest request
    ) {
        Long mentorId = mentorService.accountToUser(jwt.getSubject());
        MenteeActiveLevelResponse response = menteeService.updateActiveLevelByMentor(mentorId, menteeId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public MenteeRes menteeMyPage(@AuthenticationPrincipal Jwt jwt) {
        return menteeService.getMentee(jwt.getSubject());
    }
}
