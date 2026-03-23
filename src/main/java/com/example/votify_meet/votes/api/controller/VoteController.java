package com.example.votify_meet.votes.api.controller;

import com.example.votify_meet.common.logging.BusinessAction;
import com.example.votify_meet.users.domain.model.Users;
import com.example.votify_meet.votes.api.dto.VoteRequestDto;
import com.example.votify_meet.votes.api.dto.VoteResponseDto;
import com.example.votify_meet.votes.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VoteController implements VoteApi {
    private final VoteService voteService;

    @BusinessAction(action = "VOTE_CAST", domain = "VOTES", logDetails = "'Option ID: '+ #request.optionId")
    @Override
    public VoteResponseDto createVote(Users user, VoteRequestDto request){ return voteService.createVote(request, user.getId()); }

    @Override
    public VoteResponseDto getVote(Users user, String id){
        return voteService.getUserVote(user, id);
    }

    @BusinessAction(action = "VOTE_DELETED", domain = "VOTES", logDetails = "'Vote ID: '+ #id")
    @Override
    public VoteResponseDto deleteVote(Users user, String id){
        return voteService.deleteUserVote(user, id);
    }

    @BusinessAction(action = "VOTE_UPDATED", domain = "VOTES", logDetails = "'Vote ID: '+ #id + ' | New Option Id: ' + #request.optionId")
    @Override
    public VoteResponseDto updateVote(Users user, String id, VoteRequestDto request){ return voteService.updateVote(user, id, request); }
}
