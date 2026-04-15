package com.example.votcha.options.service;

import com.example.votcha.common.exception.AppAccessDeniedException;
import com.example.votcha.events.domain.exception.EventNotFoundException;
import com.example.votcha.events.domain.model.Event;
import com.example.votcha.events.domain.repository.EventsRepo;
import com.example.votcha.options.api.dto.OptionRequestDto;
import com.example.votcha.options.api.dto.OptionResponseDto;
import com.example.votcha.options.api.dto.UpdateOptionRequestDto;
import com.example.votcha.options.api.mapper.OptionMapper;
import com.example.votcha.options.domain.exception.MinimumOptionsException;
import com.example.votcha.options.domain.exception.OptionNotFoundException;
import com.example.votcha.options.domain.exception.UnauthorizedException;
import com.example.votcha.options.domain.model.Option;
import com.example.votcha.options.domain.repository.OptionRepository;
import com.example.votcha.users.domain.model.Users;
import jakarta.transaction.Transactional;
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
    @Transactional
    public OptionResponseDto deleteUserOption(Users user, String id){
        Option option = getOwnedOption(user, id);
        Event event = option.getEvent();

        long optionCount = optionRepository.countByEventId(event.getId());

        if(optionCount <= 2){
            throw new MinimumOptionsException("An Event must maintain at least 2 options.");
        }
        optionRepository.delete(option);
        return optionMapper.toResponse(option);
    }

    @Transactional
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
