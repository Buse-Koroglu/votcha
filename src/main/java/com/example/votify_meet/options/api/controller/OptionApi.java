package com.example.votify_meet.options.api.controller;

import com.example.votify_meet.options.api.dto.OptionRequestDto;
import com.example.votify_meet.options.api.dto.OptionResponseDto;
import com.example.votify_meet.options.api.dto.UpdateOptionRequestDto;
import com.example.votify_meet.users.domain.model.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@Tag(name = "Options", description = "Option Management APIs")
public interface OptionApi {

    @PostMapping("/events/{id}/options")
    @ResponseStatus(HttpStatus.CREATED)
    OptionResponseDto createOption(@AuthenticationPrincipal Users currentUser,
                                   @Valid @RequestBody OptionRequestDto request,
                                   @PathVariable(name = "id") String eventId);

    @GetMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    OptionResponseDto getOption(@AuthenticationPrincipal Users user,
                                @PathVariable String id);

    @DeleteMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    OptionResponseDto deleteOption(@AuthenticationPrincipal Users user,
                                   @PathVariable String id);

    @PatchMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    OptionResponseDto updateOption(@AuthenticationPrincipal Users user,
                                   @Valid @RequestBody UpdateOptionRequestDto request,
                                   @PathVariable(name = "id") String id);
}
