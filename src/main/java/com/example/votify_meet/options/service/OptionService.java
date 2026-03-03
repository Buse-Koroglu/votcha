package com.example.votify_meet.options.service;

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
import org.springframework.stereotype.Service;

@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;
    private final EventsRepo eventsRepo;

    public OptionService(OptionRepository repository, OptionMapper mapper, EventsRepo eventsRepo) {
        this.optionRepository = repository;
        this.optionMapper = mapper;
        this.eventsRepo = eventsRepo;
    }

    public OptionResponseDto getOption(String id){
        return optionMapper.toResponse(optionRepository.findById(id).orElseThrow( () -> new OptionNotFoundException(String.format("Option with id %s not found", id))));
    }

    public OptionResponseDto createOption(OptionRequestDto request, String eventId, String creatorId){
        Event event = eventsRepo.findById(eventId).orElseThrow(() -> new EventNotFoundException(String.format("Event with id %s not found", eventId)));
        if(!event.getCreator().getId().equals(creatorId)){
            throw new UnauthorizedException("Only the owner can add style to the event.");
        }
        return optionMapper.toResponse(optionRepository.saveAndFlush(optionMapper.toEntity(request, event)));
    }

    public OptionResponseDto deleteOption(String id){
        Option option = optionRepository.findById(id).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));
        optionRepository.delete(option);
        return optionMapper.toResponse(option);
    }

    public OptionResponseDto patchOption(String id, UpdateOptionRequestDto request){
        Option option = optionRepository.findById(id).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));
        optionMapper.update(request,option);
        return optionMapper.toResponse(optionRepository.save(option));
    }


}
