package com.sparkLab.study.controller;

import com.sparkLab.study.dto.mentee.MenteeActiveLevelResponse;
import com.sparkLab.study.dto.mentee.MenteeSummaryResponse;
import com.sparkLab.study.dto.mentee.MenteeActiveLevelUpdateRequest;
import com.sparkLab.study.service.MenteeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("mentors")
@RequiredArgsConstructor
public class MenteeController {

    private final MenteeService menteeService;

    @PreAuthorize("hasRole('MENTOR')")
    @PutMapping("/{mentorId}/mentees/{menteeId}/active-level")
    public ResponseEntity<MenteeActiveLevelResponse> updateActiveLevel(
            @PathVariable Long mentorId,
            @PathVariable Long menteeId,
            @RequestBody @Valid MenteeActiveLevelUpdateRequest request
    ) {
        MenteeActiveLevelResponse response = menteeService.updateActiveLevelByMentor(mentorId, menteeId, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MENTOR')")
    @GetMapping("/me/mentees")
    public ResponseEntity<List<MenteeSummaryResponse>> listMenteesByMentor(@AuthenticationPrincipal Jwt jwt) {
        String accountId = jwt.getSubject();
        List<MenteeSummaryResponse> response = menteeService.listMenteesByMentorAccount(accountId);
        return ResponseEntity.ok(response);
    }
}
