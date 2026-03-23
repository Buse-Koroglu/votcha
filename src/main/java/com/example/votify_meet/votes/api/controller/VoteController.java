package com.example.votify_meet.votes.api.controller;

import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.service.VoteService;
import org.springframework.web.bind.annotation.*;

@RestController
public class VoteController implements VoteApi {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @Override
    public VoteResponseDto createVote(Users currentUser, VoteRequestDto request){
        return voteService.createVote(request, currentUser.getId());
    }

    @Override
    public VoteResponseDto getVote(Users user, String id){
        return voteService.getUserVote(user, id);
    }

    @Override
    public VoteResponseDto deleteVote(Users user, String id){
        return voteService.deleteUserVote(user, id);
    }

    @Override
    public VoteResponseDto updateVote(String id, VoteRequestDto request){
        return voteService.updateVote(id, request);
    }
}
