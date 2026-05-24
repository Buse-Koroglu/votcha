package com.example.votcha.votes.api.controller;

import com.example.votcha.users.domain.model.Users;
import com.example.votcha.votes.api.dto.VoteRequestDto;
import com.example.votcha.votes.api.dto.VoteResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/votes")
@Tag(name = "Votes", description = "Vote Management APIs")
public interface VoteApi {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    VoteResponseDto createVote(@AuthenticationPrincipal Users currentUser, @Valid @RequestBody VoteRequestDto request);

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    VoteResponseDto getVote(@AuthenticationPrincipal Users user, @PathVariable String id);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    VoteResponseDto deleteVote(@AuthenticationPrincipal Users user, @PathVariable String id);

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    VoteResponseDto updateVote(@AuthenticationPrincipal Users user, @PathVariable(name = "id") String id,@Valid @RequestBody VoteRequestDto request);

    @GetMapping("/events/{id}/my-vote")
    VoteResponseDto getMyVote(@AuthenticationPrincipal Users user, @PathVariable String id);
}
