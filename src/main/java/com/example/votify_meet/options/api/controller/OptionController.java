package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.options.service.OptionService;
import com.example.votify_meet.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Options", description = "Option Management APIs")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping("/events/{id}/options")
    public ResponseEntity<OptionResponseDto> createOption(@AuthenticationPrincipal Users currentUser, @Valid @RequestBody OptionRequestDto request, @PathVariable(name = "id") String eventId){
        return ResponseEntity.status(HttpStatus.CREATED).body(optionService.createOption(request, eventId, currentUser.getId()));
    }

    // todo - TEST ORTAMI İÇİN BUNLAR İLERİDE GÜNCELLENECEK, HER USER KENDİ İŞİNİ KENDİ GÖRECEK

    @GetMapping("/options/{id}")
    public ResponseEntity<OptionResponseDto> getOption(@PathVariable String id){
        return ResponseEntity.ok(optionService.getOption(id));
    }

    @DeleteMapping("/options/{id}")
    public ResponseEntity<OptionResponseDto> deleteOption(@PathVariable String id){
        return ResponseEntity.ok(optionService.deleteOption(id));
    }

    @PatchMapping("/options/{id}")
    public ResponseEntity<OptionResponseDto> updateOption(@Valid @RequestBody UpdateOptionRequestDto request, @PathVariable(name = "id") String id){
        return ResponseEntity.ok(optionService.patchOption(id, request));
    }


}
