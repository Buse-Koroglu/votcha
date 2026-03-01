package com.example.votify_meet.votes.api.controller;

import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@Tag(name = "Votes", description = "Vote Management APIs")
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("")
    public ResponseEntity<VoteResponseDto> createVote(@Valid @RequestBody VoteRequestDto request){
        return ResponseEntity.ok(voteService.createVote(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<VoteResponseDto> getVote(@PathVariable String id){
        return ResponseEntity.ok(voteService.getVote(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VoteResponseDto> deleteVote(@PathVariable String id){
        return ResponseEntity.ok(voteService.deleteVote(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<VoteResponseDto> updateVote(@PathVariable(name = "id") String id, @RequestBody VoteRequestDto request){
        return ResponseEntity.ok(voteService.updateVote(id, request));
    }
}
