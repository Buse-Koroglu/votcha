package com.example.votify_meet.votes.api.controller;

import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.service.VoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<VoteResponseDto> createVote(@AuthenticationPrincipal Users currentUser, @Valid @RequestBody VoteRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.createVote(request, currentUser.getId()));
    }

    // todo - TEST ORTAMI İÇN KALSIN, İLERİDE GÜNCELLENECEK, ŞİMDİLİK TÜM USERLAR GERÇEKLEŞTİREVİLİR AŞAĞIDAKİ METOTLARI
    @GetMapping("/{id}")
    public ResponseEntity<VoteResponseDto> getVote(@PathVariable String id){
        return ResponseEntity.ok(voteService.getVote(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VoteResponseDto> deleteVote(@PathVariable String id){
        return ResponseEntity.ok(voteService.deleteVote(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<VoteResponseDto> updateVote(@PathVariable(name = "id") String id,@Valid @RequestBody VoteRequestDto request){
        return ResponseEntity.ok(voteService.updateVote(id, request));
    }
}
