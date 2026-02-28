package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionResponseDto> getOption(@PathVariable String id){
        return ResponseEntity.ok(optionService.getOption(id));
    }

    @PostMapping("")
    public ResponseEntity<OptionResponseDto> createOption(@Valid @RequestBody OptionRequestDto request){
        return ResponseEntity.ok(optionService.createOption(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptionResponseDto> deleteOption(@PathVariable String id){
        return ResponseEntity.ok(optionService.deleteOption(id));
    }

    // for update will be added


}
