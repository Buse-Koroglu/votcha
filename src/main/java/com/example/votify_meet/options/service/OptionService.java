package com.example.votify_meet.options.service;

import com.example.votify_meet.common.exception.AppAccessDeniedException;
import com.example.votify_meet.events.domain.exception.EventNotFoundException;
import com.example.votify_meet.events.domain.model.Event;
import com.example.votify_meet.events.domain.repository.EventsRepo;
import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.api.mapper.OptionMapper;
import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.domain.exception.UnauthorizedException;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import com.example.votify_meet.users.domain.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final EventsRepo eventsRepository;
    private final OptionMapper optionMapper;


    public OptionResponseDto getUserOption(Users user, String optionId){
        return optionMapper.toResponse(getOwnedOption(user, optionId));

    }

    public OptionResponseDto createOption(OptionRequestDto request, String eventId, String creatorId){
        Event event = eventsRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        if(!event.getCreator().getId().equals(creatorId)){
            throw new UnauthorizedException("Only the owner can add option to the event.");
        }
        return optionMapper.toResponse(optionRepository.saveAndFlush(optionMapper.toEntity(request, event)));
    }

    public OptionResponseDto deleteUserOption(Users user, String id){
        Option option = getOwnedOption(user, id);
        optionRepository.delete(option);
        return optionMapper.toResponse(option);
    }

    public OptionResponseDto patchUserOption(Users user, String id, UpdateOptionRequestDto request){
        Option option = getOwnedOption(user, id);
        optionMapper.update(request,option);
        return optionMapper.toResponse(optionRepository.save(option));
    }


    private Option getOwnedOption(Users user, String optionId){
        Option option = optionRepository.findById(optionId).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", optionId)));

        if(!option.getUserId().equals(user.getId())){
            throw new AppAccessDeniedException("You are not allowed to access this option");
        }
        return option;
    }
}
