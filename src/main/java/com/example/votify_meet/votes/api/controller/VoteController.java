package com.example.votify_meet.votes.api.controller;

import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteResponseDto> getVote(@PathVariable String id){
        return ResponseEntity.ok(voteService.getVote(id));
    }

    @PostMapping("")
    public ResponseEntity<VoteResponseDto> createVote(@Valid @RequestBody VoteRequestDto request){
        return ResponseEntity.ok(voteService.createVote(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VoteResponseDto> deleteVote(@PathVariable String id){
        return ResponseEntity.ok(voteService.deleteVote(id));
    }
}
