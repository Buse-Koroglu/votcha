package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.common.logging.BusinessAction;
import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.service.OptionService;
import com.example.votify_meet.users.domain.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OptionController implements OptionApi{

    private final OptionService optionService;

    @BusinessAction(action = "OPTION_ADDED", domain = "OPTIONS", logDetails = "'Event ID: ' + #eventId + ' | Content: ' + #request.content")
    @Override
    public OptionResponseDto createOption(Users currentUser, OptionRequestDto request, String eventId){return optionService.createOption(request, eventId, currentUser.getId());}


    @Override
    public OptionResponseDto getOption(Users user, String id){
        return optionService.getUserOption(user, id);
    }

    @BusinessAction(action = "OPTION_DELETED", domain = "OPTIONS", logDetails = "'Option ID: ' + #id")
    @Override
    public OptionResponseDto deleteOption(Users user,  String id){
        return optionService.deleteUserOption(user, id);
    }

    @BusinessAction(action = "OPTION_UPDATED", domain = "OPTIONS", logDetails = "'Option ID: ' + #id")
    @Override
    public OptionResponseDto updateOption(Users user, UpdateOptionRequestDto request, String id){return optionService.patchUserOption(user, id, request);}
}
