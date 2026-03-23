package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.service.OptionService;
import com.example.votify_meet.users.domain.model.Users;
import org.springframework.web.bind.annotation.*;

@RestController
public class OptionController implements OptionApi{

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }


    @Override
    public OptionResponseDto createOption(Users currentUser, OptionRequestDto request, String eventId){
        return optionService.createOption(request, eventId, currentUser.getId());
    }


    @Override
    public OptionResponseDto getOption(Users user, String id){
        return optionService.getUserOption(user, id);
    }

    @Override
    public OptionResponseDto deleteOption(Users user,  String id){
        return optionService.deleteUserOption(user, id);
    }

    @Override
    public OptionResponseDto updateOption(Users user, UpdateOptionRequestDto request, String id){
        return optionService.patchUserOption(user, id, request);
    }
}
