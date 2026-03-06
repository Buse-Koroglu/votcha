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
    @ResponseStatus(HttpStatus.CREATED)
    public VoteResponseDto createVote(@AuthenticationPrincipal Users currentUser, @Valid @RequestBody VoteRequestDto request){
        return voteService.createVote(request, currentUser.getId());
    }

    // todo - TEST ORTAMI İÇN KALSIN, İLERİDE GÜNCELLENECEK, ŞİMDİLİK TÜM USERLAR GERÇEKLEŞTİREVİLİR AŞAĞIDAKİ METOTLARI
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoteResponseDto getVote(@PathVariable String id){
        return voteService.getVote(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoteResponseDto deleteVote(@PathVariable String id){
        return voteService.deleteVote(id);
    }
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VoteResponseDto updateVote(@PathVariable(name = "id") String id,@Valid @RequestBody VoteRequestDto request){
        return voteService.updateVote(id, request);
    }
}
