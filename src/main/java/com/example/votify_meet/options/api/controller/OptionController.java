package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.service.OptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/options")
@Tag(name = "Options", description = "Option Management APIs")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionResponseDto> getOption(@PathVariable String id){
        return ResponseEntity.ok(optionService.getOption(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<OptionResponseDto> createOption(@Valid @RequestBody OptionRequestDto request, @PathVariable(name = "id") String id){
        return ResponseEntity.ok(optionService.createOption(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptionResponseDto> deleteOption(@PathVariable String id){
        return ResponseEntity.ok(optionService.deleteOption(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OptionResponseDto> updateOption(@Valid @RequestBody UpdateOptionRequestDto request, @PathVariable(name = "id") String id){
        return ResponseEntity.ok(optionService.patchOption(id, request));
    }


}
