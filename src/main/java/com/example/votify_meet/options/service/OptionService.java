package com.example.votify_meet.options.service;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.mapper.OptionMapper;
import com.example.votify_meet.options.domain.exception.OptionNotFoundException;
import com.example.votify_meet.options.domain.model.Option;
import com.example.votify_meet.options.domain.repository.OptionRepository;
import org.springframework.stereotype.Service;

@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    public OptionService(OptionRepository repository, OptionMapper mapper) {
        this.optionRepository = repository;
        this.optionMapper = mapper;
    }

    public OptionResponseDto getOption(String id){
        return optionMapper.toResponse(optionRepository.findById(id).orElseThrow( () -> new OptionNotFoundException(String.format("Option with id %s not found", id))));
    }

    public OptionResponseDto createOption(OptionRequestDto request){
        return optionMapper.toResponse(optionRepository.saveAndFlush(optionMapper.toEntity(request)));
    }

    public OptionResponseDto deleteOption(String id){
        Option option = optionRepository.findById(id).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));
        optionRepository.delete(option);
        return optionMapper.toResponse(option);
    }

    public OptionResponseDto patchOption(String id, OptionRequestDto request){
        Option option = optionRepository.findById(id).orElseThrow(() -> new OptionNotFoundException(String.format("Option with id %s not found", id)));
        optionMapper.update(request,option);
        return optionMapper.toResponse(optionRepository.save(option));
    }


}
